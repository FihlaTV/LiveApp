package com.hnust.liveapp.presenter;

import android.content.Context;

import com.hnust.liveapp.R;
import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.api.ApiManagerService;
import com.hnust.liveapp.bean.GsonInfo;
import com.hnust.liveapp.contract.SettingContract;
import com.hnust.liveapp.model.UserModelImpl;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yonglong on 2017/5/28.
 */

public class SettingPresenter implements SettingContract.Presenter {
    SettingContract.View settingView;
    Context mContext;
    UserModelImpl userModel = new UserModelImpl();

    public SettingPresenter(Context mContext, SettingContract.View settingView) {
        this.mContext = mContext;
        this.settingView = settingView;
    }

    @Override
    public void logout() {
        ApiManager.getInstence(mContext)
                .apiService.logout()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonInfo>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        settingView.returnError(mContext.getString(R.string.error_internet));
                    }

                    @Override
                    public void onNext(GsonInfo gsonInfo) {
                        if (gsonInfo.getStatus()) {
                            userModel.clearInfo(mContext);
                            settingView.logout(gsonInfo.getMessage());
                            settingView.showlogout(false);
                        } else {
                            settingView.returnError(gsonInfo.getMessage());
                        }
                    }
                });
    }

    @Override
    public void getLoginStatus() {
        String username = userModel.getUserInfo(mContext).getUsername();
        if (username == null) {
            settingView.showlogout(false);
        } else {
            settingView.showlogout(true);
        }

    }
}
