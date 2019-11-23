package com.tp_amov;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tp_amov.board.Board;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {
    Board b = new Board();
    EditText selected_cell;

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

    public  void onClick(View t)
    {
        if(selected_cell!=null) {
            Button b = (Button) t;

            ViewParent parent = t.getParent();
            String inner_board_index = null;
            String cell_index = null;
            try {
                cell_index = getIDString(selected_cell,R.id.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Number " + b.getText().toString() + " pressed! ID:" + cell_index,
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Please select a cell!",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public static String getIDString(View view, Class<?> clazz) throws Exception {

        Integer id = view.getId();
        Field[] ids = clazz.getFields();
        for (int i = 0; i < ids.length; i++) {
            Object val = ids[i].get(null);
            if (val != null && val instanceof Integer
                    && ((Integer) val).intValue() == id.intValue()) {
                return ids[i].getName();
            }
        }

        return "";

    }

    public void onResume()
    {
        //After everything is rendered
        super.onResume();
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
