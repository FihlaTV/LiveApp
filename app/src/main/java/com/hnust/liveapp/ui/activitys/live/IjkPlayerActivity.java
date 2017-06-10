package com.hnust.liveapp.ui.activitys.live;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dl7.player.danmaku.OnDanmakuListener;
import com.dl7.player.media.IjkPlayerView;
import com.hnust.liveapp.App;
import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.GsonOpenFj;
import com.hnust.liveapp.bean.Message;
import com.hnust.liveapp.bean.User;
import com.hnust.liveapp.contract.RoomContract;
import com.hnust.liveapp.contract.UserContract;
import com.hnust.liveapp.model.UserModelImpl;
import com.hnust.liveapp.presenter.RoomPresenter;
import com.hnust.liveapp.presenter.UserPresenterImpl;
import com.hnust.liveapp.ui.activitys.LoginActivity;
import com.hnust.liveapp.ui.adapter.MessageAdapter;
import com.hnust.liveapp.widget.MagicTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import master.flame.danmaku.danmaku.model.BaseDanmaku;

/**
 * Created by yonglong on 2017/3/30.
 */

public class IjkPlayerActivity extends AppCompatActivity implements RoomContract.View, UserContract.View {

    @BindView(R.id.player_view)
    IjkPlayerView mPlayerView;
    @BindView(R.id.llgiftcontent)
    LinearLayout llgiftcontent;

    @BindView(R.id.btn_send_gift)
    ImageButton btn_send_gift;
    @BindView(R.id.btn_send_msg)
    Button btn_send_msg;
    @BindView(R.id.btn_focus)
    Button btn_focus;
    @BindView(R.id.btn_lock)
    Button btn_lock;
    @BindView(R.id.rv_msg)
    RecyclerView rv_msg;

    @BindView(R.id.et_danmu)
    EditText et_danmu;
    @BindView(R.id.tv_num)
    TextView tv_num;

    @OnClick(R.id.btn_focus)
    void focus() {
        if (!isLogining) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        roomPresenter.focusRoom(room_id);
    }

    @OnClick(R.id.btn_lock)
    void btn_lock() {
        roomPresenter.lockRoom(room_id);
    }

    @OnClick(R.id.btn_send_gift)
    void sendgift() {
        showPopuwindows();
    }

