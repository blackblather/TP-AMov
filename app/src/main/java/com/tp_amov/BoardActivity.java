package com.tp_amov;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.tp_amov.board.Board;
import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {
    Board b = new Board();
    EditText selected_cell;
    ArrayList<EditText> cells = new ArrayList<>();

    public void BuildETHandlersList()
    {
        /*for (int j = 1; j <= 9; j++) {
            int initial = R.id.btn_box_m1;
            int secondary = R.id.btn_box_m2;
            int step = secondary - initial;
            int iterations = 0;
            EditText cell;
            //Find the desired inner_board_index
            for (int i = initial ; i < initial +(80*step) ; i=i+step*3)
            {
                iterations++;
                cell = (EditText)findViewById(i);
                if((iterations % 3)==0)
                {
                    i=i+step*9;
                }
            }
        }*/
    }

    public void rand()
    {
        b.setInvalidNrListener(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        b.start_board();
    }

    public void onResume()
    {
        super.onResume();
        BuildETHandlersList();
    }

    public void onFocusChange(View t)
    {
        EditText input = (EditText)t;
        Drawable unselected =  getDrawable(R.drawable.box_back);
        Drawable selected =  getDrawable( R.drawable.box_back_interact);
        Drawable current =  t.getBackground();
        Drawable.ConstantState constantStateDrawableA = unselected.getConstantState();
        Drawable.ConstantState constantStateDrawableB = current.getConstantState();
        if(constantStateDrawableA.equals(constantStateDrawableB))
        {
            if(selected_cell==null)
            {
                selected_cell = (EditText)t;
            }
            else if(selected_cell != (EditText)t)
            {
                selected_cell.setBackground(unselected);
                selected_cell = (EditText)t;
            }
            t.setBackground(selected);
        }
        else
        {
            if(selected_cell == (EditText)t)
            {
                selected_cell = null;
            }
            t.setBackground(unselected);
        }
    }


}
