package com.tp_amov.board;

import androidx.core.util.Consumer;

import java.util.ArrayList;

public class BoardEvents {
    Runnable onInsertValidNumber, onInsertInvalidNumber, onBoardCreationError, onBoardSolved, onBoardUnsolved;
    Consumer<ArrayList<ArrayList<Integer>>> onBoardCreationSuccess;

    public BoardEvents(){ }

    public void setOnInsertValidNumber(Runnable onInsertValidNumber) {
        this.onInsertValidNumber = onInsertValidNumber;
    }
    public void setOnInsertInvalidNumber(Runnable onInsertInvalidNumber) {
        this.onInsertInvalidNumber = onInsertInvalidNumber;
    }
    public void setOnBoardCreationError(Runnable onBoardCreationError) {
        this.onBoardCreationError = onBoardCreationError;
    }
    public void setOnBoardCreationSuccess(Consumer<ArrayList<ArrayList<Integer>>> onBoardCreationSuccess) {
        this.onBoardCreationSuccess = onBoardCreationSuccess;
    }
    public void setOnBoardSolved(Runnable onBoardSolved) {
        this.onBoardSolved = onBoardSolved;
    }
    public void setOnBoardUnsolved(Runnable onBoardUnsolved) {
        this.onBoardUnsolved = onBoardUnsolved;
    }
}