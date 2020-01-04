package com.tp_amov.models.sql;

import java.util.List;

public class User {
    private int id;
    private String username;
    private String profilePicture;
    private List<UserGame> userGames;

    public User(int id, String username, String profilePicture, List<UserGame> userGames){
        this.id = id;
        this.username = username;
        this.profilePicture = profilePicture;
        this.userGames = userGames;
    }

    public User(int id, String username, String profilePicture){
        this.id = id;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public User(String username, String profilePicture){
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

    public List<UserGame> getUserGames() {
        return userGames;
    }

    //Setters

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
