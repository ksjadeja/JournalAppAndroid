package com.journalapp.models;

public class Feedbox {
    private String date,time,data,id;

    public Feedbox(){}

    public Feedbox(FeedboxDao feedboxDao, String key){
        this.setDate(feedboxDao.getDate());
        this.setTime(feedboxDao.getTime());
        this.setData(feedboxDao.getData());
        this.setId(key);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }
}
