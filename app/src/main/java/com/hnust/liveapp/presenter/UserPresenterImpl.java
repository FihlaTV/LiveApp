package com.hnust.liveapp.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.bean.GsonInfo;
import com.hnust.liveapp.bean.GsonUserInfo;
import com.hnust.liveapp.bean.User;
import com.hnust.liveapp.contract.UserContract;
import com.hnust.liveapp.model.UserModelImpl;
import com.hnust.liveapp.ui.activitys.UserInfoActivity;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yonglong on 2017/03/29
 */

public class UserPresenterImpl implements UserContract.Presenter {

    UserContract.View userView;
    UserModelImpl userModel;
    Context mContext;


    public UserPresenterImpl(UserContract.View userView, Context context) {
        this.userView = userView;
        this.mContext = context;
        this.userModel = new UserModelImpl();
    }


    /**
     * 获取用户信息
     *
     * @param mContext
     */
    @Override
    public void getUserInfo(final Context mContext) {
        ApiManager.getInstence(mContext)
                .apiService.getUserInfo()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonUserInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        userView.returnInfo("网络异常，请检查网络");
                    }

                    @Override
                    public void onNext(GsonUserInfo gsonGetUserInfo) {
                        Log.e("UserPresenterImpl", gsonGetUserInfo.toString());
                        if (gsonGetUserInfo.isStatus()) {
                            userModel.setUserInfo(mContext, gsonGetUserInfo.getData());
                            updateView();
                        } else {
                            userView.returnInfo(gsonGetUserInfo.getMessage());
                        }
                    }
                });
    }

    @Override
    public void updateView() {
        User user = userModel.getUserInfo(mContext);
        Log.e("updateView", user.toString());
        if (user.getUsername() != null) {
            userView.updateView(user, true);
        } else {
            userView.updateView(user, false);
        }
    }

    /**
     * 上传文件
     *
     * @param imgpath
     */
    @Override
    public void uploadFile(String imgpath) {
        //上传
        final File file = new File(imgpath);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);

        ApiManager apiManager = ApiManager.getInstence(mContext);
        apiManager.apiService.uploadFile(requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.e("UserInfoActivity", "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("UserInfoActivity--e", e.getMessage());
                        userView.returnInfo(mContext.getResources().getString(R.string.error_internet));

                    }

                    @Override
                    public void onNext(GsonInfo gsonInfo) {
                        Log.e("UserInfoActivity", gsonInfo.toString());
                        if (gsonInfo.getStatus()) {
                            getUserInfo(mContext);
                            userView.returnInfo(gsonInfo.getMessage());
                        } else {
                            userView.returnInfo(gsonInfo.getMessage());
                        }

                    }
                });
    }

    /**
     * 修改用户信息
     *
     * @param params
     * @param flag
     */
    @Override
    public void updateUserInfo(Map<String, String> params, int flag) {
        ApiManager apiManager = ApiManager.getInstence(mContext);
        apiManager.apiService.xiugai(params)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.e("UserInfoActivity", "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("UserInfoActivity", e.getMessage());
                        userView.returnInfo(mContext.getResources().getString(R.string.error_internet));
                    }

                    @Override
                    public void onNext(GsonInfo gsonInfo) {
                        if (gsonInfo.getStatus()) {
                            getUserInfo(mContext);
                            userView.returnInfo(gsonInfo.getMessage());
                        } else {
                            userView.returnInfo(gsonInfo.getMessage());
                        }
                    }
                });
    }

}