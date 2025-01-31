package com.tp_amov.events.game;

import androidx.core.util.Consumer;
import com.tp_amov.models.board.BoardPosition;

import java.util.ArrayList;

public class GameEvents {
    private Runnable onInsertValidNumber, onInsertInvalidNumber, onBoardCreationError, onBoardSolved, onBoardUnsolved, onHintLimitReached;
    private Consumer<ArrayList<ArrayList<Integer>>> onBoardCreationSuccess;
    private Consumer<BoardPosition> onReceivedHint;
    public GameEvents(){ }

//------------> Getters


    public Runnable getOnInsertValidNumber() {
        return onInsertValidNumber;
    }

    public Runnable getOnInsertInvalidNumber() {
        return onInsertInvalidNumber;
    }

    public Consumer<ArrayList<ArrayList<Integer>>> getOnBoardCreationSuccess() {
        return onBoardCreationSuccess;
    }

    public Consumer<BoardPosition> getOnReceivedHint() {
        return onReceivedHint;
    }

    public Runnable getOnBoardCreationError() {
        return onBoardCreationError;
    }

    public Runnable getOnBoardSolved() {
        return onBoardSolved;
    }

    public Runnable getOnBoardUnsolved() {
        return onBoardUnsolved;
    }

    public Runnable getOnHintLimitReached() {
        return onHintLimitReached;
    }

    //------------> Setters


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

    public void setOnReceivedHint(Consumer<BoardPosition> onReceivedHint) {
        this.onReceivedHint = onReceivedHint;
    }

    public void setOnHintLimitReached(Runnable onHintLimitReached) {
        this.onHintLimitReached = onHintLimitReached;
    }
}