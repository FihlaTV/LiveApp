package com.hnust.liveapp.ui.activitys;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.bean.GsonInfo;
import com.hnust.liveapp.model.UserModelImpl;
import com.hnust.liveapp.ui.fragments.LayerFragment1;
import com.hnust.liveapp.util.GlobalConfig;
import com.laifeng.sopcastsdk.camera.CameraListener;
import com.laifeng.sopcastsdk.configuration.AudioConfiguration;
import com.laifeng.sopcastsdk.configuration.CameraConfiguration;
import com.laifeng.sopcastsdk.configuration.VideoConfiguration;
import com.laifeng.sopcastsdk.stream.packer.rtmp.RtmpPacker;
import com.laifeng.sopcastsdk.stream.sender.rtmp.RtmpSender;
import com.laifeng.sopcastsdk.ui.CameraLivingView;
import com.laifeng.sopcastsdk.utils.SopCastLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.laifeng.sopcastsdk.constant.SopCastConstant.TAG;

/**
 * Created by yonglong on 2017/3/20.
 */

public class HosterActivity extends AppCompatActivity {


    @BindView(R.id.liveView)
    CameraLivingView mLFLiveView;

    @BindView(R.id.btn_start)
    Button btn_start;

    @BindView(R.id.progressConnecting)
    ProgressBar mProgressConnecting;

    /**
     * 数据
     */

    LayerFragment1 layerFragment1;
    private GestureDetector mGestureDetector;

    RtmpSender mRtmpSender;
    private boolean isRecording = false;
    private VideoConfiguration mVideoConfiguration;
    UserModelImpl userModel = new UserModelImpl();

    private int mCurrentBps;
    private int room_id;

    String TAGSS = "HosterActivity";

    @OnClick(R.id.btn_start)
    void E() {
        Log.e("HosterActivity", room_id + "-------");
        if (room_id == -1) {
            Toast.makeText(HosterActivity.this, "直播失败，房间号异常", Toast.LENGTH_SHORT).show();
        } else {
            String uploadUrl = GlobalConfig.LIVE_RTMP_TL + room_id;
            Log.e(TAGSS, uploadUrl);
            mRtmpSender.setAddress(uploadUrl);
            mProgressConnecting.setVisibility(View.VISIBLE);
            btn_start.setText("直播准备中...");
            mRtmpSender.connect();
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_host);
        ButterKnife.bind(this);

        room_id = getIntent().getIntExtra("room_id", -1);
        Log.e("HosterActivity", room_id + "-------");

        initView();
        initLiveView();

    }

