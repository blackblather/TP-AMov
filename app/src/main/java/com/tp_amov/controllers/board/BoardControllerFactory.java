package com.tp_amov.controllers.board;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.tp_amov.events.board.BoardEvents;

public class BoardControllerFactory implements ViewModelProvider.Factory{
    private Context context;
    private String difficulty;
    private BoardEvents boardEvents;
    public BoardControllerFactory(Context context, String difficulty, BoardEvents boardEvents){
        this.context = context;
        this.difficulty = difficulty;
        this.boardEvents = boardEvents;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(Context.class, String.class, BoardEvents.class).newInstance(context, difficulty, boardEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
