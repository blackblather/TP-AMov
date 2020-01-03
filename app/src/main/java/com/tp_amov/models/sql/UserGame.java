package com.tp_amov.models.sql;

public class UserGame {
    private int id;
    private Game game;
    private User user;
    private int score;

    public UserGame(int id, Game game, User user, int score){
        this.id = id;
        this.game = game;
        this.user = user;
        this.score = score;
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

    public int getScore() {
        return score;
    }
}
