package com.hnust.liveapp.bean;

/**
 * Created by yonglong on 2017/5/22.
 */

public class CateInfo {

    /**
     * id : 14
     * type : 游戏
     */

    private int id;
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CateInfo{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
