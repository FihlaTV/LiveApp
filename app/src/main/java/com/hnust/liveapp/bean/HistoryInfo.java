package com.hnust.liveapp.bean;

/**
 * Created by yonglong on 2017/4/17.
 */

public class HistoryInfo {

    private int id;
    private String name;
    private String title;
    private String category;
    private String address;
    private String url;
    private String time;

    public HistoryInfo() {
    }

    public HistoryInfo(int id, String name, String title, String category, String address, String url, String time) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.category = category;
        this.address = address;
        this.url = url;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "HistoryInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", address='" + address + '\'' +
                ", url='" + url + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
