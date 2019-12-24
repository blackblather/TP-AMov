package com.tp_amov.models.board;

import android.os.AsyncTask;
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
        private final int innerBoardResourceId, cellResourceId;
        private final int selectedValue;
        private AsyncInvalidNumberTimer timer = null;  //Used when element is marked as invalid

        //Initializers
        Element(int innerBoardResourceId, int cellResourceId, int selectedValue){
            this.innerBoardResourceId = innerBoardResourceId;
            this.cellResourceId = cellResourceId;
            this.selectedValue = selectedValue;
        }

        void StartTimer() {
            timer = new AsyncInvalidNumberTimer(this);
            timer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        //Getters
        public BoardActivity getBoardActivity() {
            return EditStack.this.getBoardActivity();
        }

        private int getInnerBoardResourceId() {
            return innerBoardResourceId;
        }

        private int getCellResourceId() {
            return cellResourceId;
        }

        public EditText getSelectedCell(){
            EditText selectedCell;
            synchronized (boardActivityLock) {
                ViewGroup selectedInnerBoard = (ViewGroup) boardActivity.findViewById(getInnerBoardResourceId());
                selectedCell = (EditText) selectedInnerBoard.findViewById(getCellResourceId());
            }
            return selectedCell;
        }

        public int getSelectedValue() {
            return selectedValue;
        }

        AsyncInvalidNumberTimer getTimer() {
            return timer;
        }

        //Calls to parent
        public void RemoveIdenticalRunning(){
            EditStack.this.RemoveIdenticalRunning(this);
        }

        //Overrides
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Element){
                Element otherElement = (Element) obj;
                return getInnerBoardResourceId() == otherElement.getInnerBoardResourceId() && getCellResourceId() == otherElement.getCellResourceId();
            }
            return false;
        }
    } //End Element class

    private LinkedList<Element> pendingList = new LinkedList<>();
    private LinkedList<Element> runningList = new LinkedList<>();
    //Control vars
    private final Object boardActivityLock = new Object();
    private BoardActivity boardActivity;

    //List functions
    public void AddElement(int innerBoardResourceId, int cellResourceId, int selectedValue){
        pendingList.addLast(new Element(innerBoardResourceId, cellResourceId, selectedValue));
    }

    /*Removes from pendingList*/
    public Element RemoveValidElement(){
        return pendingList.removeLast();
    }

    /*Removes from pendingList, adds to runningList and starts timer*/
    public Element RemoveInvalidElement(){
        Element element = pendingList.removeLast();
        runningList.addLast(element);
        RemoveIdenticalRunning(element);
        element.StartTimer();
        return element;
    }

    //Removes all elements from runningList identical to param (EXCLUDING the param)
    private void RemoveIdenticalRunning(Element element) {
        for(int i = 0; i < runningList.size(); i++) {
            Element e = runningList.get(i);
            if (e.getTimer() != null && e.equals(element) && e != element) {
                runningList.remove(e);
                e.getTimer().cancel(true);
                i--;
            }
        }
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
