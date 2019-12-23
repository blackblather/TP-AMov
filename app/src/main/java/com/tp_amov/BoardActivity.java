package com.tp_amov;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Consumer;
import androidx.lifecycle.ViewModelProviders;
import com.tp_amov.controllers.board.BoardController;
import com.tp_amov.events.board.BoardEvents;
import com.tp_amov.models.board.BoardPosition;
import com.tp_amov.models.board.EditStack;
import com.tp_amov.models.board.Element;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {

    private Bundle savedInstance;
    private BoardController boardController;
    private EditText selectedCell;
    private Toolbar toolbar;
    private MenuItem highlightOpt;
    private MenuItem dkMode;
    private int foregroundUnselected, foregroundSelected;
    private boolean useWebservice = false;

    private EditStack editStack;

    private BoardEvents boardEvents;

    GridLayout NubPadBackground;
    InGamePlayerInfoFragment inGamePlayerInfoFragment;
    ArrayList<InnerBoardFragment> ibFrags = new ArrayList<>();
    private ArrayList<String> usernames;
    private ArrayList<String> imgPaths;

    private void SetBoardRunnables() {
        boardEvents = new BoardEvents();
        boardEvents.setOnInsertValidNumber(new Runnable() {
            @Override
            public void run() {
                EditStack.Element editStackElement = editStack.RemoveValidElement();
                Drawable color;

                if(selectedCell == editStackElement.getSelectedCell())
                    color = getColorSelect();
                else
                    color = getColorUnselect();

                editStackElement.getSelectedCell().setBackground(color);
                editStackElement.getSelectedCell().setText(Integer.toString(editStackElement.getSelectedValue()));
            }
        });
        boardEvents.setOnInsertInvalidNumber(new Runnable() {
            @Override
            public void run() {
                EditStack.Element editStackElement = editStack.RemoveInvalidElement();
                editStackElement.getSelectedCell().setText(Integer.toString(editStackElement.getSelectedValue()));
                editStackElement.getSelectedCell().setBackground(getColorInvalid());
            }
        });
        boardEvents.setOnBoardCreationError(new Runnable() {
            @Override
            public void run() {
                //TODO
            }
        });
        boardEvents.setOnBoardCreationSuccess(new Consumer<ArrayList<ArrayList<Integer>>>() {
            @Override
            public void accept(ArrayList<ArrayList<Integer>> arrayLists) {
                FillViews(arrayLists);
            }
        });
        boardEvents.setOnBoardSolved(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), "OH TU TAMÉM", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        boardEvents.setOnBoardUnsolved(new Runnable(){
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), "Solução inválida", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        boardEvents.setOnReceivedHint(new Consumer<BoardPosition>() {
            @Override
            public void accept(BoardPosition boardPosition) {
                int innerBoardIndex = boardPosition.GetInnerBoardIndex();
                int cellIndex = boardPosition.GetCellIndex();
                int value = boardPosition.GetValue();
                ibFrags.get(innerBoardIndex).UpdateValue(cellIndex, value,R.color.nice_green, true);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_board);

            savedInstance = savedInstanceState;

            //Set toolbar info
            toolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            //Get intent
            Intent intent = getIntent();
            usernames = intent.getStringArrayListExtra(SelectUserActivity.EXTRA_USERNAMES);
            imgPaths = intent.getStringArrayListExtra(SelectUserActivity.EXTRA_IMG_PATHS);
            useWebservice = intent.getBooleanExtra(SelectUserActivity.EXTRA_USE_WEBSERVICE, false);
            Toast toast = Toast.makeText(getApplicationContext(), "VALUE = " + (useWebservice?"TRUE":"FALSE"), Toast.LENGTH_SHORT);
            toast.show();

            //Populates fragment
            inGamePlayerInfoFragment.onSetDataForInGamePlayerInfo(usernames,imgPaths);

            //Set Adaptation for screen
            setScreenAdaptation(getApplicationContext());
        } catch (ClassCastException e) {
            finish();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(getApplicationContext(), "An error occurred, try again later", duration);
            toast.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Set boardController using ViewModelProviders
        SetBoardRunnables();
        BoardController.Factory boardControllerFactory = new BoardController.Factory(getApplicationContext(), "easy", boardEvents, useWebservice);
        boardController = ViewModelProviders.of(this, boardControllerFactory).get(BoardController.class);
        boardController.InitializeBoard();

        //Set editStack using ViewModelProviders
        editStack = ViewModelProviders.of(this).get(EditStack.class);
        editStack.setBoardActivity(this);
        setScreenAdaptation(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.board_settings_menu, menu);
        highlightOpt = menu.findItem(R.id.board_action_setting_HEC);
        dkMode = menu.findItem(R.id.board_action_setting_DKM);
        //Isto está aqui porque os menus são criados depois do onRestoreInstanceState
        if(savedInstance != null) {
            highlightOpt.setChecked((boolean) savedInstance.getBoolean("Highlight", true));
            dkMode.setChecked((boolean) savedInstance.getBoolean("Dark_mode", false));
            Toggle_darkmode();
        }
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
                    ToggleHighlight();
                    return false;
                }
                else{
                    item.setChecked(true);
                    ToggleHighlight();
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
        if(selectedCell !=null) {
            int selectedInnerBoardResourceId = ((ViewGroup)selectedCell.getParent()).getId();
            int selectedCellResourceId = selectedCell.getId();
            editStack.AddElement(selectedInnerBoardResourceId, selectedCellResourceId, getBtnValue(t));
            boardController.InsertNumber(new BoardPosition(getInnerBoxIndex(),getCellIndex(),getBtnValue(t)));
        } else {
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
            cell_index = getIDString(selectedCell,R.id.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] id = cell_index.split("btn_box_m");
        int cell_i = (Integer.parseInt(id[1]))-1;
        return cell_i;
    }

    private int getInnerBoxIndex(){
        int f_index=0;
        for (InnerBoardFragment frag : ibFrags)
            if(frag.ElementExists(selectedCell))
                break;
            else
                f_index++;
        return f_index;
    }

    public static String getIDString(View view, Class<?> clazz) throws Exception {
        int id = view.getId();
        Field[] ids = clazz.getFields();
        for (Field field : ids) {
            Object val = field.get(null);
            if (val instanceof Integer && (Integer) val == (int) id)
                return field.getName();
        }
        return "";
    }

    public void onRestart(){
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //DATA TO SAVE
        outState.putBoolean("Highlight", highlightOpt.isChecked());
        outState.putBoolean("Dark_mode", dkMode.isChecked());
//        getSupportFragmentManager().putFragment(outState, "boardFragment", boardFragment);
//
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.detach(boardFragment);
//        fragmentTransaction.commit();

        //SAVE
        super.onSaveInstanceState(outState);

    }

    private void FillViews(ArrayList<ArrayList<Integer>> boardArray) {

        for (int i = 0; i < boardArray.size(); i++)
            for (int j = 0; j < boardArray.get(i).size(); j++)
                if(!boardController.ElementContainsType(i,j, Element.Type.userValue, Element.Type.hintValue))
                    ibFrags.get(i).UpdateValue(j, boardArray.get(i).get(j),(dkMode.isChecked()?R.color.white:R.color.black), false);
    }

    private void Toggle_darkmode() {
        ConstraintLayout playerNameplate = (ConstraintLayout) inGamePlayerInfoFragment.getView();
        TextView nameplateText = (TextView) inGamePlayerInfoFragment.getView().findViewById(R.id.in_game_current_user_txt);
        androidx.gridlayout.widget.GridLayout gl = findViewById(R.id.Board_activity);
        int default_color = ResourcesCompat.getColor(getResources(),R.color.white,null);
        int dark_color_background = ResourcesCompat.getColor(getResources(), R.color.dark_gray, null);
        Drawable default_color_kbd = getDrawable( R.drawable.keyboard_background);
        Drawable dark_color_background_kbd = getDrawable( R.drawable.keyboard_background_dark);
        if(dkMode.isChecked()){
            gl.setBackgroundColor(dark_color_background);
            NubPadBackground.setBackground(dark_color_background_kbd);
            playerNameplate.setBackground(dark_color_background_kbd);
            nameplateText.setTextColor(default_color);
        }
        else{
            gl.setBackgroundColor(default_color);
            NubPadBackground.setBackground(default_color_kbd);
            playerNameplate.setBackground(default_color_kbd);
            nameplateText.setTextColor(dark_color_background);
        }
        ApplyDarkMode_opt();
        ToggleHighlight();
    }

    private void ApplyDarkMode_opt(){
        for (int i = 0; i < 9; i++) {
            ArrayList<View> components = ibFrags.get(i).GetViews();
            for (int j = 0; j < 9; j++)
                if(dkMode.isChecked())
                    if(boardController.GetValuesFromStartBoard(i).get(j) == 0)
                        if(((EditText)components.get(j)) == selectedCell)
                            ((EditText)components.get(j)).setBackground((Drawable) getDrawable( R.drawable.box_back_dark_interact));
                        else
                            ((EditText)components.get(j)).setBackground((Drawable) getDrawable( R.drawable.box_back_dark));
                    else {
                        ((EditText) components.get(j)).setBackground((Drawable) getDrawable(R.drawable.box_back_dark));
                        ((EditText) components.get(j)).setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                    }
                else{
                    if(boardController.GetValuesFromStartBoard(i).get(j) == 0)
                        if(((EditText)components.get(j)) == selectedCell)
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

    private void ToggleHighlight() {
        ToggleForeground();
        ApplyHighlight_opt();
    }

    private void ApplyHighlight_opt(){
        for (int i = 0; i < 9; i++) {
            ArrayList<View> components = ibFrags.get(i).GetViews();
            for (int j = 0; j < 9; j++)
                if(boardController.GetValuesFromStartBoard(i).get(j) == 0)
                    if(((EditText)components.get(j)) == selectedCell)
                        ((EditText)components.get(j)).setTextColor(foregroundSelected);
                    else
                        ((EditText)components.get(j)).setTextColor(foregroundUnselected);
        }
    }

    private void ToggleForeground(){
        if(highlightOpt.isChecked()){
            if(!dkMode.isChecked()) {
                foregroundUnselected = ResourcesCompat.getColor(getResources(), R.color.select_foreground_blue, null);
                foregroundSelected = ResourcesCompat.getColor(getResources(), R.color.white, null);
            }
            else{
                foregroundUnselected = ResourcesCompat.getColor(getResources(), R.color.dk_md_yellow, null);
                foregroundSelected = ResourcesCompat.getColor(getResources(), R.color.dark_gray, null);
            }
        }
        else{
            if(!dkMode.isChecked()) {
                foregroundUnselected = ResourcesCompat.getColor(getResources(), R.color.black, null);
                foregroundSelected = ResourcesCompat.getColor(getResources(), R.color.black, null);
            }
            else{
                foregroundUnselected = ResourcesCompat.getColor(getResources(), R.color.white, null);
                foregroundSelected = ResourcesCompat.getColor(getResources(), R.color.white, null);
            }
        }
    }

    public Drawable getColorSelect(){
        Drawable selected;
        if(dkMode.isChecked()){
            selected = getDrawable( R.drawable.box_back_dark_interact);
        }
        else{
            selected = getDrawable( R.drawable.box_back_interact);
        }
        return selected;
    }

    public Drawable getColorUnselect(){
        Drawable unselected;
        if(dkMode.isChecked()){
            unselected = getDrawable(R.drawable.box_back_dark);
        }
        else{
            unselected = getDrawable(R.drawable.box_back);
        }
        return unselected;
    }

    private Drawable getColorInvalid(){
        Drawable invalidNumber;
        invalidNumber = getDrawable(R.drawable.box_back_invalid_number);
        return invalidNumber;
    }

    private void setScreenAdaptation(Context context){
        int width = getScreenWidthInDPs(context);
        int height = getScreenHeightInDPs(context);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            setBoardSizeForScreenSize(width,height,true, context);
            setInGamePlayerInfoForScreenSize( true, context);
            setBoardKeyboardForScreenSize(width, height, true, context);
        } else {
            // In portrait
            setBoardSizeForScreenSize(width,height,false, context);
            setBoardKeyboardForScreenSize(width, height, false, context);
        }
    }

    private int setBoardSizeForScreenSize(int width, int height, boolean isLandScape, Context context){
        final float scale = this.getApplicationContext().getResources().getDisplayMetrics().density;
        int cell_dimension;
        if(isLandScape){
            ViewGroup.LayoutParams tb_size = toolbar.getLayoutParams();
            cell_dimension = (int) (((((height-convertPixelsToDp(tb_size.height,context))-16)/9)* scale)-(5*scale));
        }
        else{
            cell_dimension = (int) ((((width-(16))/9)* scale)-(2*scale));
        }
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                EditText editT = (EditText) ibFrags.get(i).GetViews().get(j);
                ViewGroup.LayoutParams layoutParams = editT.getLayoutParams();
                layoutParams.width = cell_dimension;
                layoutParams.height = cell_dimension;
                editT.setLayoutParams(layoutParams);
            }
        return cell_dimension;
    }

    private int setBoardKeyboardForScreenSize(int width, int height, boolean isLandScape, Context context){
        final float scale = this.getApplicationContext().getResources().getDisplayMetrics().density;
        int btn_dimension;
        if(isLandScape){
            ViewGroup.LayoutParams tb_size = toolbar.getLayoutParams();
            btn_dimension = (int) (((((height-convertPixelsToDp(tb_size.height,context)-convertPixelsToDp((tb_size.height),context))-38)/4)* scale)-(5*scale));
        }
        else{
            btn_dimension = (int) ((((width-38)/6)* scale)-(2*scale));
        }
        //Icon buttons
        setButtonToSize(btn_dimension,(Button) findViewById(R.id.btn_backspace),true);
        setButtonToSize(btn_dimension,(Button) findViewById(R.id.btn_submit),true);
        setButtonToSize(btn_dimension,(Button) findViewById(R.id.btn_hint),true);
        //Number buttons
        setButtonToSize(btn_dimension,(Button) findViewById(R.id.num_1),false);
        setButtonToSize(btn_dimension,(Button) findViewById(R.id.num_2),false);
        setButtonToSize(btn_dimension,(Button) findViewById(R.id.num_3),false);
        setButtonToSize(btn_dimension,(Button) findViewById(R.id.num_4),false);
        setButtonToSize(btn_dimension,(Button) findViewById(R.id.num_5),false);
        setButtonToSize(btn_dimension,(Button) findViewById(R.id.num_6),false);
        setButtonToSize(btn_dimension,(Button) findViewById(R.id.num_7),false);
        setButtonToSize(btn_dimension,(Button) findViewById(R.id.num_8),false);
        setButtonToSize(btn_dimension,(Button) findViewById(R.id.num_9),false);
        return btn_dimension;
    }

    private void setButtonToSize(int btn_dimensions, Button btn, boolean hasIcon){
        //Padding Start for icons must be 2/7 of button size for icon to be centered
        ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
        layoutParams.width = btn_dimensions;
        layoutParams.height = btn_dimensions;
        btn.setLayoutParams(layoutParams);
        if(hasIcon){
            int paddingL = (btn_dimensions/7)*2;
            btn.setPadding(paddingL,0,0,0);
        }
    }

    private void setInGamePlayerInfoForScreenSize(boolean isLandScape, Context context){
        final float scale = this.getApplicationContext().getResources().getDisplayMetrics().density;
        int image_dimension;
        ViewGroup.LayoutParams tb_size = toolbar.getLayoutParams();
        View pImage = findViewById(R.id.profile_image);
        if(isLandScape){
            image_dimension = tb_size.height;
            ViewGroup.LayoutParams layoutParams = pImage.getLayoutParams();
            layoutParams.width = image_dimension;
            layoutParams.height = image_dimension;
            pImage.setLayoutParams(layoutParams);
        }

    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private static int getScreenWidthInDPs(Context context){
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
        return Math.round(dm.widthPixels / dm.density);
    }

    // Custom method to get screen height in dp/dip using Context object
    private static int getScreenHeightInDPs(Context context){
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
        Drawable unselected = getColorUnselect();
        Drawable selected = getColorSelect();
        Drawable current = t.getBackground();
        Drawable.ConstantState constantStateDrawableA = unselected.getConstantState();
        Drawable.ConstantState constantStateDrawableB = current.getConstantState();
        if(constantStateDrawableA.equals(constantStateDrawableB)) {
            if(selectedCell == null) {
                selectedCell = (EditText)t;
                selectedCell.setTextColor(foregroundSelected);
            }
            else if(selectedCell != t) {
                selectedCell.setBackground(unselected);
                selectedCell.setTextColor(foregroundUnselected);
                selectedCell = (EditText) t;
            }
            selectedCell.setTextColor(foregroundSelected);
            t.setBackground(selected);
        }
        else {
            if(selectedCell == t) {
                selectedCell.setTextColor(foregroundUnselected);
                selectedCell = null;
            }
            t.setBackground(unselected);
        }
    }

    public void onSubmitBoard(View t) {
        boardController.ValidateSolution();
    }

    public void onBackspace(View t) {
        Toast toast = Toast.makeText(getApplicationContext(), "Backspace not Implemented", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onHintRequest(View t) {
        boardController.RequestHint();
    }

    public EditText getSelectedCell() {
        return selectedCell;
    }
}
