package com.tp_amov;

import android.content.Intent;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.tp_amov.board.Board;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {
    private Board b;
    private EditText selected_cell;
    private Toolbar toolbar;
    private MenuItem highlight_opt;
    private MenuItem dk_mode;
    private int foreground_unselected,foreground_selected;

    LinearLayout NubPadBackground;
    ArrayList<InnerBoardFragment> ib_frags = new ArrayList<>();
    private void SetBoardRunnables() {
        b.setInvalidNrListener(new Runnable() {
            @Override
            public void run() {

            }
        });
        b.setinsertNrListner(new RunnableWithObjList() {
            @Override
            public void run(Integer innerboard_index,Integer cell_index,Integer value) {
                if(b.IsCellEditable(innerboard_index, cell_index))
                {
                    selected_cell.setText(value.toString());
                /*Toast toast = Toast.makeText(context, text, duration);
                toast.show();*/
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_board);
            //Set toolbar info
            toolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            //Get intent
            Intent intent = getIntent();

            //Store intent's extras
            ArrayList<String> usernames = intent.getStringArrayListExtra(SelectUserActivity.EXTRA_USERNAMES);
            ArrayList<String> imgPaths = intent.getStringArrayListExtra(SelectUserActivity.EXTRA_IMG_PATHS);

            //Get fragment manager / fragment transaction objects
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //Gets new fragment instance by sending it the chosen usernames + imgPaths
            Fragment fragment = InGamePlayerInfoFragment.newInstance(usernames, imgPaths);

            //Adds fragment to activity
            fragmentTransaction.add(R.id.Board_activity, fragment);
            fragmentTransaction.commit();

            //Get application context
            Context context = getApplicationContext();

            //Initializes board object
            b = new Board(context, "easy",
                new Consumer<ArrayList<ArrayList<Integer>>>() {
                    @Override
                    public void accept(ArrayList<ArrayList<Integer>> boardArray) {
                        FillViews(boardArray);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            );
            SetBoardRunnables();
            setScreenAdaptation(context);
        } catch (ClassCastException e){
            finish();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(getApplicationContext(), "An error occurred, try again later", duration);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.board_settings_menu, menu);
        highlight_opt = menu.findItem(R.id.board_action_setting_HEC);
        dk_mode = menu.findItem(R.id.board_action_setting_DKM);
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
            case R.id.board_action_setting_DKM:
                if(item.isChecked()) {
                    item.setChecked(false);
                    Toggle_darkmode();
                    return false;
                }
                else{
                    item.setChecked(true);
                    Toggle_darkmode();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View t) {
        if(selected_cell!=null)
            b.insertNum(getInnerBoxIndex(),getCellIndex(),getBtnValue(t));
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

    public void onRestart(){
        super.onRestart();
    }

    private void FillViews(ArrayList<ArrayList<Integer>> boardArray) {
        for (int i = 0; i < boardArray.size(); i++)
            for (int j = 0; j < boardArray.get(i).size(); j++)
                if(!b.IsCellEditable(i,j))
                    ib_frags.get(i).UpdateValue(j, boardArray.get(i).get(j));
    }

    public void Toggle_darkmode() {
        GridLayout gl = findViewById(R.id.Board_activity);
        int default_color = ResourcesCompat.getColor(getResources(),R.color.white,null);
        int dark_color_background = ResourcesCompat.getColor(getResources(), R.color.dark_gray, null);
        Drawable default_color_kbd = getDrawable( R.drawable.keyboard_background);
        Drawable dark_color_background_kbd = getDrawable( R.drawable.keyboard_background_dark);
        if(dk_mode.isChecked()){
            gl.setBackgroundColor(dark_color_background);
            NubPadBackground.setBackground(dark_color_background_kbd);
        }
        else{
            gl.setBackgroundColor(default_color);
            NubPadBackground.setBackground(default_color_kbd);
        }
        ApplyDarkMode_opt();
        ToogleHighlight();
    }

    private void ApplyDarkMode_opt(){
        for (int i = 0; i < 9; i++) {
            ArrayList<View> components = ib_frags.get(i).GetViews();
            for (int j = 0; j < 9; j++)
                if(dk_mode.isChecked())
                    if(b.getValuesFromStartBoard(i).get(j) == 0)
                        if(((EditText)components.get(j)) == selected_cell)
                            ((EditText)components.get(j)).setBackground((Drawable) getDrawable( R.drawable.box_back_dark_interact));
                        else
                            ((EditText)components.get(j)).setBackground((Drawable) getDrawable( R.drawable.box_back_dark));
                    else {
                        ((EditText) components.get(j)).setBackground((Drawable) getDrawable(R.drawable.box_back_dark));
                        ((EditText) components.get(j)).setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                    }
                else{
                    if(b.getValuesFromStartBoard(i).get(j) == 0)
                        if(((EditText)components.get(j)) == selected_cell)
                            ((EditText)components.get(j)).setBackground((Drawable) getDrawable( R.drawable.box_back_interact));
                        else
                            ((EditText)components.get(j)).setBackground((Drawable) getDrawable( R.drawable.box_back));
                    else {
                        ((EditText) components.get(j)).setBackground((Drawable) getDrawable(R.drawable.box_back));
                        ((EditText) components.get(j)).setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
                    }
                }

        }
    }

    private void ToogleHighlight() {
        ToggleForeground();
        ApplyHighlight_opt();
    }

    private void ApplyHighlight_opt(){
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
            if(!dk_mode.isChecked()) {
                foreground_unselected = ResourcesCompat.getColor(getResources(), R.color.select_foreground_blue, null);
                foreground_selected = ResourcesCompat.getColor(getResources(), R.color.white, null);
            }
            else{
                foreground_unselected = ResourcesCompat.getColor(getResources(), R.color.dk_md_yellow, null);
                foreground_selected = ResourcesCompat.getColor(getResources(), R.color.dark_gray, null);
            }
        }
        else{
            if(!dk_mode.isChecked()) {
                foreground_unselected = ResourcesCompat.getColor(getResources(), R.color.black, null);
                foreground_selected = ResourcesCompat.getColor(getResources(), R.color.black, null);
            }
            else{
                foreground_unselected = ResourcesCompat.getColor(getResources(), R.color.white, null);
                foreground_selected = ResourcesCompat.getColor(getResources(), R.color.white, null);
            }
        }
    }

    private Drawable updateColorSelect(){
        Drawable selected;
        if(dk_mode.isChecked()){
            selected = getDrawable( R.drawable.box_back_dark_interact);
        }
        else{
            selected = getDrawable( R.drawable.box_back_interact);
        }
        return selected;
    }

    private Drawable updateColorUnselect(){
        Drawable unselected;
        if(dk_mode.isChecked()){
            unselected = getDrawable(R.drawable.box_back_dark);
        }
        else{
            unselected = getDrawable(R.drawable.box_back);
        }
        return unselected;
    }

    public void setScreenAdaptation(Context context){
        int width = getScreenWidthInDPs(context);
        int height = getScreenHeightInDPs(context);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            setBoardSizeForScreenSize(width,height,true);
        } else {
            // In portrait
            setBoardSizeForScreenSize(width,height,false);
        }
    }

    public void setBoardSizeForScreenSize(int width, int height, boolean isLandScape){
        final float scale = this.getApplicationContext().getResources().getDisplayMetrics().density;
        int cell_dimension;
        if(isLandScape){
            cell_dimension = (int) ((((height-16)/9)* scale)-(2*scale));
        }
        else{
            cell_dimension = (int) ((((width-(16))/9)* scale)-(2*scale));
        }
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                EditText editT = (EditText) ib_frags.get(i).GetViews().get(j);
                ViewGroup.LayoutParams layoutParams = editT.getLayoutParams();
                layoutParams.width = cell_dimension;
                layoutParams.height = cell_dimension;
                editT.setLayoutParams(layoutParams);
                //((EditText) ib_frags.get(i).GetViews().get(j)).setLayoutParams(layoutParams);
            }
    }

    public static int getScreenWidthInDPs(Context context){
        /*
            DisplayMetrics
                A structure describing general information about a display,
                such as its size, density, and font scaling.
        */
        DisplayMetrics dm = new DisplayMetrics();

        /*
            WindowManager
                The interface that apps use to talk to the window manager.
                Use Context.getSystemService(Context.WINDOW_SERVICE) to get one of these.
        */

        /*
            public abstract Object getSystemService (String name)
                Return the handle to a system-level service by name. The class of the returned
                object varies by the requested name. Currently available names are:

                WINDOW_SERVICE ("window")
                    The top-level window manager in which you can place custom windows.
                    The returned object is a WindowManager.
        */

        /*
            public abstract Display getDefaultDisplay ()

                Returns the Display upon which this WindowManager instance will create new windows.

                Returns
                The display that this window manager is managing.
        */

        /*
            public void getMetrics (DisplayMetrics outMetrics)
                Gets display metrics that describe the size and density of this display.
                The size is adjusted based on the current rotation of the display.

                Parameters
                outMetrics A DisplayMetrics object to receive the metrics.
        */
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int widthInDP = Math.round(dm.widthPixels / dm.density);
        return widthInDP;
    }

    // Custom method to get screen height in dp/dip using Context object
    public static int getScreenHeightInDPs(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        /*
            In this example code we converted the float value
            to nearest whole integer number. But, you can get the actual height in dp
            by removing the Math.round method. Then, it will return a float value, you should
            also make the necessary changes.
        */

        /*
            public int heightPixels
                The absolute height of the display in pixels.

            public float density
             The logical density of the display.
        */
        int heightInDP = Math.round(dm.heightPixels / dm.density);
        return heightInDP;
    }

    public void onFocusChange(View t) {
        ToggleForeground();
        Drawable unselected = updateColorUnselect();
        Drawable selected = updateColorSelect();
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
                selected_cell = (EditText) t;
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
