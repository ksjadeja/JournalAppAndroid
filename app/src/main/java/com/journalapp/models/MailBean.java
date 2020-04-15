package com.journalapp.models;

import com.google.firebase.firestore.Exclude;

public class MailBean {

    @Exclude
    private String key;

    private String personName;
    private String email;
    private boolean EmailEntered;



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public boolean getEmailEntered() {
        return EmailEntered;
    }

    public void setEmailEntered(boolean emailEntered) {
        EmailEntered = emailEntered;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
