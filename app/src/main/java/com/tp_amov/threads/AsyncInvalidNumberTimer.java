package com.tp_amov.threads;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.EditText;
import com.tp_amov.BoardActivity;
import com.tp_amov.models.board.EditStack;

import java.util.concurrent.TimeUnit;

public class AsyncInvalidNumberTimer extends AsyncTask<Void, Void, BoardActivity> {
    private EditStack.Element editStackElement;

    public AsyncInvalidNumberTimer(EditStack.Element editStackElement){
        this.editStackElement = editStackElement;
    }

    protected BoardActivity doInBackground(Void... params) {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return editStackElement.getParentBoardActivity();
    }

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