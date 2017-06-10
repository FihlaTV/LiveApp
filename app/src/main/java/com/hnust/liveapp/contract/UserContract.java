package com.hnust.liveapp.contract;

import android.content.Context;

import com.hnust.liveapp.bean.User;

import java.util.Map;

/**
 * Created by yonglong on 2017/3/28.
 */

public class UserContract {

    public interface View {
        //更新登录状态
        void updateView(User user, boolean isLogin);

        void returnInfo(String msg);
    }

    public interface Presenter {

        void getUserInfo(Context mContext);

        void updateView();

        void uploadFile(String imgpath);

        void updateUserInfo(Map<String, String> params, final int flag);
    }

    public interface Model {
        User getUserInfo(Context mContext);

        void setUserInfo(Context mContext, User userInfo);

        void clearInfo(Context mContext);
    }


}