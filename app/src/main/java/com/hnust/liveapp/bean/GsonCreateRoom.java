package com.hnust.liveapp.bean;

/**
 * Created by yonglong on 2017/5/23.
 */

public class GsonCreateRoom {


    /**
     * data : {"room_id":"28","fj_name":"123","type":"卫视直播","username":"ttttt","name":"eee","time":"2017-05-23 23:10:47","number":"0","dy_num":"0","now":null,"url":"rtmp://zb.zzsun.cc:1935/live/28","jturl":"https://cdn.zzsun.cc/zzsun-zhibo/live/28"}
     * status : true
     * message : 创建主播房间成功
     */

    private RoomInfo data;
    private boolean status;
    private String message;

    public RoomInfo getData() {
        return data;
    }

    public void setData(RoomInfo data) {
        this.data = data;
    }

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

    @Override
    public String toString() {
        return "GsonCreateRoom{" +
                "data=" + data +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
