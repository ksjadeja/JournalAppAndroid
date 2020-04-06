package com.journalapp.models;
import android.util.Log;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Feedbox implements Serializable {

    private Date timestamp;

    private String date,time,data,id;

    public Feedbox(){}

    public Feedbox(FeedboxDao feedboxDao, String key){

        this.setData(feedboxDao.getData());
        this.setId(key);
        this.timestamp = feedboxDao.getTimestamp().toDate();
        this.date = new SimpleDateFormat("dd-MM-yyyy").format(feedboxDao.getTimestamp().toDate());
        this.time = new SimpleDateFormat("hh:mm:ss a").format(feedboxDao.getTimestamp().toDate());
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
