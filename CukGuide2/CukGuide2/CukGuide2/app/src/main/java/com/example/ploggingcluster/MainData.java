package com.example.ploggingcluster;

import java.io.Serializable;

public class MainData implements Serializable {

    long id;
    String name;
    String title;
    String address;
    String content;
    int coin;
    String picture;
    String createDateStr;

    public MainData(long id, String name, String title, String address, String content, int coin, String imagePath, String createDateStr) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.address = address;
        this.content = content;
        this.picture = imagePath;
        this.coin = coin;
        this.createDateStr = createDateStr;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }
}
