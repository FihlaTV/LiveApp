package com.hnust.liveapp.presenter;

import android.content.Context;
import android.util.Log;

import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.bean.GsonInfo;
import com.hnust.liveapp.contract.RegisterContract;
import com.hnust.liveapp.util.GlobalConfig;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yonglong on 2017/04/30
 */

public class RegisterPresenterImpl implements RegisterContract.Presenter {

    private RegisterContract.View registerView;
    private Context context;

    private int code;
    private boolean result = false;

    public RegisterPresenterImpl(RegisterContract.View context) {
        this.registerView = context;
        this.context = (Context) context;
    }

    @Override
    public void doRegister(String name, String nick, String passwd) {
        ApiManager.getInstence(context).apiService.register(name, nick, passwd)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.e(GlobalConfig.TAG, "ONC--");
//                        registerView.onResult(true, s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(GlobalConfig.TAG, "onError--" + e.getMessage());
                        registerView.onResult(false, "服务器异常");
                        setProgressBarVisiblity(false);
                    }

                    @Override
                    public void onNext(GsonInfo gsonInfo) {
                        Log.e(GlobalConfig.TAG, "onNext--" + gsonInfo.toString());
                        registerView.onResult(gsonInfo.getStatus(), gsonInfo.getMessage());
                        setProgressBarVisiblity(false);
                    }
                });
    }

    @Override
    public void setProgressBarVisiblity(boolean visiblity) {
        registerView.onSetProgressBarVisibility(visiblity);
    }
}