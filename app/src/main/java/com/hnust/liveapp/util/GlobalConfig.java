package com.hnust.liveapp.util;

import okhttp3.HttpUrl;

/**
 * Created by yonglong on 2017/3/29.
 */

public class GlobalConfig {
    //    public static final String CHAT_SERVER_URL = "http://192.168.123.39:29005/";
//    public static final String CHAT_SERVER_URL = "https://socketio-chat.now.sh/";
    public static final String CHAT_SERVER_URL = "http://192.168.123.39:29005/";

    public static final String LIVE_RTMP_TL = "rtmp://live-bd-tl.zzsun.cc/live/stream";

    public static final String BASE_SERVER_URL = "http://192.168.123.50";
//    public static final String BASE_SERVER_URL = "http://live-api.zzsun.cc";
//    public static HttpUrl baseUrl="http://www.bai";

    public static final String APP_ID = "101395613";
    public static final String TAG = "ceshi";

    public static final int ROOMS_MAIN = 0;
    public static final int ROOMS_LIVE = 1;
    public static final int ROOMS_FOCUS = 2;
    public static final int ROOMS_HISTORY = 3;

    //登录requestCode
    public static int LOGING_PAGE = 101;
    /**
     * 登录状态错误代码
     */
    public static int LOGIN_SUCCESS = 1001;
    //密码不合法
    public static int LOGIN_FAIL_INVAILD = 1002;
    //用户名为空
    public static int LOGIN_FAIL_NAME_ERROR = 1003;
    //密码错误
    public static int LOGIN_FAIL_PASSWD_ERROR = 1004;
    //服务器错误
    public static int LOGIN_FAIL_SERVER_ERROR = 1005;

}