    @OnClick(R.id.btn_send_msg)
    void send() {
        String msg = et_danmu.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(this, "内容为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isConnected) {
            return;
        }
        if (isLogining) {
            et_danmu.setText("");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("content", msg);
                jsonObject.put("room", room_id);
                jsonObject.put("username", mUsername);
                Log.e("socket", jsonObject.toString());
                mSocket.emit("new_barrage", jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(IjkPlayerActivity.this, "请登录", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 数据
     */
    private List<View> giftViewCollection = new ArrayList<View>();
    private RecyclerView.Adapter mAdapter;
    private List<Message> mMessages = new ArrayList<Message>();

    private Socket mSocket;
    private int room_id = -1;
    private boolean isFocus = false;
    private String jt_url;
    private String play_url;
    private String zhubo_name;
    private String fj_name;

    private boolean isLogining = false;

    private String mUsername;

    //连接聊天服务器
    private Boolean isConnected = false;
    private static final String TAG = "Ijkplayer";

    RoomPresenter roomPresenter;
    UserPresenterImpl userPresenter;
    UserModelImpl userModel = new UserModelImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijkplayer);
        ButterKnife.bind(this);

        /**
         * 初始化数据
         */
        room_id = getIntent().getIntExtra("room_id", -1);
        jt_url = getIntent().getStringExtra("jt_url");
        play_url = getIntent().getStringExtra("play_url");
        zhubo_name = getIntent().getStringExtra("zhubo_name");
        fj_name = getIntent().getStringExtra("fj_name");

        roomPresenter = new RoomPresenter(this, this);
        userPresenter = new UserPresenterImpl(this, this);
        roomPresenter.getRoomInfo(room_id);

        if (userModel.getUserInfo(this) != null && userModel.getUserInfo(this).getUsername() != null) {
            isLogining = true;
            mUsername = userModel.getUserInfo(this).getName();
        }
        if (isLogining && userModel.getUserInfo(this).getZhubo() == 2) {
            btn_lock.setVisibility(View.VISIBLE);
        }

        initVideo();
        initView();
        initChat();
        initGift();
    }

    private void initVideo() {
        //显示封面图片
        Glide.with(this).load(jt_url).error(R.mipmap.icon).fitCenter().into(mPlayerView.mPlayerThumb); // Show the thumb before play
        mPlayerView.init()              // Initialize, the first to use
                .setTitle(fj_name + "--" + zhubo_name)    // set title
                .setVideoPath(play_url)    // set video url
                .enableDanmaku()
                .showOrHideDanmaku(false)
                .setDanmakuListener(new OnDanmakuListener() {
                    @Override
                    public boolean isValid() {
                        return true;
                    }

                    @Override
                    public void onDataObtain(Object data) {
                        BaseDanmaku danmaku = (BaseDanmaku) data;
                        if (!danmaku.isLive) {
                            //显示到recyclerview
                            if (isLogining) {
//                                addMessage(mUsername, danmaku.text.toString(), false);
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("content", danmaku.text.toString());
                                    jsonObject.put("room", room_id);
                                    jsonObject.put("username", mUsername);

                                    mSocket.emit("new_barrage", jsonObject);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(IjkPlayerActivity.this, "请登录", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .start();   // Start playing
    }

    /**
     * 初始化聊天服务器
     */
    private void initChat() {
        //获取本地Application
        App app = (App) getApplication();
        //实例化Socket对象
        mSocket = app.getSocket();
        //设置聊天服务器连接监听事件
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        //设置聊天消息监听事件
        mSocket.on("barrage", onNewMessage1);
        //设置系统消息监听事件
        mSocket.on("sys_msg", onNewMessage2);
        mSocket.connect();
    }

    private void initView() {
        rv_msg.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessageAdapter(this, mMessages);
        rv_msg.setAdapter(mAdapter);
        scrollToBottom();

//        /**
//         * 回车键聊天信息发送
//         */
        et_danmu.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    send();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 动画相关
     */
    private NumAnim giftNumAnim;
    private TranslateAnimation inAnim;
    private TranslateAnimation outAnim;


    private void initGift() {
        /**
         * 礼物
         */
        inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_in);
        outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_out);
        giftNumAnim = new NumAnim();
        clearTiming();
    }

    private void scrollToBottom() {
        rv_msg.scrollToPosition(mAdapter.getItemCount() - 1);
    }


    /**
     * 增加log
     *
     * @param message
     */
    private void addLog(String message) {
        mMessages.add(new Message.Builder(Message.TYPE_LOG)
                .message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    /**
     * 消息显示到mAdapter，和弹幕
     *
     * @param username
     * @param message
     * @param isLive   true 直播收到消息，发到弹幕 ， false 弹幕返回数据显示到rv
     */
    private void addMessage(String username, String message, boolean isLive) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .username(username).message(message).build());
        //添加到弹幕
        try {
            mPlayerView.sendDanmaku(message, isLive);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }


    /**
     * 连接成功
     */
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if (!isConnected) {
                    addLog(getResources().getString(R.string.message_welcome));
                    addLog("聊天服务器连接成功");

                    mSocket.emit("join", room_id);

                    isConnected = true;
//                    }
                }
            });
        }
    };

    /**
     * 新消息
     */
    private Emitter.Listener onNewMessage1 = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("Socket.io", args[0].toString());
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    String time;
                    try {
                        username = data.getString("username");
                        message = data.getString("content");
                        time = data.getString("date");
                        addMessage(username, message, true);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                }
            });
        }
    };

    /**
     * 系统消息
     */
    private Emitter.Listener onNewMessage2 = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("Socket.io", args[0].toString());
//                    10001：加入房间成功；10002：房间人数；10003：送礼物失败；10004：有人送礼物给主播
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        int type = jsonObject.getInt("type");
                        //房间人数
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (type == 10001) {
                            //加入房间成功
                            addLog("加入房间成功");
                        } else if (type == 10002) {
                            int num = data.getInt("num");
                            tv_num.setText("当前房间人数：" + num);
                        } else if (type == 10003) {
                            String msg = data.getString("msg");
                            Toast.makeText(IjkPlayerActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else if (type == 10004) {
                            String name = data.getString("nickname");
                            int gifttype = data.getInt("type");
                            int num = data.getInt("num");
                            showGift(name, gifttype);
                        }
                        if (type == 10005) {
                            userPresenter.getUserInfo(IjkPlayerActivity.this);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                }
            });
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        mPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.onDestroy();


        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off("barrage", onNewMessage1);
        mSocket.off("sys_msg", onNewMessage2);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPlayerView.configurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mPlayerView.handleVolumeKey(keyCode)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mPlayerView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void updateView(User user, boolean isLogin) {
        Log.e("Ijkplayer", user.toString() + ":" + isLogin);
        if (giftWindow != null) {
            tv_gift_last.setText(user.getGift());
        }
    }

