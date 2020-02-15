package com.journalapp.models;

public class AccountBox {

    private String name;
    private String date;
    private String time;
    private String amount;
    private String desc;
    private String t_type;
    private String id;

    public AccountBox(){}

    public AccountBox(AccountBoxDao accountBoxDao, String key){
        this.setDate(accountBoxDao.getDate());
        this.setTime(accountBoxDao.getTime());
        this.setName(accountBoxDao.getName());
        this.setAmount(accountBoxDao.getAmount());
        this.setDesc(accountBoxDao.getDesc());
        this.setT_type(accountBoxDao.getT_type());
        this.setId(key);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
