package com.journalapp.models;



import com.google.firebase.Timestamp;

import java.util.Date;

public class AccountBoxDao{

    private Timestamp timestamp;
    private String name;
    private int amount;
    private String desc;
    private String t_type;
//    private static final int GIVE=0;
//    private static final int TAKE=1;

    public AccountBoxDao(){}
    public AccountBoxDao(AccountBox accountBox){
        if(accountBox.getTimestamp()!=null){
            timestamp = new Timestamp(accountBox.getTimestamp());
        }
        else{
            timestamp = new Timestamp(new Date());
        }
        name = accountBox.getName();
        amount = accountBox.getAmount();
        desc = accountBox.getDesc();
        t_type = accountBox.getT_type();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        this.desc = desc;
    }

    public String getT_type() {
        return t_type;
    }

    public void setT_type(String t_type) {
        this.t_type = t_type;
    }
}
