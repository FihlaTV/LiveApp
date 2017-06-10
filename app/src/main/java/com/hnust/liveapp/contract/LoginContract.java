package com.hnust.liveapp.contract;

import android.content.Intent;

import com.hnust.liveapp.bean.User;

import java.util.Map;

/**
 * Created by yonglong on 2017/3/28.
 */

public interface LoginContract {
    interface View {

        //用户名格式不正确
        void onUsernameView(Boolean result, String message);

        //密码格式不正确
        void onPasswordView(Boolean result, String message);

        //登录结果
        void onLoginResult(Boolean result, String message);

        //进度条
        void onSetProgressBarVisibility(boolean visibility);

    }

    interface Presenter {

        void doLogin(Map<String, String> params);

        void setProgressBarVisiblity(boolean visiblity);
    }
}