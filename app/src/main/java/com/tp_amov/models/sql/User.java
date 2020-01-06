package com.tp_amov.models.sql;

import java.io.Serializable;

public class User implements Serializable {
    private long id;
    private String username;
    private String imagePath;

    public User(int id, String username, String imagePath){
        this.id = id;
        this.username = username;
        this.imagePath = imagePath;
    }

    public User(String username, String imagePath){
        this.username = username;
        this.imagePath = imagePath;
    }

    //Getters
    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImagePath() {
        return imagePath;
    }

    //Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
