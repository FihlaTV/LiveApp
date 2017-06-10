package com.hnust.liveapp.bean;

import java.util.List;

/**
 * Created by yonglong on 2017/4/17.
 */

public class GsonRoomInfo {

    /**
     * data : [{"room_id":"16","fj_name":"abcd","type":"你好","username":"333333","name":"223","time":"2017-05-09 01:11:04","number":"50000","dy_num":null,"now":"2"}]
     * status : true
     * message : 创建主播房间成功
     */

    private boolean status;
    private String message;
    private List<RoomInfo> data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RoomInfo> getData() {
        return data;
    }

    public void setData(List<RoomInfo> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GsonRoomInfo{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
