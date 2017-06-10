package com.hnust.liveapp.presenter;

import android.content.Context;

import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.bean.GsonCateInfo;
import com.hnust.liveapp.bean.GsonInfo;
import com.hnust.liveapp.bean.GsonOpenFj;
import com.hnust.liveapp.contract.RoomContract;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yonglong on 2017/5/23.
 */

public class RoomPresenter implements RoomContract.Presenter {

    RoomContract.View roomView;
    Context context;

    public RoomPresenter(Context context, RoomContract.View roomView) {
        this.context = context;
        this.roomView = roomView;
    }

    @Override
    public void getRoomInfo(final int room_id) {
        ApiManager.getInstence(context)
                .apiService.openFJ(room_id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonOpenFj>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        roomView.returnError("网络异常，请检查网络");
                    }

                    @Override
                    public void onNext(GsonOpenFj gsonOpenFj) {
                        if (gsonOpenFj.isStatus()) {
//                            roomView.returnFocusInfo(gsonOpenFj.getData().isStatus());
                            roomView.returnLiveInfo(gsonOpenFj);
                        } else {
                            roomView.returnError(gsonOpenFj.getMessage());
                        }
                    }
                });
    }

    @Override
    public void focusRoom(int room_id) {
        ApiManager.getInstence(context)
                .apiService.guanzhu(room_id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonInfo>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        roomView.returnError("网络异常，请检查网络");
                    }

                    @Override
                    public void onNext(GsonInfo gsonInfo) {
                        if (gsonInfo.getStatus()) {
                            if (gsonInfo.getMessage().equals("取消关注")) {
                                roomView.returnFocusInfo(false);
                            } else if (gsonInfo.getMessage().equals("关注成功")) {
                                roomView.returnFocusInfo(true);
                            }
                        } else {
                            roomView.returnError(gsonInfo.getMessage());
                        }
                    }
                });
    }

    @Override
    public void lockRoom(int room_id) {
        ApiManager.getInstence(context)
                .apiService.lockRoom(room_id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonInfo>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        roomView.returnError("网络异常，请检查网络");
                    }

                    @Override
                    public void onNext(GsonInfo gsonInfo) {
                        if (gsonInfo.getStatus()) {
                            roomView.returnError(gsonInfo.getMessage());
                        } else {
                            roomView.returnError(gsonInfo.getMessage());
                        }
                    }
                });
    }

}
