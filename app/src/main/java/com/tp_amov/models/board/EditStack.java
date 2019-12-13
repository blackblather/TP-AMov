package com.tp_amov.models.board;

import android.widget.EditText;
import androidx.lifecycle.ViewModel;

import java.util.LinkedList;

/**
 * Used as an element in EditStack, in BoardActivity.
 * EditStack is used to synchronize events between the UI and network requests
 */
public class EditStack extends ViewModel {
    /**
     * Encapsulates (EditView) selectedCell and (int) selectedValue.
     */
    public static class Element {
        private final EditText selectedCell;
        private final int selectedValue;

        public Element(EditText selectedCell, int selectedValue){
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

    private LinkedList<Element> list = new LinkedList<>();

    public LinkedList<Element> GetList() {
        return list;
    }
}
