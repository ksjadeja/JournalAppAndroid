package com.journalapp.models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class FeedboxDao {

    private Timestamp timestamp;
    private String data;

    public FeedboxDao(){}

    public FeedboxDao(Feedbox feedbox){

        if (feedbox.getTimestamp()!=null){
            timestamp = new Timestamp(feedbox.getTimestamp());
        }else {
            timestamp = new Timestamp(new Date());
        }
        data = feedbox.getData();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
