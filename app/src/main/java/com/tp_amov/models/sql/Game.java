package com.tp_amov.models.sql;

public class Game {
    private int id;
    private GameMode gameMode;

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
}
