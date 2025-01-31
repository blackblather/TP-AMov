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
import com.tp_amov.controllers.game.GameController;
import com.tp_amov.events.game.GameEvents;
import com.tp_amov.models.board.BoardPosition;
import com.tp_amov.models.board.EditStack;
import com.tp_amov.models.board.Element;
import com.tp_amov.models.sql.User;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {

    private Bundle savedInstance;
    private GameController gameController;
    private EditText selectedCell;
    private Toolbar toolbar;
    private MenuItem highlightOpt;
    private MenuItem dkMode;
    private int foregroundUnselected, foregroundSelected ,hintUnselected;
    private boolean useWebservice = false;

    private EditStack editStack;

    private GameEvents gameEvents;

    GridLayout NubPadBackground;
    InGamePlayerInfoFragment inGamePlayerInfoFragment;
    ArrayList<InnerBoardFragment> ibFrags = new ArrayList<>();
    //Extracted from intent
    private ArrayList<User> users;
    private ArrayList<String> encodedImages;
    private String gameMode;

    private void SetBoardRunnables() {
        gameEvents = new GameEvents();
        gameEvents.setOnInsertValidNumber(new Runnable() {
            @Override
            public void run() {
                EditStack.Element editStackElement = editStack.RemoveValidElement();
                Drawable color;
                if(selectedCell == editStackElement.getSelectedCell())
                    color = getColorSelect();
                else
                    color = getColorUnselect();
                if(!Integer.toString(editStackElement.getSelectedValue()).equals("0")) {
                    editStackElement.getSelectedCell().setBackground(color);
                    String valueToInsert = Integer.toString(editStackElement.getSelectedValue());
                    editStackElement.getSelectedCell().setText(valueToInsert);
                }
                else
                    editStackElement.getSelectedCell().setText("");
                updateUserNameplate();
                //updateScoreOnView();
            }
        });
        gameEvents.setOnInsertInvalidNumber(new Runnable() {
            @Override
            public void run() {
                EditStack.Element editStackElement = editStack.RemoveInvalidElement();
                updateUserNameplate();
                //updateScoreOnView();
                if(!Integer.toString(editStackElement.getSelectedValue()).equals("0")) {
                    editStackElement.getSelectedCell().setText(Integer.toString(editStackElement.getSelectedValue()));
                    editStackElement.getSelectedCell().setBackground(getColorInvalid());
                }
                else
                    editStackElement.getSelectedCell().setText("");
            }
        });
        gameEvents.setOnBoardCreationError(new Runnable() {
            @Override
            public void run() {
                finishActivity(0);
            }
        });
        gameEvents.setOnBoardCreationSuccess(new Consumer<ArrayList<ArrayList<Integer>>>() {
            @Override
            public void accept(ArrayList<ArrayList<Integer>> arrayLists) {
                FillViews(arrayLists);
            }
        });

        gameEvents.setOnBoardSolved(new Runnable() {
            @Override
            public void run() {
                if(gameMode.equals(SelectUserActivity.GAME_MODE_1))
                {
                    Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                    intent.putExtra("score", gameController.GetScore());
                    startActivity(intent);
                }
                else if(gameMode.equals(SelectUserActivity.GAME_MODE_2))
                {
                    Intent intent = new Intent(getApplicationContext(), ResultsActivityM2.class);
                    intent.putExtra(SelectUserActivity.EXTRA_USERS, users);
                    intent.putExtra("scores", gameController.GetScores());
                    startActivity(intent);
                }
                else {
                    //TODO
                    Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                    intent.putExtra("score", gameController.GetScore());
                    startActivity(intent);
                }
            }
        });
        gameEvents.setOnBoardUnsolved(new Runnable(){
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), "Solução inválida", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        gameEvents.setOnReceivedHint(new Consumer<BoardPosition>() {
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
            toolbar.setTitle("Sudoku - Jogo");
            setSupportActionBar(toolbar);

            //Get intent
            Intent intent = getIntent();

            //Extract intent info
            users = (ArrayList<User>) intent.getSerializableExtra(SelectUserActivity.EXTRA_USERS);
            encodedImages = intent.getStringArrayListExtra(SelectUserActivity.EXTRA_ENCODED_IMAGES);
            useWebservice = intent.getBooleanExtra(SelectUserActivity.EXTRA_USE_WEBSERVICE, false);
            gameMode = intent.getStringExtra(SelectUserActivity.EXTRA_GAME_MODE);
            //Populate fragment
            inGamePlayerInfoFragment.onSetDataForInGamePlayerInfo(users,encodedImages);

            //Set adaptation for screen
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
        GameController.Factory gameControllerFactory = new GameController.Factory(getApplicationContext(), "easy", gameMode, users, gameEvents, useWebservice);
        gameController = ViewModelProviders.of(this, gameControllerFactory).get(GameController.class);
        gameController.InitializeBoard();

        //Set editStack using ViewModelProviders
        editStack = ViewModelProviders.of(this).get(EditStack.class);
        editStack.setBoardActivity(this);
        setScreenAdaptation(getApplicationContext());

        //Initializes the score module
        int updatedScore = gameController.GetScore();
        inGamePlayerInfoFragment.updateScoreOnView(updatedScore);
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
            gameController.InsertNumber(new BoardPosition(getInnerBoxIndex(),getCellIndex(),getBtnValue(t)));
        } else {
            Toast.makeText(this, "Please select a cell!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private int getBtnValue(View t){
        Button btn = (Button) t;
        return Integer.parseInt(btn.getText().toString());
    }

    private int getCellIndex(View view){
        String cell_index = null;
        try {
            cell_index = getIDString(view,R.id.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] id = cell_index.split("btn_box_m");
        int cell_i = (Integer.parseInt(id[1]))-1;
        return cell_i;
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

    private int getInnerBoxIndex(View view){
        int f_index=0;
        for (InnerBoardFragment frag : ibFrags)
            if(frag.ElementExists(view))
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

        boolean dkModeIsChecked = (dkMode == null?false:dkMode.isChecked());

        for (int i = 0; i < boardArray.size(); i++)
            for (int j = 0; j < boardArray.get(i).size(); j++)
                if(!gameController.ElementContainsType(i,j, Element.Type.userValue, Element.Type.hintValue))
                    ibFrags.get(i).UpdateValue(j, boardArray.get(i).get(j),(dkModeIsChecked?R.color.white:R.color.black), false);
    }

    private void Toggle_darkmode() {
        ConstraintLayout playerNameplate = (ConstraintLayout) inGamePlayerInfoFragment.getView();
        TextView nameplateText = (TextView) inGamePlayerInfoFragment.getView().findViewById(R.id.in_game_current_user_txt);
        TextView scoreText = (TextView) inGamePlayerInfoFragment.getView().findViewById(R.id.in_game_score_label);
        TextView scoreValue = (TextView) inGamePlayerInfoFragment.getView().findViewById(R.id.in_game_score_text);
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
            scoreText.setTextColor(default_color);
            scoreValue.setTextColor(default_color);
        }
        else{
            gl.setBackgroundColor(default_color);
            NubPadBackground.setBackground(default_color_kbd);
            playerNameplate.setBackground(default_color_kbd);
            nameplateText.setTextColor(dark_color_background);
            scoreText.setTextColor(dark_color_background);
            scoreValue.setTextColor(dark_color_background);
        }
        ApplyDarkMode_opt();
        ToggleHighlight();
    }

    private void ApplyDarkMode_opt(){
        for (int i = 0; i < 9; i++) {
            ArrayList<View> components = ibFrags.get(i).GetViews();
            for (int j = 0; j < 9; j++)
                if(dkMode.isChecked())
                    if(gameController.GetValuesFromStartBoard(i).get(j) == 0)
                        if(((EditText)components.get(j)) == selectedCell)
                            ((EditText)components.get(j)).setBackground((Drawable) getDrawable( R.drawable.box_back_dark_interact));
                        else
                            ((EditText)components.get(j)).setBackground((Drawable) getDrawable( R.drawable.box_back_dark));
                    else {
                        ((EditText) components.get(j)).setBackground((Drawable) getDrawable(R.drawable.box_back_dark));
                        ((EditText) components.get(j)).setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                    }
                else{
                    if(gameController.GetValuesFromStartBoard(i).get(j) == 0)
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
                if(gameController.GetValuesFromStartBoard(i).get(j) == 0)
                    if(((EditText)components.get(j)) == selectedCell)
                        ((EditText)components.get(j)).setTextColor(foregroundSelected);
                    else {
                        if(!(gameController.GetElementType(i,j)== Element.Type.hintValue))
                            ((EditText) components.get(j)).setTextColor(foregroundUnselected);
                        else {
                            if (highlightOpt.isChecked())
                                ((EditText) components.get(j)).setTextColor(hintUnselected);
                            else
                                ((EditText) components.get(j)).setTextColor(foregroundUnselected);
                        }
                    }
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
        hintUnselected = ResourcesCompat.getColor(getResources(), R.color.nice_green, null);
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
        int cell_tam;
        int key_tam;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            setBoardSizeForScreenSize(width,height,true, context);
            setInGamePlayerInfoForScreenSize(0,0,0,true);
            setBoardKeyboardForScreenSize(width, height, true, context);
        } else {
            // In portrait
            cell_tam = setBoardSizeForScreenSize(width,height,false, context);
            key_tam = setBoardKeyboardForScreenSize(width, height, false, context);
            setInGamePlayerInfoForScreenSize(height,cell_tam,key_tam,false);
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

    private void setInGamePlayerInfoForScreenSize(int height,int cell_tam,int key_tam,boolean isLandScape){
        final float scale = this.getApplicationContext().getResources().getDisplayMetrics().density;
        int image_dimension;
        ViewGroup.LayoutParams tb_size = toolbar.getLayoutParams();
        View pImage = findViewById(R.id.profile_image);
        if(isLandScape){
            image_dimension = tb_size.height;
        }
        else{
            image_dimension = (int) ((height - ((int)convertPixelsToDp(((cell_tam*9)+(key_tam*2)+tb_size.height),getApplicationContext())+84)) * scale);
            if(image_dimension > (tb_size.height * 2)) image_dimension = tb_size.height * 2;
        }
        ViewGroup.LayoutParams layoutParams = pImage.getLayoutParams();
        layoutParams.width = image_dimension;
        layoutParams.height = image_dimension;
        pImage.setLayoutParams(layoutParams);

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

    public void onFocusChange(View newSelectedCell) {
        ToggleForeground();
        Drawable unselected = getColorUnselect();
        Drawable selected = getColorSelect();
        Drawable current = newSelectedCell.getBackground();
        Drawable.ConstantState unselectedConstantState = unselected.getConstantState();
        Drawable.ConstantState currentConstantState = current.getConstantState();
        if(unselectedConstantState.equals(currentConstantState)) {
            if(selectedCell == null) {
                selectedCell = (EditText)newSelectedCell;
                selectedCell.setTextColor(foregroundSelected);
            }
            else if(selectedCell != newSelectedCell) {
                int innerboardIndex = getInnerBoxIndex(selectedCell);
                int cellIndex = getCellIndex(selectedCell);
                selectedCell.setBackground(unselected);
                Element.Type type = gameController.GetElementType(innerboardIndex,cellIndex);
                Element.Type type1 = Element.Type.hintValue;
                if(!(gameController.GetElementType(innerboardIndex,cellIndex)== Element.Type.hintValue))
                    selectedCell.setTextColor(foregroundUnselected);
                else
                    selectedCell.setTextColor(hintUnselected);
                selectedCell = (EditText) newSelectedCell;
            }
            selectedCell.setTextColor(foregroundSelected);
            newSelectedCell.setBackground(selected);
        }
        else {
            if(selectedCell == newSelectedCell) {
                int innerboardIndex = getInnerBoxIndex(selectedCell);
                int cellIndex = getCellIndex(selectedCell);
                if(!(gameController.GetElementType(innerboardIndex,cellIndex)== Element.Type.hintValue))
                    selectedCell.setTextColor(foregroundUnselected);
                else
                    selectedCell.setTextColor(hintUnselected);
                selectedCell = null;
            }
            newSelectedCell.setBackground(unselected);
        }
    }

    public void onSubmitBoard(View t) {
        gameController.ValidateSolution();
    }

    public void onBackspace(View t) {
        if(selectedCell !=null) {
            int selectedInnerBoardId = ((ViewGroup)selectedCell.getParent()).getId();
            int selectedCellId = selectedCell.getId();
            editStack.AddElement(selectedInnerBoardId, selectedCellId, 0);
            gameController.InsertNumber(new BoardPosition(getInnerBoxIndex(),getCellIndex(),0));
        } else {
            Toast.makeText(this, "Please select a cell!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onHintRequest(View t) {
        gameController.RequestHint();
    }

    public EditText getSelectedCell() {
        return selectedCell;
    }

    private void updateUserNameplate(){
        int turn = gameController.NextTurn();
        int updatedScore = gameController.GetScore();
        inGamePlayerInfoFragment.UpdateNameplateData(turn);
        inGamePlayerInfoFragment.updateScoreOnView(updatedScore);
    }
}
