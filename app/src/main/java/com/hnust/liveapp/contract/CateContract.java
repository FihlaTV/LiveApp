package com.hnust.liveapp.contract;

import com.hnust.liveapp.bean.CateInfo;
import com.hnust.liveapp.bean.RoomInfo;

import java.util.List;

/**
 * Created by yonglong on 2017/5/22.
 */

public interface CateContract {

    public interface View {

        void returnError(String msg);

        //返回获取的直播信息
        void returnTypeData(List<CateInfo> types);
    }

    public interface Presenter {
        //发起获取分类
        void getTypesInfo();

    }
}
