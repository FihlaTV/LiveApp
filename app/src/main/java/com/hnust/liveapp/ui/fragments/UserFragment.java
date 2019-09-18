package com.hnust.liveapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.User;
import com.hnust.liveapp.contract.UserContract;
import com.hnust.liveapp.model.UserModelImpl;
import com.hnust.liveapp.presenter.UserPresenterImpl;
import com.hnust.liveapp.ui.activitys.AuthActivity;
import com.hnust.liveapp.ui.activitys.HistoryActivity;
import com.hnust.liveapp.ui.activitys.LoginActivity;
import com.hnust.liveapp.ui.activitys.ManagerActivity;
import com.hnust.liveapp.ui.activitys.SettingActivity;
import com.hnust.liveapp.ui.activitys.SignActivity;
import com.hnust.liveapp.ui.activitys.UserInfoActivity;
import com.hnust.liveapp.ui.activitys.live.PreLiveActivity;
import com.hnust.liveapp.util.GlobalConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

/**
 * Created by yonglong on 2017/3/28.
 */

public class UserFragment extends Fragment implements UserContract.View {

    UserPresenterImpl userPresenter;


    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.lin_live)
    RelativeLayout lin_live;
    @BindView(R.id.lin_sign)
    RelativeLayout lin_sign;
    @BindView(R.id.userView)
    LinearLayout mUserView;
    @BindView(R.id.lin_settings)
    RelativeLayout lin_setting;
    @BindView(R.id.lin_manager)
    RelativeLayout lin_manager;
    @BindView(R.id.username)
    TextView mTvUserName;
    @BindView(R.id.tv_sex)
    TextView tv_sex;
    @BindView(R.id.gift)
    TextView mTvGift;
    @BindView(R.id.civ_person)
    CircleImageView mCivPerson;

    /**
     * 数据
     */
    boolean isLogining = false;
    UserModelImpl userModel = new UserModelImpl();

    @OnClick(R.id.btn_login)
    void login() {
        Intent it = new Intent(getContext(), LoginActivity.class);
        startActivityForResult(it, GlobalConfig.LOGING_PAGE);
    }

    @OnClick(R.id.lin_sign)
    void sign() {
        if (isLogining) {
            Intent it = new Intent(getContext(), SignActivity.class);
            startActivity(it);
        } else {
            login();
        }
    }

    @OnClick(R.id.lin_manager)
    void lin_manager() {
        if (isLogining) {
            Intent it = new Intent(getContext(), ManagerActivity.class);
            startActivity(it);
        } else {
            login();
        }
    }

    @OnClick(R.id.lin_live)
    void live() {
        if (isLogining) {
            User user = userModel.getUserInfo(getContext());
            if (user.getZhubo() != -1 && (user.getZhubo() == 1 || user.getZhubo() == 2)) {
                Intent it = new Intent(getContext(), PreLiveActivity.class);
                startActivity(it);
            } else {
                Intent it = new Intent(getContext(), AuthActivity.class);
                startActivity(it);
            }
        } else {
            login();
        }
    }
    @OnClick(R.id.userView)
    void userInfo() {
        if (isLogining) {
            Intent it = new Intent(getContext(), UserInfoActivity.class);
            startActivity(it);
        } else {
            login();
        }
    }
    @OnClick(R.id.lin_history)
    void history() {
        if (isLogining) {
            Intent it = new Intent(getContext(), HistoryActivity.class);
            startActivity(it);
        } else {
            login();
        }
    }


    @OnClick(R.id.lin_settings)
    void setting() {
        Intent it = new Intent(getContext(), SettingActivity.class);
        startActivity(it);
    }


    public static UserFragment newInstance(String name) {

        Bundle args = new Bundle();
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userPresenter = new UserPresenterImpl(this, getContext());
        userPresenter.updateView();
    }

    /**
     * 登录返回值
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                if (requestCode == GlobalConfig.LOGING_PAGE) {
                    Log.e("ttt", requestCode + "data");
                    userPresenter.updateView();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void updateView(User user, boolean isLogin) {
        //判断是否是登录状态
        if (isLogin) {
            mTvUserName.setText(user.getName());
            //显示礼物数
            mTvGift.setText(user.getGift());
            tv_sex.setText("性别：" + user.getSex());
            //使用Glide显示头像
            Glide.with(getContext()).load(user.getIcon()).placeholder(R.mipmap.ic_launcher_round).into(mCivPerson);
            mUserView.setVisibility(View.VISIBLE);
            isLogining = isLogin;
            //判断是否是管理员，如果是显示禁播
            if (user.getZhubo() == 2) {
                lin_manager.setVisibility(View.VISIBLE);
            }
        } else {
            mUserView.setVisibility(View.GONE);
            isLogining = isLogin;
        }
    }

    @Override
    public void returnInfo(String msg) {

    }

    @Override
    public void onResume() {
        super.onResume();
        userPresenter.getUserInfo(getContext());
    }


}
