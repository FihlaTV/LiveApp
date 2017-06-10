package com.hnust.liveapp.contract;

import com.hnust.liveapp.bean.RoomInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by yonglong on 2017/5/23.
 */

public interface SignContract {
    interface View {
        //加载数据后更新
        void updateView();

        void returnError(String msg);

        //返回签到信息
        void returnSuccess(String msg);

    }

    interface Presenter {

        //发起获取直播请求
        void qiandao(int num);


        //发起获取直播请求
        void qiandao2();
    }
}
