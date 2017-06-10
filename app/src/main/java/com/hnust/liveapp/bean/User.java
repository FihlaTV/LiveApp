package com.hnust.liveapp.bean;

/**
 * Created by yonglong on 2017/3/28.
 */

public class User {
    /**
     * id : 25
     * username : 10010
     * password : aaaaaa
     * name : qqqqq
     * sex : qwe
     * icon : asdf
     * gift : 100
     * time : 2017-05-06 00:33:48
     * from : qq
     * zhubo : qq
     */

    private String id;
    private String username;
    private String password;
    private String name;
    private String sex;
    private String icon;
    private String gift;
    private String time;
    private String from;
    private int zhubo = -1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getZhubo() {
        return zhubo;
    }

    public void setZhubo(int zhubo) {
        this.zhubo = zhubo;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", icon='" + icon + '\'' +
                ", gift='" + gift + '\'' +
                ", time='" + time + '\'' +
                ", from='" + from + '\'' +
                ", zhubo='" + zhubo + '\'' +
                '}';
    }
}
