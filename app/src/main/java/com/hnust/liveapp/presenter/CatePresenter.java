package com.hnust.liveapp.presenter;

import android.content.Context;

import com.hnust.liveapp.R;
import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.bean.GsonCateInfo;
import com.hnust.liveapp.contract.CateContract;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yonglong on 2017/5/22.
 */

public class CatePresenter implements CateContract.Presenter {
    CateContract.View liveView;
    Context mContext;

    public CatePresenter(CateContract.View liveView, Context mContext) {
        this.liveView = liveView;
        this.mContext = mContext;
    }

    @Override
    public void getTypesInfo() {
        ApiManager.getInstence(mContext)
                .apiService.getTypes()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonCateInfo>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        liveView.returnError(mContext.getResources().getString(R.string.error_internet));
                    }

                    @Override
                    public void onNext(GsonCateInfo gsonCateInfo) {
                        if (gsonCateInfo.isStatus()) {
                            liveView.returnTypeData(gsonCateInfo.getData());
                        } else {
                            liveView.returnError(gsonCateInfo.getMessage());
                        }
                    }
                });
    }
}
