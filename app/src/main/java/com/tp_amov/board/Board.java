package com.tp_amov.board;

import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import com.tp_amov.R;
import java.util.ArrayList;

public class Board
{
    ArrayList<InnerBoard> InnerBoards = new ArrayList<>();

    public Board()
    {
        for (int i = 0; i < 9; i++) {
            InnerBoards.add(new InnerBoard());
        }
    }
    private Runnable InvalidNrListener;
    public void start_board()
    {

    }

    public boolean insert(int inner_board_index, int cell_index, int value)
    {
        InnerBoards.get(inner_board_index).insert(cell_index,value);
        return true;
    }

    public void setInvalidNrListener (Runnable invalidNrListener)
    {
        this.InvalidNrListener = invalidNrListener;
    }

    public boolean isInvalidNr(int Nr)
    {
        /*if(...)
        {

        }
        else
        {*/
            InvalidNrListener.run();
            return false;
        /*}*/
    }

    private class InnerBoard
    {
        ArrayList<Integer> values = new ArrayList<>();
        InnerBoard()
        {
            for (int i = 0; i < 9; i++) {
                values.add(0);
            }

        }

        public boolean insert(int cell_index, int value)
        {
            values.set(cell_index,value);
            return true;
        }
    }
}
