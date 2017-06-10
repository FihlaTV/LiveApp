package com.hnust.liveapp.bean;

import java.util.List;

/**
 * Created by yonglong on 2017/4/17.
 */

public class GsonCateInfo {


    /**
     * data : [{"id":"14","type":"游戏"},{"id":"5","type":"校园活动"}]
     * status : true
     * message : 刷新栏目列表
     */

    private boolean status;
    private String message;
    private List<CateInfo> data;

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

    public List<CateInfo> getData() {
        return data;
    }

    public void setData(List<CateInfo> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GsonCateInfo{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