    @Override
    public void returnInfo(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void returnError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void returnFocusInfo(boolean isFocus) {
        this.isFocus = isFocus;
        if (isFocus) {
            Toast.makeText(IjkPlayerActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
            btn_focus.setText("已关注");
        } else {
            btn_focus.setText("关注");
            Toast.makeText(IjkPlayerActivity.this, "取消关注成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void returnLiveInfo(GsonOpenFj gsonOpenFj) {
        if (gsonOpenFj.getData().isStatus()) {
            btn_focus.setText("已关注");
        } else {
            btn_focus.setText("关注");
        }
    }

    /************************************
     *
     *礼物
     *
     *
     */


    /**
     * 添加礼物view,(考虑垃圾回收)
     */
    private View addGiftView() {
        View view = null;
        if (giftViewCollection.size() <= 0) {
            /*如果垃圾回收中没有view,则生成一个*/
            view = LayoutInflater.from(this).inflate(R.layout.item_gift, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 10;
            view.setLayoutParams(lp);
            llgiftcontent.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    giftViewCollection.add(view);
                }
            });
        } else {
            view = giftViewCollection.get(0);
            giftViewCollection.remove(view);
        }
        return view;
    }

    /**
     * 删除礼物view
     */
    private void removeGiftView(final int index) {
        final View removeView = llgiftcontent.getChildAt(index);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llgiftcontent.removeViewAt(index);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeView.startAnimation(outAnim);
            }
        });
    }

    int[] gifts = new int[]{R.mipmap.ic_gift, R.mipmap.ic_chicken, R.mipmap.ic_car, R.mipmap.ic_plane};
    String[] gifts_name = new String[]{"狗粮", "鸡肉", "汽车", "飞机"};
    int[] gifts_value = new int[]{1, 20, 50, 100};

    /**
     * 显示礼物的方法
     *
     * @param username
     * @param flag     0 1 2 3
     */
    private void showGift(String username, int flag) {
        View giftView = llgiftcontent.findViewWithTag(username + flag);
        if (giftView == null) {/*该用户不在礼物显示列表*/

            if (llgiftcontent.getChildCount() > 2) {/*如果正在显示的礼物的个数超过两个，那么就移除最后一次更新时间比较长的*/
                View giftView1 = llgiftcontent.getChildAt(0);
                ImageView picTv1 = (ImageView) giftView1.findViewById(R.id.ivgift);
                long lastTime1 = (Long) picTv1.getTag();
                View giftView2 = llgiftcontent.getChildAt(1);
                ImageView picTv2 = (ImageView) giftView2.findViewById(R.id.ivgift);
                long lastTime2 = (Long) picTv2.getTag();
                if (lastTime1 > lastTime2) {/*如果第二个View显示的时间比较长*/
                    removeGiftView(1);
                } else {/*如果第一个View显示的时间长*/
                    removeGiftView(0);
                }
            }
            giftView = addGiftView();/*获取礼物的View的布局*/
            giftView.setTag(username + flag);/*设置view标识*/
            //礼物
            ImageView crvheadimage = (ImageView) giftView.findViewById(R.id.ivgift);
            crvheadimage.setImageResource(gifts[flag]);
            TextView tv_name = (TextView) giftView.findViewById(R.id.tv_name);
            tv_name.setText(username);
            TextView tv_gift_name = (TextView) giftView.findViewById(R.id.tv_gift_name);
            tv_gift_name.setText(gifts_name[flag]);
            final MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);/*找到数量控件*/
            giftNum.setText("x1");/*设置礼物数量*/
            crvheadimage.setTag(System.currentTimeMillis());/*设置时间标记*/
            giftNum.setTag(1);/*给数量控件设置标记*/

            llgiftcontent.addView(giftView);/*将礼物的View添加到礼物的ViewGroup中*/
            llgiftcontent.invalidate();/*刷新该view*/
            giftView.startAnimation(inAnim);/*开始执行显示礼物的动画*/
            inAnim.setAnimationListener(new Animation.AnimationListener() {/*显示动画的监听*/
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    giftNumAnim.start(giftNum);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {/*该用户在礼物显示列表*/
            ImageView crvheadimage = (ImageView) giftView.findViewById(R.id.ivgift);/*找到头像控件*/
            MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);/*找到数量控件*/
            int showNum = (Integer) giftNum.getTag() + 1;
            giftNum.setText("x" + showNum);
            giftNum.setTag(showNum);
            crvheadimage.setTag(System.currentTimeMillis());
            giftNumAnim.start(giftNum);
        }
    }


    /**
     * 定时清除礼物
     */
    private void clearTiming() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                int count = llgiftcontent.getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = llgiftcontent.getChildAt(i);
                    ImageView crvheadimage = (ImageView) view.findViewById(R.id.ivgift);
                    long nowtime = System.currentTimeMillis();
                    long upTime = (Long) crvheadimage.getTag();
                    if ((nowtime - upTime) >= 3000) {
                        removeGiftView(i);
                        return;
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 3000);
    }

    /**
     * 数字放大动画
     */
    public class NumAnim {
        private Animator lastAnimator = null;

        public void start(View view) {
            if (lastAnimator != null) {
                lastAnimator.removeAllListeners();
                lastAnimator.end();
                lastAnimator.cancel();
            }
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.3f, 1.0f);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.3f, 1.0f);
            AnimatorSet animSet = new AnimatorSet();
            lastAnimator = animSet;
            animSet.setDuration(200);
            animSet.setInterpolator(new OvershootInterpolator());
            animSet.playTogether(anim1, anim2);
            animSet.start();
        }

    }


    /**************
     *
     */

    PopupWindow giftWindow;
    GridView gridView;
    TextView tv_gift_last;
    Button btn_gift;
    int selectItem = -1;


    private void showPopuwindows() {
        View mMenuView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null);
        selectItem = -1;
        gridView = (GridView) mMenuView.findViewById(R.id.gv_gift);//窗口的布局
        gridView.setAdapter(getAdapter());
        gridView.setOnItemClickListener(new ItemClickListener1());

        tv_gift_last = (TextView) mMenuView.findViewById(R.id.tv_gift_last);
        tv_gift_last.setText(userModel.getUserInfo(this).getGift());
        btn_gift = (Button) mMenuView.findViewById(R.id.btn_gift);
        btn_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectItem == -1) {
                    Toast.makeText(IjkPlayerActivity.this, "请选择礼物", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("cookie", Context.MODE_PRIVATE);
                    String cookie = sharedPreferences.getString("cookie", "");

                    Log.e("22222222222222", cookie);
                    JSONObject jsonObject = new JSONObject();
                    String[] cookiews = cookie.replace(";", "").split("=");
                    try {
                        JSONObject cookiew = new JSONObject();
                        cookiew.put(cookiews[0], cookiews[1]);

                        Log.e("22222222222222", cookiew.toString());
                        jsonObject.put("cookie", cookiew);
                        jsonObject.put("num", 1);
                        jsonObject.put("type", selectItem);
                        jsonObject.put("room_id", room_id);

                        mSocket.emit("gift", jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //实例化SelectPicPopupWindow
        giftWindow = new PopupWindow(mMenuView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置layout在PopupWindow中显示的位置
        giftWindow.setAnimationStyle(R.anim.gift_in);
        giftWindow.setBackgroundDrawable(new BitmapDrawable());
        giftWindow.setOutsideTouchable(true);
        //显示窗口
        giftWindow.showAtLocation(getCurrentFocus(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    private final class ItemClickListener1 implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (giftWindow.isShowing()) {
                selectItem = position;
                for (int i = 0; i < parent.getCount(); i++) {
                    View v = parent.getChildAt(i);
                    if (position == i) {//当前选中的Item改变背景颜色
                        view.setBackgroundColor(R.color.grey);
                    } else {
                        v.setBackgroundColor(Color.TRANSPARENT);
                    }
                }

            }

        }

    }

    private ListAdapter getAdapter() {
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < gifts.length; i++) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("image", gifts[i]);
            item.put("name", gifts_name[i]);
            item.put("value", gifts_value[i] + "狗粮");
            data.add(item);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.item_gift1,
                new String[]{"image", "name", "value"}, new int[]{R.id.iv_gift, R.id.tv_gift_name, R.id.tv_gift_value});

        return simpleAdapter;
    }


}