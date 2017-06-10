package com.hnust.liveapp.contract;

import com.hnust.liveapp.bean.RoomInfo;

import java.util.List;

/**
 * Created by yonglong on 2017/5/23.
 */

public interface FocusContract {
    interface View {

        //加载数据后更新
        void updateView();

        void returnError(String msg);

        //返回获取的直播信息
        void returnLivesListData(List<RoomInfo> lives);
    }

    interface Presenter {
        //发起获取直播请求
        void getRoomsInfo();

        //发起获取更多直播请求
        void getMoreDatas();
    }
}
