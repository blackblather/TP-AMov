package com.tp_amov.models.board;

import android.view.ViewGroup;
import android.widget.EditText;
import androidx.lifecycle.ViewModel;
import com.tp_amov.BoardActivity;
import com.tp_amov.threads.AsyncInvalidNumberTimer;

import java.util.LinkedList;

/**
 * Used as an element in EditStack, in BoardActivity.
 * EditStack is used to synchronize events between the UI and network requests
 */
public class EditStack extends ViewModel {
    /**
     * Encapsulates (EditView) selectedCell and (int) selectedValue.
     */
    public class Element {
        private final int innerBoardId, cellId;
        private final int selectedValue;
        private AsyncInvalidNumberTimer timer = null;  //Used when element is marked as invalid

        //Initializers
        Element(int innerBoardId, int cellId, int selectedValue){
            this.innerBoardId = innerBoardId;
            this.cellId = cellId;
            this.selectedValue = selectedValue;
        }

        public void CreateTimer() {
            timer = new AsyncInvalidNumberTimer(this);
        }

        //Getters
        public BoardActivity getParentBoardActivity() {
            return getBoardActivity();
        }

        private int getInnerBoardId() {
            return innerBoardId;
        }

        private int getCellId() {
            return cellId;
        }

        public EditText getSelectedCell(){
            EditText selectedCell;
            synchronized (boardActivityLock) {
                ViewGroup selectedInnerBoard = (ViewGroup) boardActivity.findViewById(getInnerBoardId());
                selectedCell = (EditText) selectedInnerBoard.findViewById(getCellId());
            }
            return selectedCell;
        }

        public int getSelectedValue() {
            return selectedValue;
        }

        public AsyncInvalidNumberTimer getTimer() {
            return timer;
        }


    } //End Element class

    private LinkedList<Element> list = new LinkedList<>();
    //Control vars
    private final Object boardActivityLock = new Object();
    private BoardActivity boardActivity;

    //list functions
    public void AddElement(int innerBoardId, int cellId, int selectedValue){
        list.addLast(new Element(innerBoardId, cellId, selectedValue));
    }

    public Element RemoveElement(){
        return list.removeLast();
    }
    public Element Peek(){
        return list.peekLast();
    }

    //Getters
    private BoardActivity getBoardActivity() {
        synchronized (boardActivityLock){
            return boardActivity;
        }
    }

    //Setters
    public void setBoardActivity(BoardActivity boardActivity) {
        synchronized (boardActivityLock){
            this.boardActivity = boardActivity;
        }
    }
}
