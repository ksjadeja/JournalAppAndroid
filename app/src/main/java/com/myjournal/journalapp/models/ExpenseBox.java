package com.myjournal.journalapp.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseBox implements Serializable {

    private Date timestamp;
    private String itemName;
    private int amount;
    private String desc;
    private String date;
    private String time;
    private String id;

    public ExpenseBox(){}

    public ExpenseBox(ExpenseBoxDao expenseBoxDao, String key){
        itemName= expenseBoxDao.getItemName().trim();
        amount = expenseBoxDao.getAmount();
        desc = expenseBoxDao.getDesc().trim();
        id = key;
        timestamp = expenseBoxDao.getTimestamp().toDate();
        date = new SimpleDateFormat("dd-MM-yyyy").format(timestamp);
        time = new SimpleDateFormat("hh:mm:ss a").format(timestamp);
    }
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String name) {
        this.itemName= name.trim();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.trim();
    }
}
