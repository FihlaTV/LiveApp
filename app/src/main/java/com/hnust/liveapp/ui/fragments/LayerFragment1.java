package com.hnust.liveapp.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hnust.liveapp.App;
import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.Message;
import com.hnust.liveapp.bean.User;
import com.hnust.liveapp.model.UserModelImpl;
import com.hnust.liveapp.ui.activitys.HosterActivity;
import com.hnust.liveapp.ui.adapter.MessageAdapter;
import com.hnust.liveapp.util.DisplayUtil;
import com.hnust.liveapp.widget.MagicTextView;
import com.hnust.liveapp.widget.SoftKeyBoardListener;
import com.hnust.liveapp.widget.SwitchIconView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * 该Fragment是用于dialogFragment中的pager，为了实现滑动隐藏交互Fragment的
 * 交互的操作都在这个界面实现的
 */
public class LayerFragment1 extends Fragment {

    /**
     * 控制
     */
    @BindView(R.id.btn_flash)
    SwitchIconView mFlashBtn;
    @BindView(R.id.btn_camera)
    SwitchIconView mFaceBtn;

    @BindView(R.id.btn_close)
    ImageButton btn_close;

    /**
     * 用户
     */
    @BindView(R.id.civ_head)
    CircleImageView civ_head;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_num)
    TextView tv_num;
    @BindView(R.id.tv_room_gift)
    TextView tv_room_gift;
    @BindView(R.id.lvmessage)
    RecyclerView mRecyclerView;

    /**
     * 聊天
     */
    @BindView(R.id.tvChat)
    TextView tvChat;
    @BindView(R.id.etInput)
    EditText etInput;
    @BindView(R.id.sendInput)
    Button sendInput;
    @BindView(R.id.llgiftcontent)
    LinearLayout llgiftcontent;
    @BindView(R.id.llinputparent)
    LinearLayout llinputparent;
    @BindView(R.id.container)
    RelativeLayout container;

    /**
     * 显示聊天布局
     */
    @OnClick(R.id.tvChat)
    void showChat() {
        tvChat.setVisibility(View.GONE);
        llinputparent.setVisibility(View.VISIBLE);
        llinputparent.requestFocus();
        showKeyboard();
    }

    @OnClick(R.id.btn_close)
    void close() {
        hosterActivity.logout();
    }

    @OnClick(R.id.btn_camera)
    void switchcamera() {
        mFaceBtn.switchState();
        hosterActivity.switchCamera();
    }

    @OnClick(R.id.btn_flash)
    void flash() {
        mFlashBtn.switchState();
        hosterActivity.switchTorch();
    }

    @OnClick(R.id.container)
    void container() {
        if (llinputparent.getVisibility() == View.VISIBLE) {
            tvChat.setVisibility(View.VISIBLE);
            llinputparent.setVisibility(View.GONE);
            hideKeyboard();
        }
    }

    /**
     * 发送消息
     */
    @OnClick(R.id.sendInput)
    void sendText() {
        String content = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getContext(), "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isConnected) {
            Toast.makeText(getContext(), "未连接到聊天服务", Toast.LENGTH_SHORT).show();
            return;
        }
        String username = userModel.getUserInfo(getContext()).getName();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", content);

            jsonObject.put("room", room_id);
            jsonObject.put("username", username);
        } catch (JSONException e) {

        }
        mSocket.emit("new_barrage", jsonObject);
        etInput.setText("");
        hideKeyboard();
    }


    private Handler handler = new Handler();
    /**
     * 标示判断
     */
    private boolean isOpen;
    private long liveTime;


    /**
     * 动画相关
     */
    private NumAnim giftNumAnim;
    private TranslateAnimation inAnim;
    private TranslateAnimation outAnim;
    private AnimatorSet animatorSetHide = new AnimatorSet();
    private AnimatorSet animatorSetShow = new AnimatorSet();

    /**
     * 数据相关
     */
    private List<View> giftViewCollection = new ArrayList<View>();
    private List<Message> mMessages = new ArrayList<>();
    private MessageAdapter mAdapter;
    private UserModelImpl userModel;
    private int room_id = -1;
    private User user;

    HosterActivity hosterActivity;

    public static LayerFragment1 newInstance(int flag) {
        Bundle args = new Bundle();
        args.putInt("flag", flag);
        LayerFragment1 fragment = new LayerFragment1();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layer1, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        softKeyboardListnenr();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MessageAdapter(getContext(), mMessages);
        mRecyclerView.setAdapter(mAdapter);
        scrollToBottom();

        hosterActivity = (HosterActivity) getActivity();
        userModel = new UserModelImpl();
        room_id = getArguments().getInt("flag", -1);
        Log.e("LayerFragment", "-0--" + room_id);
        tv_room_gift.setText("狗粮：" + userModel.getUserInfo(getContext()).getGift());
        if (room_id == -1) {
            Toast.makeText(getContext(), "加入房间失败", Toast.LENGTH_SHORT);
            return;
        }
        initChat();
        initView();
    }


    private void initView() {

        /**
         * 主播信息
         */
        user = userModel.getUserInfo(getContext());
        tv_name.setText(user.getName());
        Glide.with(getContext())
                .load(user.getIcon())
                .error(R.mipmap.dashi)
                .fitCenter()
                .into(civ_head);

        Log.e("LayerFragment", "-0--" + user.getName() + "++++" + user.getIcon());
        /**
         * 礼物
         */
        inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.gift_in);
        outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.gift_out);
        giftNumAnim = new NumAnim();
        clearTiming();
    }


    /**
     * 发送消息
     *
     * @param username
     * @param message
     */
    private void addMessage(String username, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_ZHIBO)
                .username(username).message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    /**
     * 滚动
     */
    private void scrollToBottom() {

        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeView.startAnimation(outAnim);
            }
        });
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
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_gift, null);
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


    int[] gifts = new int[]{R.mipmap.ic_gift, R.mipmap.ic_chicken, R.mipmap.ic_car, R.mipmap.ic_plane};
    String[] gifts_name = new String[]{"狗粮", "鸡肉", "汽车", "飞机"};

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
                        try {
                            removeGiftView(i);
                        } catch (Exception e) {

                        }
                        return;
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 3000);
    }


    /**
     * 显示软键盘并因此头布局
     */
    private void showKeyboard() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etInput, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
    }

    /**
     * 隐藏软键盘并显示头布局
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
    }

    /**
     * 软键盘显示与隐藏的监听
     */
    private void softKeyboardListnenr() {
        SoftKeyBoardListener.setListener((AppCompatActivity) getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {/*软键盘显示：执行隐藏title动画，并修改listview高度和装载礼物容器的高度*/
                dynamicChangeListviewH(100);
                dynamicChangeGiftParentH(true);
            }

            @Override
            public void keyBoardHide(int height) {/*软键盘隐藏：隐藏聊天输入框并显示聊天按钮，执行显示title动画，并修改listview高度和装载礼物容器的高度*/
                tvChat.setVisibility(View.VISIBLE);
                llinputparent.setVisibility(View.GONE);
                dynamicChangeListviewH(150);
                dynamicChangeGiftParentH(false);
            }
        });
    }

    /**
     * 动态的修改listview的高度
     *
     * @param heightPX
     */
    private void dynamicChangeListviewH(int heightPX) {
        ViewGroup.LayoutParams layoutParams = mRecyclerView.getLayoutParams();
        layoutParams.height = DisplayUtil.dip2px(getActivity(), heightPX);
        mRecyclerView.setLayoutParams(layoutParams);
    }

    /**
     * 动态修改礼物父布局的高度
     *
     * @param showhide
     */
    private void dynamicChangeGiftParentH(boolean showhide) {
        if (showhide) {/*如果软键盘显示中*/
            if (llgiftcontent.getChildCount() != 0) {
                /*判断是否有礼物显示，如果有就修改父布局高度，如果没有就不作任何操作*/
                ViewGroup.LayoutParams layoutParams = llgiftcontent.getLayoutParams();
                layoutParams.height = llgiftcontent.getChildAt(0).getHeight();
                llgiftcontent.setLayoutParams(layoutParams);
            }
        } else {/*如果软键盘隐藏中*/
            /*就将装载礼物的容器的高度设置为包裹内容*/
            ViewGroup.LayoutParams layoutParams = llgiftcontent.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            llgiftcontent.setLayoutParams(layoutParams);
        }
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

    private Socket mSocket;
    //连接聊天服务器
    private Boolean isConnected = false;
    private static final String TAG = "Ijkplayer";

    private void initChat() {
        App app = (App) getActivity().getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("barrage", onNewMessage1);
        mSocket.on("sys_msg", onNewMessage2);

        mSocket.connect();
        //连接
        mSocket.emit("join", room_id);

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


    @Override
    public void onDestroy() {
        super.onDestroy();


        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off("barrage", onNewMessage1);
        mSocket.off("sys_msg", onNewMessage2);
    }

    /**
     * 连接成功
     */
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("Socket.io", args.toString());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if (!isConnected) {
                    addLog(getResources().getString(R.string.message_welcome));
                    addLog("聊天服务器连接成功");
                    isConnected = true;
//                    } else {
//                        addLog("聊天服务器连接失败");
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
            getActivity().runOnUiThread(new Runnable() {
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

                        addMessage(username, message);
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("Socket.io", args[0].toString());
//                    10001：加入房间成功；10002：房间人数；10003：送礼物失败；10004：有人送礼物给主播
                    JSONObject jsonObject = (JSONObject) args[0];
                    int type;

                    try {
                        type = jsonObject.getInt("type");
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
                            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        } else if (type == 10004) {
                            String name = data.getString("nickname");
                            int gifttype = data.getInt("type");
                            int num = data.getInt("num");
                            int gift = data.getJSONObject("gift").getInt("gift");
                            tv_room_gift.setText("狗粮：" + gift);
                            showGift(name, gifttype);
                        } else if (type == 1005) {
                            Toast.makeText(getContext(), "发送成功", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                }
            });
        }
    };

}