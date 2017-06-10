package com.hnust.liveapp.presenter;

import android.content.Context;
import android.util.Log;

import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.bean.GsonRoomInfo;
import com.hnust.liveapp.contract.FocusContract;
import com.hnust.liveapp.contract.LivesContract;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yonglong on 2017/5/23.
 */

public class FocusPresenterImpl implements FocusContract.Presenter {
    FocusContract.View focusView;
    Context mContext;

    public FocusPresenterImpl(FocusContract.View focusView, Context mContext) {
        this.focusView = focusView;
        this.mContext = mContext;
    }

    @Override
    public void getRoomsInfo() {

        ApiManager.getInstence(mContext).apiService
                .getGuanzhu()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonRoomInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.e("onCompleted", "----");
                        focusView.updateView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError-----", e.getMessage());
                        focusView.returnError("网络异常，请检查网络");
                    }

                    @Override
                    public void onNext(GsonRoomInfo gsonRoomInfo) {
                        Log.e("onNext", gsonRoomInfo.toString());
                        if (gsonRoomInfo.isStatus()) {
                            focusView.returnLivesListData(gsonRoomInfo.getData());
                        } else {
                            focusView.returnError(gsonRoomInfo.getMessage());
                        }
                    }
                });
    }

    @Override
    public void getMoreDatas() {
        focusView.updateView();
    }
}
