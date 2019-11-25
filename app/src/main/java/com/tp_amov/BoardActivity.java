package com.tp_amov;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.tp_amov.board.Board;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {
    Board b = new Board();
    EditText selected_cell;
    Toolbar toolbar;
    MenuItem highlight_opt;
    /*MenuItem dk_mode;*/

    ArrayList<InnerBoardFragment> ib_frags = new ArrayList<>();
    public void InitRuns()
    {
        b.setInvalidNrListener(new Runnable() {
            @Override
            public void run() {

            }
        });
        b.setinsertNrListner(new RunnableWithObjList() {
            @Override
            public void run(ArrayList<Object> objs) {
                if(b.IsCellEditable((Integer)objs.get(0), (Integer)objs.get(1)))
                {
                    selected_cell.setText((objs.get(2)).toString());
                /*Toast toast = Toast.makeText(context, text, duration);
                toast.show();*/
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        InitRuns();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.board_settings_menu, menu);
        highlight_opt = menu.findItem(R.id.board_action_setting_HEC);
        /*dk_mode = menu.findItem(R.id.board_action_setting_DKM);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.board_action_setting_HEC:
                if(item.isChecked()) {
                    item.setChecked(false);
                    ToogleHighlight();
                    return false;
                }
                else{
                    item.setChecked(true);
                    ToogleHighlight();
                    return true;
                }
            /*case R.id.board_action_setting_DKM:
                if(item.isChecked()) {
                    item.setChecked(false);
                    Toggle_darkmode();
                    return false;
                }
                else{
                    item.setChecked(true);
                    Toggle_darkmode();
                    return true;
                }*/
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View t)
    {
        if(selected_cell!=null) {
            ArrayList<Object> data = new ArrayList<>();
            data.add(getInnerBoxIndex()); //f_index
            data.add(getCellIndex()); //cell_i
            data.add(getBtnValue(t)); //value
            b.insertNum(data);
        }
        else
        {
            Toast.makeText(this, "Please select a cell!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public int getBtnValue(View t){
        Button btn = (Button) t;
        return Integer.parseInt(btn.getText().toString());
    }

    public int getCellIndex(){
        String cell_index = null;
        try {
            cell_index = getIDString(selected_cell,R.id.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] id = cell_index.split("btn_box_m");
        int cell_i = (Integer.parseInt(id[1]))-1;
        return cell_i;
    }

    public int getInnerBoxIndex(){
        int f_index=0;
        for (InnerBoardFragment frag : ib_frags) {
            if(frag.ElementExists(selected_cell))
                break;
            else
                f_index++;
        }
        return f_index;
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
        FillViews();
    }


    public void FillViews()
    {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(!b.IsCellEditable(i,j))
                    ib_frags.get(i).UpdateValue(j, b.getValuesFromBoard(i).get(j));
            }
        }
    }

    public void Toggle_darkmode()
    {

    }

    public void ToogleHighlight()
    {
        int foreground_unselected;
        int foreground_selected;
        if(highlight_opt.isChecked()){
            foreground_unselected = ResourcesCompat.getColor(getResources(), R.color.select_foreground_blue, null);
            foreground_selected = ResourcesCompat.getColor(getResources(), R.color.white, null);
        }
        else{
            foreground_unselected = ResourcesCompat.getColor(getResources(), R.color.black, null);
            foreground_selected = ResourcesCompat.getColor(getResources(), R.color.black, null);
        }
        ApplyHighlight_opt(foreground_unselected,foreground_selected);
    }

    public void ApplyHighlight_opt(int foreground_unselected, int foreground_selected){
        for (int i = 0; i < 9; i++) {
            ArrayList<View> components = ib_frags.get(i).GetViews();
            for (int j = 0; j < 9; j++) {
                if(b.getValuesFromStartBoard(i).get(j) == 0){
                    if(((EditText)components.get(j)) == selected_cell){
                        ((EditText)components.get(j)).setTextColor(foreground_selected);
                    }
                    else{
                        ((EditText)components.get(j)).setTextColor(foreground_unselected);
                    }
                }
            }
        }

    }

    public void onFocusChange(View t)
    {
        int foreground_unselected;
        int foreground_selected;
        if(highlight_opt.isChecked()){
            foreground_unselected = ResourcesCompat.getColor(getResources(), R.color.select_foreground_blue, null);
            foreground_selected = ResourcesCompat.getColor(getResources(), R.color.white, null);
        }
        else{
            foreground_unselected = ResourcesCompat.getColor(getResources(), R.color.black, null);
            foreground_selected = ResourcesCompat.getColor(getResources(), R.color.black, null);
        }
        Drawable unselected = getDrawable(R.drawable.box_back);
        Drawable selected = getDrawable( R.drawable.box_back_interact);
        Drawable current = t.getBackground();
        Drawable.ConstantState constantStateDrawableA = unselected.getConstantState();
        Drawable.ConstantState constantStateDrawableB = current.getConstantState();
        if(constantStateDrawableA.equals(constantStateDrawableB))
        {
            if(selected_cell==null)
            {
                selected_cell = (EditText)t;
                selected_cell.setTextColor(foreground_selected);
            }
            else if(selected_cell != (EditText)t)
            {

                selected_cell.setBackground(unselected);
                selected_cell.setTextColor(foreground_unselected);
                selected_cell = (EditText)t;
            }
            selected_cell.setTextColor(foreground_selected);
            t.setBackground(selected);
        }
        else
        {
            if(selected_cell == (EditText)t)
            {
                selected_cell.setTextColor(foreground_unselected);
                selected_cell = null;
            }
            t.setBackground(unselected);
        }
    }
}
