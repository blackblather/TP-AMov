package com.tp_amov;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    public void onFocusChange(View t)
    {
        /*EditText input = (EditText)t;*/
        /*v.setBackgroundResource(R.drawable.yellow_button);*/
        Drawable unselected =  getDrawable(R.drawable.box_back);
        Drawable selected =  getDrawable( R.drawable.box_back_interact );
        Drawable current =  t.getBackground();

        Drawable.ConstantState constantStateDrawableA = unselected.getConstantState();
        Drawable.ConstantState constantStateDrawableB = current.getConstantState();
        if(constantStateDrawableA.equals(constantStateDrawableB))
        {
            t.setBackground(selected);
        }
        else
        {
            t.setBackground(unselected);
        }
    }
}
