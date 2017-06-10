package com.hnust.liveapp.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dou361.ijkplayer.listener.OnControlPanelVisibilityChangeListener;
import com.dou361.ijkplayer.listener.OnShowThumbnailListener;
import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;
import com.hnust.liveapp.App;
import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.Message;
import com.hnust.liveapp.model.UserModelImpl;
import com.hnust.liveapp.ui.adapter.MessageAdapter;
import com.hnust.liveapp.util.MediaUtils;
import com.hnust.liveapp.widget.SwitchIconView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "Socket";
    /**
     * 界面相关
     */
    //顶部
    @BindView(R.id.live_top_box)
    LinearLayout live_top_box;
    @BindView(R.id.live_top_danmu)
    LinearLayout live_top_danmu;
    @BindView(R.id.live_title)
    TextView live_title;
    @BindView(R.id.live_finish)
    ImageView live_finish;
    @BindView(R.id.et_top_danmu)
    EditText et_top_danmu;
    @BindView(R.id.btn_top_send)
    Button btn_top_send;
    //底部
    @BindView(R.id.live_bottom_box)
    RelativeLayout live_bottom_box;
    @BindView(R.id.live_fullscreen)
    ImageView live_fullscreen;
    @BindView(R.id.iv_gift)
    ImageView iv_gift;
    @BindView(R.id.si_danmu)
    SwitchIconView si_danmu;
    @BindView(R.id.live_bottom_danmu)
    LinearLayout live_bottom_danmu;
    @BindView(R.id.btn_send)
    Button btn_send;
    //播放窗口外
    @BindView(R.id.sendInput)
    Button sendInput;
    @BindView(R.id.rv_msg)
    RecyclerView rv_msg;
    @BindView(R.id.et_Input)
    EditText et_Input;
    //中
    @BindView(R.id.app_video_box)
    RelativeLayout app_video_box;
    @BindView(R.id.numsUsers)
    TextView numsUsers;
    //弹幕控件
    @BindView(R.id.danmaku_view)
    DanmakuView danmakuView;

    /**
     * 屏幕
     */
    private PowerManager.WakeLock wakeLock;
    private InputMethodManager imm;

    /**
     * 变量
     */
    private boolean isPlaying = true;

    private boolean showDanmaku;
    //DanmakuContext  字体实例
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    Context mContext;
    PlayerView playerView;
    /**
     * 数据相关
     */
    /**
     * 是否是竖屏，默认为竖屏，true为竖屏，false为横屏
     */
    private boolean isPortrait = true;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private String mUsername;
    private Socket mSocket;

    private Boolean isConnected = false;

    UserModelImpl userModel = new UserModelImpl();


    String name;
    String nick;
    String url;


    /**
     * 点击事件
     * 关闭
     */
    @OnClick(R.id.live_finish)
    void close() {
        boolean back = playerView.onBackPressed();
        if (!back) {
            finish();
        }
    }


    /**
     * 全屏
     */
    @OnClick(R.id.live_fullscreen)
    void full() {
        playerView.toggleFullScreen();
    }

    /**
     * 显示输入框
     */
    @OnClick(R.id.btn_send)
    void show_et() {
        live_top_danmu.setVisibility(View.VISIBLE);
        et_top_danmu.requestFocus();
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 发送弹幕
     */
    @OnClick(R.id.btn_top_send)
    void send_danmu() {
        String text = et_top_danmu.getText().toString();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
        } else {
            addDanmaku(text, true);
            sendChat("大师", text);
            et_top_danmu.setText("");
            live_top_danmu.setVisibility(View.GONE);
        }
    }

    /**
     * 发送聊天内容
     */
    @OnClick(R.id.sendInput)
    void send() {
        String text = et_Input.getText().toString();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
        } else {
            if (userModel.getUserInfo(mContext).getId() != null) {
//            mSocket.emit("new message", "大师" + text);
                sendChat("大师", text);
                et_Input.setText("");
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
            }
        }
    }

    /**
     * 关闭弹幕
     */
    @OnClick(R.id.si_danmu)
    void oc_danmu() {
        si_danmu.switchState();
        if (si_danmu.isIconEnabled()) {
            danmakuView.setVisibility(View.VISIBLE);
        } else {
            danmakuView.setVisibility(View.GONE);
        }
    }


    /**
     * 发送聊天
     *
     * @param username
     * @param message
     */
    private void sendChat(String username, String message) {
        if (null == mUsername) return;
        addMessage(mUsername, message);

        // perform the sending message attempt.
        mSocket.emit("new message", message);
        hideKeyboard();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mContext = this;
        ButterKnife.bind(this);

        url = getIntent().getStringExtra("url");
        nick = getIntent().getStringExtra("nick");
        name = getIntent().getStringExtra("name");

        live_title.setText(name + "-" + nick);
        imm = (InputMethodManager) et_top_danmu.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        initDanmu();
        initVideo();
        initChat();


    }


    /**
     * 初始化聊天室
     */
    private void initChat() {
        App app = (App) getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("sys_message", onNewMessage);
        mSocket.on("join", onUserJoined);
        mSocket.on("login", onLogin);
        mSocket.connect();


        mUsername = "username";
        // perform the user login attempt.
        mSocket.emit("add user", mUsername);

        rv_msg.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessageAdapter(this, mMessages);
        rv_msg.setAdapter(mAdapter);
        scrollToBottom();


        addLog(getResources().getString(R.string.message_welcome));

    }

    /**
     * 播放器
     */
    private void initVideo() {
        /**常亮*/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();

        /**播放器*/
        playerView = new PlayerView(this) {
            @Override
            public PlayerView toggleProcessDurationOrientation() {
                return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? PlayStateParams.PROCESS_PORTRAIT : PlayStateParams.PROCESS_LANDSCAPE);
            }
        }
                .setScaleType(PlayStateParams.wrapcontent)
                .hideHideTopBar(true)
                .forbidTouch(false)
                .showThumbnail(new OnShowThumbnailListener() {
                    @Override
                    public void onShowThumbnail(ImageView ivThumbnail) {
                        /**加载前显示的缩略图*/
                        Glide.with(mContext)
                                .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
                                .error(R.color.black)
                                .into(ivThumbnail);
                    }
                })
                .setOnControlPanelVisibilityChangListenter(new OnControlPanelVisibilityChangeListener() {
                    @Override
                    public void change(boolean isShowing) {
                        //显示虚拟键盘事件
                        if (playerView != null && playerView.getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            app_video_box.setSystemUiVisibility(isShowing ? View.SYSTEM_UI_FLAG_VISIBLE : View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                        }
                        live_bottom_box.setVisibility(isShowing ? View.VISIBLE : View.GONE);
                        live_top_box.setVisibility(isShowing ? View.VISIBLE : View.GONE);
                        live_top_danmu.setVisibility(View.GONE);

                        imm.hideSoftInputFromWindow(et_top_danmu.getWindowToken(), 0);

                    }
                })
                .setPlaySource(url)
                .startPlay();

    }


    /**
     * 弹幕初始化
     */
    private void initDanmu() {
        //默认为true 在模拟器上运行有问题
        danmakuView.enableDanmakuDrawingCache(true);
        //看源码得知是一个接口    怎么实现还是要咱们去重写其中的方法
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                //把变量置为 true
                showDanmaku = true;
                //开始运行弹幕控件
                danmakuView.start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        //调用  DanmakuContext.create() 完成DanmakuContext的实例化.
        danmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser, danmakuContext);
    }


    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 向弹幕View中添加一条弹幕
     *
     * @param content    弹幕的具体内容
     * @param withBorder 弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        //BaseDanmaku 您可以点击进入查看源码实现
        // 弹幕的相关设置：弹幕优先级  颜色  时长  文本  Z轴  Y轴  阴影  描边
        //                下划线  内边距  宽度  高度  存活时间  是否是直播弹幕
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;                  //文本
        danmaku.padding = 5;                     //内边距
        danmaku.textSize = sp2px(20);           //字体大小
        danmaku.textColor = Color.WHITE;       //文本颜色
        danmaku.setTime(danmakuView.getCurrentTime() + 1000); //显示时长 偏移时间
        //如果是true 证明是自己的弹幕，那么就可以更改自己想要的颜色了
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        //调用底层代码 把弹幕内容添加到</span><span style="font-family:KaiTi_GB2312;font-size:18px;">LinkedList<Long> mDrawTimes;</span><span style="font-family:KaiTi_GB2312;font-size:18px;">
        danmakuView.addDanmaku(danmaku);
    }

    /**
     * 隐藏软键盘并显示头布局
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_Input.getWindowToken(), 0);
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
     * 增加人数log
     *
     * @param numUsers
     */
    private void addParticipantsLog(final int numUsers) {
//        addLog(getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                numsUsers.setText("房间人数：" + numUsers);
            }
        });

    }

    /**
     * 发送消息
     *
     * @param username
     * @param message
     */
    private void addMessage(String username, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .username(username).message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }


    private void scrollToBottom() {
        rv_msg.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    /**
     * 连接失败
     */
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isConnected) {
                        if (null != mUsername)
                            mSocket.emit("add user", mUsername);
                        Toast.makeText(getApplicationContext(),
                                R.string.connect, Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }
    };
    /**
     * 登录
     */

    int numUsers;
    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }
            enter();
        }
    };

    private void enter() {
        addParticipantsLog(numUsers);
    }

    /**
     * 连接失败
     */
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
                    isConnected = false;
                    Toast.makeText(getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    /**
     * 连接错误
     */
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
                    Toast.makeText(getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    /**
     * 新消息
     */
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    if (playerView.getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        addDanmaku(message, false);
                    }
                    addMessage(username, message);
                }
            });
        }
    };

    /**
     * 进入房间
     */
    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_joined, username));
                    addParticipantsLog(numUsers);
                }
            });
        }
    };

    /**
     * 退出房间
     */
    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_left, username));
                    addParticipantsLog(numUsers);
                }
            });
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (playerView != null) {
            playerView.onPause();
        }
        /**demo的内容，恢复系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(mContext, true);


        //如果弹幕控件不为空 && 弹幕控件的线程还存活
        if (danmakuView != null && danmakuView.isPrepared()) {
            //暂停运行弹幕控件
            danmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerView != null) {
            playerView.onResume();
        }
        /**demo的内容，暂停系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(mContext, false);
        /**demo的内容，激活设备常亮状态*/
        if (wakeLock != null) {
            wakeLock.acquire();
        }
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerView != null) {
            playerView.onDestroy();
        }
        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);

        //把变量置为false
        showDanmaku = false;
        //如果弹幕控件还存在.调用release(); 底层调用stop(),并把底层的LinkedList<Long> mDrawTimes 置为空；
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (playerView != null) {
            playerView.onConfigurationChanged(newConfig);

            live_top_danmu.setVisibility(View.GONE);
            imm.hideSoftInputFromWindow(et_top_danmu.getWindowToken(), 0);

            if (playerView.getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                danmakuView.setVisibility(View.VISIBLE);
                live_bottom_danmu.setVisibility(View.VISIBLE);
            } else {
                danmakuView.setVisibility(View.GONE);
                live_bottom_danmu.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (playerView != null && playerView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
        /**demo的内容，恢复设备亮度状态*/
        if (wakeLock != null) {
            wakeLock.release();
        }
    }
}
