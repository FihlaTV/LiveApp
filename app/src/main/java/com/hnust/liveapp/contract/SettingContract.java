package com.hnust.liveapp.contract;

/**
 * Created by yonglong on 2017/5/28.
 */

public interface SettingContract {

    interface View {
        void logout(String message);

        void returnError(String message);

        void showlogout(boolean isLogin);
    }

    interface Presenter {
        void logout();

        void getLoginStatus();
    }
}
