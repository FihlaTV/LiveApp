package com.hnust.liveapp.presenter;

import android.content.Context;

import com.hnust.liveapp.R;
import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.bean.GsonInfo;
import com.hnust.liveapp.bean.GsonRoomInfo;
import com.hnust.liveapp.contract.HistoryContract;
import com.hnust.liveapp.contract.ManagerContract;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yonglong on 2017/5/27.
 */

public class ManagerPresenter implements ManagerContract.Presenter {
    ManagerContract.View historyView;
    Context mContext;

    public ManagerPresenter(ManagerContract.View historyView, Context mContext) {
        this.historyView = historyView;
        this.mContext = mContext;
    }

    @Override
    public void getHistory() {
        ApiManager.getInstence(mContext)
                .apiService
                .getLock()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonRoomInfo>() {
                    @Override
                    public void onCompleted() {
                        historyView.updateView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        historyView.returnError(mContext.getResources().getString(R.string.error_internet));
                    }

                    @Override
                    public void onNext(GsonRoomInfo gsonRoomInfo) {
                        if (gsonRoomInfo.isStatus()) {
                            historyView.returnLivesListData(gsonRoomInfo.getData());
                        } else {
                            historyView.returnError(gsonRoomInfo.getMessage());
                        }
                    }
                });

    }

    @Override
    public void clearHistory() {
        ApiManager.getInstence(mContext)
                .apiService
                .clearhistory()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonInfo>() {
                    @Override
                    public void onCompleted() {
                        historyView.updateView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        historyView.returnError(mContext.getResources().getString(R.string.error_internet));
                    }

                    @Override
                    public void onNext(GsonInfo gsonRoomInfo) {
                        if (gsonRoomInfo.getStatus()) {
                            historyView.returnLivesListData(null);
                        } else {
                            historyView.returnError(gsonRoomInfo.getMessage());
                        }
                    }
                });
    }

    @Override
    public void getMoreDatas() {

    }
}
