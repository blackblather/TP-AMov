package com.tp_amov.models.sql;

public class User {
    private int id;
    private String username;
    private String profilePicture;

    public User(int id, String username, String profilePicture){
        this.id = id;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    //Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    //Setters

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
