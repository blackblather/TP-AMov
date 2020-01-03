package com.tp_amov.models.sql;

public class User {
    private String username;
    private String profilePicture;

    public User(String username, String profilePicture){
        this.username = username;
        this.profilePicture = profilePicture;
    }

    //Getters

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
