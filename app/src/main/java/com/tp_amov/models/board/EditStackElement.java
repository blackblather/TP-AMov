package com.tp_amov.models.board;

import android.widget.EditText;

/**
 * Encapsulates (EditView) selectedCell and (int) selectedValue.
 * Used as an element in EditStack, in BoardActivity.
 * EditStack is used to synchronize events between the UI and network requests
 */
public class EditStackElement {
    private final EditText selectedCell;
    private final int selectedValue;

    public EditStackElement(EditText selectedCell, int selectedValue){
        this.selectedCell = selectedCell;
        this.selectedValue = selectedValue;
    }

    public EditText getSelectedCell() {
        return selectedCell;
    }

    public int getSelectedValue() {
        return selectedValue;
    }
}
