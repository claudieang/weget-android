package com.wegot.fuyan.fyp;

import java.io.Serializable;

/**
 * Created by FU YAN on 7/13/2016.
 */
public class Account implements Serializable {
    int id;
    String username;
    String password;
    int contactNo;
    String email;
    String fulfiller;
    String picture;

    public Account(int id, String username, String password, int contactNo, String email, String fulfiller, String picture){
        this.id = id;
        this.username = username;
        this.password = password;
        this.contactNo = contactNo;
        this.email = email;
        this.fulfiller = fulfiller;
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getContactNo() {
        return contactNo;
    }

    public void setContactNo(int contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFulfiller() {
        return fulfiller;
    }

    public void setFulfiller(String fulfiller) {
        this.fulfiller = fulfiller;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
