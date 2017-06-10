package com.hnust.liveapp.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.bean.GsonUserInfo;
import com.hnust.liveapp.bean.User;
import com.hnust.liveapp.contract.LoginContract;
import com.hnust.liveapp.model.UserModelImpl;
import com.hnust.liveapp.util.GlobalConfig;
import com.hnust.liveapp.util.Md5Util;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by yonglong on 2017/03/28
 */

public class LoginPresenterImpl implements LoginContract.Presenter {
    private UserModelImpl userModel;
    private LoginContract.View loginView;
    private Context context;


    public LoginPresenterImpl(LoginContract.View loginView) {
        this.loginView = loginView;
        this.context = (Context) loginView;
        this.userModel = new UserModelImpl();
    }


    @Override
    public void doLogin(Map<String, String> params) {

        setProgressBarVisiblity(true);
        ApiManager apiManager = ApiManager.getInstence(context);
        apiManager.apiService.login(params)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonUserInfo>() {
                    @Override
                    public void onCompleted() {
                        setProgressBarVisiblity(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Login", e.getMessage());
                        loginView.onLoginResult(false, "请检查网络是否连接正常");
                        setProgressBarVisiblity(false);
                    }

                    @Override
                    public void onNext(GsonUserInfo gsonUserInfo) {
                        if (gsonUserInfo.isStatus()) {
                            User user = gsonUserInfo.getData();
                            userModel.setUserInfo(context, user);
                            loginView.onLoginResult(true, gsonUserInfo.getMessage());
                        } else {
                            loginView.onLoginResult(false, gsonUserInfo.getMessage());
                        }
                    }
                });

    }

    @Override
    public void setProgressBarVisiblity(boolean visiblity) {
        loginView.onSetProgressBarVisibility(visiblity);
    }


}