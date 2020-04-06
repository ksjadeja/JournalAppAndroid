package com.journalapp.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseBox {

    private Date timestamp;
    private String itemName;
    private String amount;
    private String desc;
    private String date;
    private String time;
    private String id;

    public ExpenseBox(){}

    public ExpenseBox(ExpenseBoxDao expenseBoxDao, String key){
        itemName= expenseBoxDao.getItemName();
        amount = expenseBoxDao.getAmount();
        desc = expenseBoxDao.getDesc();
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
//
//    public ExpenseBoxDao getExpenseBoxDao() {
//        return expenseBoxDao;
//    }
//
//    public void setExpenseBoxDao(ExpenseBoxDao expenseBoxDao) {
//        this.expenseBoxDao = expenseBoxDao;
//    }

//    private ExpenseBoxDao expenseBoxDao;

//    public ExpenseBox(ExpenseBoxDao expenseBoxDao, String key) {
//        this.setExpenseBoxDao(expenseBoxDao);
//        this.setDate(expenseBoxDao.getDate());
//        this.setTime(expenseBoxDao.getTime());
//        this.setItemName(expenseBoxDao.getItemName());
//        this.setAmount(expenseBoxDao.getAmount());
//        this.setDesc(expenseBoxDao.getDesc());
//        this.setId(key);
//    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
