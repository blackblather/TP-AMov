package com.tp_amov.models.board;

public class BoardPosition {
    private Integer innerBoardIndex;
    private Integer cellIndex;
    private Integer value;

    public BoardPosition(){ }

    public BoardPosition(Integer innerBoardIndex, Integer cellIndex, Integer value){
        this.innerBoardIndex = innerBoardIndex;
        this.cellIndex = cellIndex;
        this.value = value;
    }

    //Getters

    public Integer GetInnerBoardIndex() {
        return innerBoardIndex;
    }

    public Integer GetCellIndex() {
        return cellIndex;
    }

    public Integer GetValue() {
        return value;
    }

    //Setters

    public void SetInnerBoardIndex(Integer innerBoardIndex) {
        this.innerBoardIndex = innerBoardIndex;
    }

    public void SetCellIndex(Integer cellIndex) {
        this.cellIndex = cellIndex;
    }

    public void SetValue(Integer value) {
        this.value = value;
    }
}
