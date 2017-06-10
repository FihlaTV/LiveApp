package com.hnust.liveapp.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.ui.activitys.PlayerForAppActivity;
import com.hnust.liveapp.ui.adapter.AudienceAdapter;
import com.hnust.liveapp.ui.adapter.MessageAdapter;
import com.hnust.liveapp.util.DisplayUtil;
import com.hnust.liveapp.widget.HorizontalListView;
import com.hnust.liveapp.widget.MagicTextView;
import com.hnust.liveapp.widget.SoftKeyBoardListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 该Fragment是用于dialogFragment中的pager，为了实现滑动隐藏交互Fragment的
 * 交互的操作都在这个界面实现的，如果大家要改交互主要修改这个界面就可以了
 * <p>
 * Success is the sum of small efforts, repeated day in and day out.
 * 成功就是日复一日那一点点小小努力的积累。
 */
public class PlayerFragment extends Fragment {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
            }
        }
    };

    /**
     * 标示判断
     */
    private boolean isOpen;
    private long liveTime;

    int height;

    /**
     * 界面相关
     */
    private LinearLayout lin_control;
    private EditText etInput;
    private Button btn_send;


    public static PlayerFragment newInstance(int flag) {
        Bundle args = new Bundle();
        args.putInt("flag", flag);
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_control_view, container, false);

        lin_control = (LinearLayout) view.findViewById(R.id.lin_control);
        etInput = (EditText) view.findViewById(R.id.etInput);
        btn_send = (Button) view.findViewById(R.id.btn_send);
        height = lin_control.getHeight();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        softKeyboardListnenr();
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
        SoftKeyBoardListener.setListener(getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {/*软键盘显示：执行隐藏title动画，并修改listview高度和装载礼物容器的高度*/
                dynamicChangeH(height);
                Log.e("Height",height+"---");
            }

            @Override
            public void keyBoardHide(int height) {/*软键盘隐藏：隐藏聊天输入框并显示聊天按钮，执行显示title动画，并修改listview高度和装载礼物容器的高度*/
                dynamicChangeH(height);
                Log.e("Height",height+"---");
            }
        });
    }

    /**
     * 动态的修改listview的高度
     *
     * @param heightPX
     */
    private void dynamicChangeH(int heightPX) {
        ViewGroup.LayoutParams layoutParams = lin_control.getLayoutParams();
        layoutParams.height = DisplayUtil.dip2px(getActivity(), heightPX);
        lin_control.setLayoutParams(layoutParams);
    }
}