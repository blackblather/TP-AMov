package com.tp_amov.threads;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.EditText;
import com.tp_amov.BoardActivity;
import com.tp_amov.models.board.EditStack;

public class AsyncInvalidNumberTimer extends AsyncTask<Void, Void, BoardActivity> {
    private EditStack.Element editStackElement;

    public AsyncInvalidNumberTimer(EditStack.Element editStackElement){
        this.editStackElement = editStackElement;
    }

    protected BoardActivity doInBackground(Void... params) {
        try {
            double seconds = 2;
            double checkRate = 0.5;

            seconds = seconds * 1/0.5;

            while((seconds--) > 0) {
                Thread.sleep((long)(checkRate*1000));
            }

        } catch (InterruptedException e) {
            //Task was canceled
        }
        return editStackElement.getBoardActivity();
    }

    //Only called if AsyncTask was NOT canceled
    protected void onPostExecute(BoardActivity boardActivity){

        Drawable color;

        EditText currentlySelectedCell = boardActivity.getSelectedCell();
        EditText selectedCell = editStackElement.getSelectedCell();

        if(currentlySelectedCell != null && currentlySelectedCell.equals(selectedCell))
            color = boardActivity.getColorSelect();
        else
            color = boardActivity.getColorUnselect();

        String oldValueSTR = selectedCell.getText().toString();
        int currentValue = editStackElement.getSelectedValue();
        if(!oldValueSTR.equals("") && Integer.parseInt(oldValueSTR) == currentValue) {
            selectedCell.setText("");
            selectedCell.setBackground(color);
        }
    }
}