package com.tp_amov.models.sql;

public class GameMode {
    private int id;
    private String name;

    public GameMode(int id, String name){
        this.id = id;
        this.name = name;
    }

    //Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
