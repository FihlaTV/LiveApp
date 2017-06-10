package com.hnust.liveapp.bean;

/**
 * Created by yonglong on 2017/4/17.
 */

public class RoomInfo {


    /**
     * room_id : 24
     * fj_name : 香港卫视
     * type : 卫视直播
     * username : master
     * name : cehis
     * time : 2017-05-22 19:36:08
     * number : null
     * dy_num : null
     * now : 1
     * url : rtmp://live.hkstv.hk.lxdns.com/live/hks
     * jturl : https://cdn.zzsun.cc/zzsun-zhibo/images.png
     */

    private int room_id;
    private String fj_name;
    private String type;
    private String username;
    private String name;
    private String time;
    private int number;
    private int dy_num;
    private int now;
    private String url;
    private String jturl;
    private String content;

    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getFj_name() {
        return fj_name;
    }

    public void setFj_name(String fj_name) {
        this.fj_name = fj_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Object getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Object getDy_num() {
        return dy_num;
    }

    public void setDy_num(int dy_num) {
        this.dy_num = dy_num;
    }

    public int getNow() {
        return now;
    }

    public void setNow(int now) {
        this.now = now;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJturl() {
        return jturl;
    }

    public void setJturl(String jturl) {
        this.jturl = jturl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RoomInfo{" +
                "room_id=" + room_id +
                ", fj_name='" + fj_name + '\'' +
                ", type='" + type + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", number=" + number +
                ", dy_num=" + dy_num +
                ", now=" + now +
                ", url='" + url + '\'' +
                ", jturl='" + jturl + '\'' +
                ", content='" + content + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
