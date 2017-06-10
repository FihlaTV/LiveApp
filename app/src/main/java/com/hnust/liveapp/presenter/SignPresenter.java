package com.hnust.liveapp.presenter;

import android.content.Context;
import android.util.Log;

import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.bean.GsonInfo;
import com.hnust.liveapp.bean.GsonRoomInfo;
import com.hnust.liveapp.bean.GsonUserInfo;
import com.hnust.liveapp.contract.SignContract;
import com.hnust.liveapp.model.UserModelImpl;

import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yonglong on 2017/5/23.
 */

public class SignPresenter implements SignContract.Presenter {

    SignContract.View signView;
    Context mContext;
    UserModelImpl userModel = new UserModelImpl();


    public SignPresenter(Context mContext, SignContract.View signView) {
        this.mContext = mContext;
        this.signView = signView;
    }

    @Override
    public void qiandao(int num) {
        ApiManager.getInstence(mContext)
                .apiService.qiandao(num)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonInfo>() {

                    @Override
                    public void onCompleted() {
                        signView.updateView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        signView.returnError("网络异常，请检查网络");
                    }

                    @Override
                    public void onNext(GsonInfo gsonInfo) {
                        if (gsonInfo.getStatus()) {
                            signView.returnSuccess(gsonInfo.getMessage());
                        } else {
                            signView.returnError(gsonInfo.getMessage());
                        }
                    }
                });
    }

    @Override
    public void qiandao2() {
        ApiManager.getInstence(mContext)
                .apiService.qiandao2()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonInfo>() {

                    @Override
                    public void onCompleted() {
                        signView.updateView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        signView.returnError("网络异常，请检查网络");
                    }

                    @Override
                    public void onNext(GsonInfo gsonInfo) {
                        if (gsonInfo.getStatus()) {
                            signView.returnSuccess(gsonInfo.getMessage());
                        } else {
                            signView.returnError(gsonInfo.getMessage());
                        }
                    }
                });
    }


}
