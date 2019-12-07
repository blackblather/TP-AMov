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
    private Integer value;

//------------> Initializers

    Element(Type type, Integer value){
        this.type = type;
        this.value = value;
    }

//------------> Getters

    public Type GetType() {
        return type;
    }

    public Integer GetValue() {
        return value;
    }

//------------> Setters

    public void SetType(Type type) {
        this.type = type;
    }

    void SetValue(Integer value) {
        this.value = value;
    }
}