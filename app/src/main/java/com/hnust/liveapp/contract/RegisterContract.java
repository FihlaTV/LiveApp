package com.hnust.liveapp.contract;

/**
 * Created by yonglong on 2017/4/30.
 */

public class RegisterContract {

    public interface View {
        //注册结果
        void onResult(Boolean result, String msg);

        //进度条
        void onSetProgressBarVisibility(boolean visibility);

    }

    public interface Presenter {

        void doRegister(String name, String nick, String passwd);

        void setProgressBarVisiblity(boolean visiblity);
    }

    public interface Model {
    }

}