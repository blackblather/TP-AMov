package com.tp_amov.board;

import androidx.core.util.Consumer;
import com.tp_amov.RunnableWithObjList;

import java.util.ArrayList;

public class BoardEvents {
    RunnableWithObjList onInsertValidNumber;
    Runnable onInsertInvalidNumber, onBoardCreationError, onGameFinished;
    Consumer<ArrayList<ArrayList<Integer>>> onBoardCreationSuccess;

    public BoardEvents(){ }

    public void setOnInsertValidNumber(RunnableWithObjList onInsertValidNumber) {
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
    public void setOnGameFinished(Runnable onGameFinished) {
        this.onGameFinished = onGameFinished;
    }
}