    private void initView() {
        layerFragment1 = LayerFragment1.newInstance(room_id);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.fl_message, layerFragment1);
        //ft.addToBackStack(TAG);
        ft.commit();
    }

    /**
     * 摄像头选择
     */
    public void switchCamera() {
        mLFLiveView.switchCamera();
    }

    /**
     * 退出
     */
    public void logout() {
        new AlertDialog.Builder(this)
                .setTitle("确定退出直播？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        btn_start.setVisibility(View.VISIBLE);
                        openlive(1);
                        finish();
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }


    public void switchTorch() {
        mLFLiveView.switchTorch();
    }


    private void initLiveView() {
        SopCastLog.isOpen(true);
        mLFLiveView.init();
        CameraConfiguration.Builder cameraBuilder = new CameraConfiguration.Builder();
        cameraBuilder.setOrientation(CameraConfiguration.Orientation.PORTRAIT)
                .setFacing(CameraConfiguration.Facing.BACK);
        CameraConfiguration cameraConfiguration = cameraBuilder.build();
        mLFLiveView.setCameraConfiguration(cameraConfiguration);
        VideoConfiguration.Builder videoBuilder = new VideoConfiguration.Builder();
        videoBuilder.setSize(360, 640);
        mVideoConfiguration = videoBuilder.build();
        mLFLiveView.setVideoConfiguration(mVideoConfiguration);
        //设置预览监听
        mLFLiveView.setCameraOpenListener(new CameraListener() {
            @Override
            public void onOpenSuccess() {
                Toast.makeText(HosterActivity.this, "摄像头打开成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onOpenFail(int error) {
                Toast.makeText(HosterActivity.this, "摄像头打开失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCameraChange() {
                Toast.makeText(HosterActivity.this, "更换摄像头", Toast.LENGTH_LONG).show();
            }
        });
        //设置手势识别
        mGestureDetector = new GestureDetector(this, new GestureListener());
        mLFLiveView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return false;
            }
        });
        //初始化flv打包器
        RtmpPacker packer = new RtmpPacker();
        packer.initAudioParams(AudioConfiguration.DEFAULT_FREQUENCY, 16, false);
        mLFLiveView.setPacker(packer);
        //设置发送器
        mRtmpSender = new RtmpSender();
        mRtmpSender.setVideoParams(360, 640);
        mRtmpSender.setAudioParams(AudioConfiguration.DEFAULT_FREQUENCY, 16, false);
        mRtmpSender.setSenderListener(mSenderListener);
        mLFLiveView.setSender(mRtmpSender);
        mLFLiveView.setLivingStartListener(new CameraLivingView.LivingStartListener() {
            @Override
            public void startError(int error) {
                Log.e("===", error + "");
                //直播失败
                Toast.makeText(HosterActivity.this, "开始直播失败,服务器异常" + error, Toast.LENGTH_SHORT).show();
                mLFLiveView.stop();

                isRecording = false;
                btn_start.setText("开始直播");
                btn_start.setVisibility(View.VISIBLE);
            }

            @Override
            public void startSuccess() {
                btn_start.setVisibility(View.GONE);
                //直播成功
                btn_start.setText("直播开始");
                openlive(0);
            }
        });
    }

    private RtmpSender.OnSenderListener mSenderListener = new RtmpSender.OnSenderListener() {
        @Override
        public void onConnecting() {
        }

        @Override
        public void onConnected() {
            mProgressConnecting.setVisibility(View.GONE);
            mLFLiveView.start();
            mCurrentBps = mVideoConfiguration.maxBps;
        }

        @Override
        public void onDisConnected() {
            mProgressConnecting.setVisibility(View.GONE);
            Toast.makeText(HosterActivity.this, "服务器异常，连接失败", Toast.LENGTH_SHORT).show();
            btn_start.setVisibility(View.VISIBLE);
            btn_start.setText("直播开始");
            mLFLiveView.stop();
            isRecording = false;
        }

        @Override
        public void onPublishFail() {
            mProgressConnecting.setVisibility(View.GONE);
            Toast.makeText(HosterActivity.this, "服务器异常，推流失败", Toast.LENGTH_SHORT).show();
            btn_start.setVisibility(View.VISIBLE);
            btn_start.setText("直播开始");
            isRecording = false;
        }

        @Override
        public void onNetGood() {
            if (mCurrentBps + 50 <= mVideoConfiguration.maxBps) {
                SopCastLog.d(TAG, "BPS_CHANGE good up 50");
                int bps = mCurrentBps + 50;
                if (mLFLiveView != null) {
                    boolean result = mLFLiveView.setVideoBps(bps);
                    if (result) {
                        mCurrentBps = bps;
                    }
                }
            } else {
                SopCastLog.d(TAG, "BPS_CHANGE good good good");
            }
            SopCastLog.d(TAG, "Current Bps: " + mCurrentBps);
        }

        @Override
        public void onNetBad() {
            if (mCurrentBps - 100 >= mVideoConfiguration.minBps) {
                SopCastLog.d(TAG, "BPS_CHANGE bad down 100");
                int bps = mCurrentBps - 100;
                if (mLFLiveView != null) {
                    boolean result = mLFLiveView.setVideoBps(bps);
                    if (result) {
                        mCurrentBps = bps;
                    }
                }
            } else {
                SopCastLog.d(TAG, "BPS_CHANGE bad down 100");
            }
            SopCastLog.d(TAG, "Current Bps: " + mCurrentBps);
        }
    };


    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > 100
                    && Math.abs(velocityX) > 200) {
                // Fling left
//                Toast.makeText(HosterActivity.this, "Fling Left", Toast.LENGTH_SHORT).show();
            } else if (e2.getX() - e1.getX() > 100
                    && Math.abs(velocityX) > 200) {
                // Fling right
//                Toast.makeText(HosterActivity.this, "Fling Right", Toast.LENGTH_SHORT).show();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }


    private void openlive(int now) {
        ApiManager.getInstence(this).apiService
                .openLive(now)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(HosterActivity.this, "服务器异常", Toast.LENGTH_SHORT);
                        Log.e("HosterActivity", e.getMessage());

                    }

                    @Override
                    public void onNext(GsonInfo gsonInfo) {
                        if (gsonInfo.getStatus()) {
                            Toast.makeText(HosterActivity.this, gsonInfo.getMessage(), Toast.LENGTH_SHORT);
                        } else {
                            Toast.makeText(HosterActivity.this, gsonInfo.getMessage(), Toast.LENGTH_SHORT);

                        }
                    }
                });
    }


    @Override
    protected void onStop() {
        super.onStop();
        mLFLiveView.pause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLFLiveView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLFLiveView.stop();
        mLFLiveView.release();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        logout();
    }
}
