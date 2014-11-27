package com.klinker.android.twitter;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class RegistrationRecord {

    @Id
    Long id;

    @Index
    private String regId;
    @Index
    private String username;

    public RegistrationRecord() { }

    public String getRegId() {
        return regId;
    }

    public String getUsername() {
        return username;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}