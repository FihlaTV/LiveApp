package com.hnust.liveapp.bean;

/**
 * Created by yonglong on 2017/4/17.
 */

public class GsonInfo {

    /**
     * status : true
     * message : 注册失败
     */

    private boolean status;
    private String message;
    private String data;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "GsonInfo{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
