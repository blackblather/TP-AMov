package com.tp_amov.models.sql;

import java.util.List;

public class Game {
    private int id;
    private GameMode gameMode;
    private List<UserGame> userGames;

    public Game(int id, GameMode gameMode){
        this.id = id;
        this.gameMode = gameMode;
    }

    public Game(GameMode gameMode){
        this.gameMode = gameMode;
    }

    //Getters
    public int getId() {
        return id;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public List<UserGame> getUserGames() {
        return userGames;
    }

    //Setter
    public void setUserGames(List<UserGame> userGames) {
        this.userGames = userGames;
    }
}
