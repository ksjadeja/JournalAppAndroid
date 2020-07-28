package com.journalapp.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccountBox implements Serializable {

    private Date timestamp;
    private String name;
    private String date;
    private String time;
    private int amount;
    private String desc;
    private String t_type;
    private String id;

    public AccountBox(){}
    public AccountBox(AccountBoxDao accountBoxDao, String key){
        name = accountBoxDao.getName().trim();
        amount = accountBoxDao.getAmount();
        desc = accountBoxDao.getDesc().trim();
        t_type = accountBoxDao.getT_type();
        id = key.trim();
        timestamp = accountBoxDao.getTimestamp().toDate();
        date = new SimpleDateFormat("dd-MM-yyyy").format(timestamp);
        time = new SimpleDateFormat("hh:mm:ss a").format(timestamp);
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name.trim();
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
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc.trim();
    }
    public String getT_type() {
        return t_type;
    }
    public void setT_type(String t_type) {
        this.t_type = t_type;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
