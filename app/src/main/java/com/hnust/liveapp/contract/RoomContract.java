package com.hnust.liveapp.contract;

import com.hnust.liveapp.bean.CateInfo;
import com.hnust.liveapp.bean.GsonOpenFj;
import com.hnust.liveapp.bean.RoomInfo;

import java.util.List;

/**
 * Created by yonglong on 2017/5/23.
 */

public interface RoomContract {

    interface View {


        void returnError(String msg);

        //返回获取的关注信息
        void returnFocusInfo(boolean isFocus);

        //返回获取的直播信息
        void returnLiveInfo(GsonOpenFj gsonOpenFj);
    }

    interface Presenter {
        //获取房间信息
        void getRoomInfo(int room_id);

        //关注房间信息
        void focusRoom(int room_id);

        void lockRoom(int room_id);

    }
}
