package com.tp_amov;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Consumer;
import com.tp_amov.board.Board;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {
    private Board b;
    private EditText selected_cell;
    private Toolbar toolbar;
    private MenuItem highlight_opt;
    private int foreground_unselected,foreground_selected;
    /*MenuItem dk_mode;*/

    ArrayList<InnerBoardFragment> ib_frags = new ArrayList<>();
    private void InitRuns() {
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
        b = new Board(getApplicationContext(), "easy",
                new Consumer<ArrayList<ArrayList<Integer>>>() {
                    @Override
                    public void accept(ArrayList<ArrayList<Integer>> boardArray) {
                        FillViews(boardArray);
                    }
                }, new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
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
        switch (id) {
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

    public void onClick(View t) {
        if(selected_cell!=null) {
            ArrayList<Object> data = new ArrayList<>();
            data.add(getInnerBoxIndex()); //f_index
            data.add(getCellIndex()); //cell_i
            data.add(getBtnValue(t)); //value
            b.insertNum(data);
        }
        else {
            Toast.makeText(this, "Please select a cell!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private int getBtnValue(View t){
        Button btn = (Button) t;
        return Integer.parseInt(btn.getText().toString());
    }

    private int getCellIndex(){
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

    private int getInnerBoxIndex(){
        int f_index=0;
        for (InnerBoardFragment frag : ib_frags)
            if(frag.ElementExists(selected_cell))
                break;
            else
                f_index++;
        return f_index;
    }

    static String getIDString(View view, Class<?> clazz) throws Exception {
        int id = view.getId();
        Field[] ids = clazz.getFields();
        for (Field field : ids) {
            Object val = field.get(null);
            if (val instanceof Integer && (Integer) val == (int) id)
                return field.getName();
        }
        return "";
    }

    public void onResume() {
        //After everything is rendered
        super.onResume();
        //FillViews();
    }


    private void FillViews(ArrayList<ArrayList<Integer>> boardArray) {
        for (int i = 0; i < boardArray.size(); i++)
            for (int j = 0; j < boardArray.get(i).size(); j++)
                if(!b.IsCellEditable(i,j))
                    ib_frags.get(i).UpdateValue(j, boardArray.get(i).get(j));
    }

    public void Toggle_darkmode() {

    }

    private void ToogleHighlight() {
        ToggleForeground();
        ApplyHighlight_opt(foreground_unselected,foreground_selected);
    }

    private void ApplyHighlight_opt(int foreground_unselected, int foreground_selected){
        for (int i = 0; i < 9; i++) {
            ArrayList<View> components = ib_frags.get(i).GetViews();
            for (int j = 0; j < 9; j++)
                if(b.getValuesFromStartBoard(i).get(j) == 0)
                    if(((EditText)components.get(j)) == selected_cell)
                        ((EditText)components.get(j)).setTextColor(foreground_selected);
                    else
                        ((EditText)components.get(j)).setTextColor(foreground_unselected);
        }
    }

    private void ToggleForeground(){
        if(highlight_opt.isChecked()){
            foreground_unselected = ResourcesCompat.getColor(getResources(), R.color.select_foreground_blue, null);
            foreground_selected = ResourcesCompat.getColor(getResources(), R.color.white, null);
        }
        else{
            foreground_unselected = ResourcesCompat.getColor(getResources(), R.color.black, null);
            foreground_selected = ResourcesCompat.getColor(getResources(), R.color.black, null);
        }
    }

    public void onFocusChange(View t) {
        ToggleForeground();
        Drawable unselected = getDrawable(R.drawable.box_back);
        Drawable selected = getDrawable( R.drawable.box_back_interact);
        Drawable current = t.getBackground();
        Drawable.ConstantState constantStateDrawableA = unselected.getConstantState();
        Drawable.ConstantState constantStateDrawableB = current.getConstantState();
        if(constantStateDrawableA.equals(constantStateDrawableB)) {
            if(selected_cell==null) {
                selected_cell = (EditText)t;
                selected_cell.setTextColor(foreground_selected);
            }
            else if(selected_cell != (EditText)t) {
                selected_cell.setBackground(unselected);
                selected_cell.setTextColor(foreground_unselected);
                selected_cell = (EditText)t;
            }
            selected_cell.setTextColor(foreground_selected);
            t.setBackground(selected);
        }
        else {
            if(selected_cell == (EditText)t) {
                selected_cell.setTextColor(foreground_unselected);
                selected_cell = null;
            }
            t.setBackground(unselected);
        }
    }
}
