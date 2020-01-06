package com.tp_amov.models.sql;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private long id;
    private String username;
    private String imagePath;
    private List<UserGame> userGames;

    public User(long id, String username, String imagePath, List<UserGame> userGames){
        this.id = id;
        this.username = username;
        this.imagePath = imagePath;
        this.userGames = userGames;
    }

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

    public List<UserGame> getUserGames() {
        return userGames;
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

    public void setUserGames(List<UserGame> userGames) {
        this.userGames = userGames;
    }
}
