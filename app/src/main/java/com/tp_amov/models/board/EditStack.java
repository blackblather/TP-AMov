package com.tp_amov.models.board;

import androidx.lifecycle.ViewModel;

import java.util.LinkedList;

/**
 * Used
 */
public class EditStack extends ViewModel {
    private LinkedList<EditStackElement> editStack = new LinkedList<>();

    public LinkedList<EditStackElement> GetEditStack() {
        return editStack;
    }
}
