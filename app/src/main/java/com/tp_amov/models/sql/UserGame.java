package com.tp_amov.models.sql;

public class UserGame {
    private int id;
    private Game game;
    private User user;

    public UserGame(int id, Game game, User user){
        this.id = id;
        this.game = game;
        this.user = user;
    }

    //Getters
    public int getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public User getUser() {
        return user;
    }
}
