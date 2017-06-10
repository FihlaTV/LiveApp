package com.hnust.liveapp.bean;

/**
 * Created by yonglong on 2017/5/23.
 */

public class GsonOpenFj {


    /**
     * data : {"info":{"room_id":"24","fj_name":"香港卫视","type":"卫视直播","username":"master","name":"cehis ","time":"2017-05-22 19:36:08","number":"1","dy_num":null,"now":"1","url":"rtmp://live.hkstv.hk.lxdns.com/live/hks","jturl":"https://cdn.zzsun.cc/zzsun-zhibo/images.png","avatar":"http://q.qlogo.cn/qqapp/101396171/E0E7444DAB81D844193696FA773CF442/100"},"status":false}
     * status : true
     * message : 请求成功
     */

    private DataBean data;
    private boolean status;
    private String message;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
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

    public static class DataBean {
        /**
         * info : {"room_id":"24","fj_name":"香港卫视","type":"卫视直播","username":"master","name":"cehis ","time":"2017-05-22 19:36:08","number":"1","dy_num":null,"now":"1","url":"rtmp://live.hkstv.hk.lxdns.com/live/hks","jturl":"https://cdn.zzsun.cc/zzsun-zhibo/images.png","avatar":"http://q.qlogo.cn/qqapp/101396171/E0E7444DAB81D844193696FA773CF442/100"}
         * status : false
         */

        private RoomInfo info;
        private boolean status;

        public RoomInfo getInfo() {
            return info;
        }

        public void setInfo(RoomInfo info) {
            this.info = info;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }


    }
}
