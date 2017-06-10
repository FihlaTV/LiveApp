package com.hnust.liveapp.bean;

/**
 * Created by yonglong on 2017/4/17.
 */

public class GsonUserInfo {
    /**
     * data : {"id":"61","username":"master","password":"master","name":"ceshi","sex":"1","icon":"1","gift":"100","time":"2017-05-18 14:26:31","from":"","zhubo":null}
     * status : true
     * message : 登录成功
     */

    private User data;
    private boolean status;
    private String message;

    public User getData() {
        return data;
    }

    public void setData(User data) {
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
        return "GsonUserInfo{" +
                "data=" + data +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
