package com.journalapp.models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class ExpenseBoxDao {
    private Timestamp timestamp;

    private String itemName;
    private String amount;
    private String desc;
    private String date;
    private String time;
    public ExpenseBoxDao(){}
    public ExpenseBoxDao(ExpenseBox expenseBox){
        if(expenseBox.getTimestamp()!=null){
            timestamp = new Timestamp(expenseBox.getTimestamp());
        }
        else{
            timestamp = new Timestamp(new Date());
        }
        itemName = expenseBox.getItemName();
        amount = expenseBox.getAmount();
        desc = expenseBox.getDesc();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public void setTimeStampp(Date date){
        if(timestamp==null) {
            timestamp = new Timestamp(new Date());
        }
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
        this.itemName= name;
    }

    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

}
