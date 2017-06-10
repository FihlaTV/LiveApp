package com.hnust.liveapp.presenter;

import android.content.Context;
import android.util.Log;

import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.bean.GsonRoomInfo;
import com.hnust.liveapp.contract.LivesContract;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yonglong on 2017/04/17
 */

public class LivesPresenterImpl implements LivesContract.Presenter {
    LivesContract.View liveView;
    Context mContext;

    public LivesPresenterImpl(LivesContract.View liveView, Context mContext) {
        this.liveView = liveView;
        this.mContext = mContext;
    }

    @Override
    public void getRoomsInfo(String type) {

        ApiManager.getInstence(mContext).apiService
                .getRooms(type)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonRoomInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.e("onCompleted", "----");
                        liveView.updateView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError-----", e.getMessage());
                        liveView.returnError("网络异常，请检查网络");
                    }

                    @Override
                    public void onNext(GsonRoomInfo gsonRoomInfo) {
                        Log.e("onNext", gsonRoomInfo.toString());
                        if (gsonRoomInfo.isStatus()) {
                            liveView.returnLivesListData(gsonRoomInfo.getData());
                        } else {
                            liveView.returnError(gsonRoomInfo.getMessage());
                        }
                    }
                });
    }

    @Override
    public void getMoreDatas() {
        liveView.updateView();
    }
}