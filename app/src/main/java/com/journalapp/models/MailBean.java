package com.journalapp.models;

import com.google.firebase.firestore.Exclude;

public class MailBean {

//    @Exclude
    private String key;
    private String personName;
    private String email;
    private boolean emailEntered;

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key.trim();
    }
    public boolean getEmailEntered() {
        return emailEntered;
    }

    public void setEmailEntered(boolean emailEntered) {
        this.emailEntered = emailEntered;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }
}
