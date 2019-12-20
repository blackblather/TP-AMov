package com.tp_amov.models.board;

public class Element{
//Enums
    public enum Type{
        defaultValue,
        userValue,
        hintValue
    }
//Control vars
    private Type type;
    private int value;

//------------> Initializers

    Element(Type type, int value){
        this.type = type;
        this.value = value;
    }

//------------> Getters

    public Type GetType() {
        return type;
    }

    public int GetValue() {
        return value;
    }

//------------> Setters

    public void SetType(Type type) {
        this.type = type;
    }

    void SetValue(int value) {
        this.value = value;
    }
}