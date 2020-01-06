package com.tp_amov.models.sql;

import java.util.List;

public class Game {
    private long id;
    private GameMode gameMode;
    private List<UserGame> userGames;

    public Game(long id, GameMode gameMode){
        this.id = id;
        this.gameMode = gameMode;
    }

    public Game(GameMode gameMode){
        this.gameMode = gameMode;
    }

    //Getters
    public long getId() {
        return id;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public List<UserGame> getUserGames() {
        return userGames;
    }

    //Setter
    public void setId(long id){
        this.id = id;
    }

    public void setUserGames(List<UserGame> userGames) {
        this.userGames = userGames;
    }
}