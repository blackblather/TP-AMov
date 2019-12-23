package com.tp_amov;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SelectUserActivity extends AppCompatActivity {
    static final String EXTRA_USERNAMES = "usernames";
    static final String EXTRA_IMG_PATHS = "imgPaths";
    private String selectedMode;
    private Fragment fragment;
    private Toolbar toolbar;
    private Bundle savedInstanceState;
    private MenuItem useWebservice;

    private void LoadFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (selectedMode){
            case "btn_m1": fragment = new SelectUserFragmentM1M3(); break;
            case "btn_m2": fragment = new SelectUserFragmentM2(); break;
            case "btn_m3": fragment = new SelectUserFragmentM1M3(); break;  //TODO
            default: return;
        }

        fragmentTransaction.add(R.id.fragment_layout, fragment);
        fragmentTransaction.commitNow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        //Set toolbar info
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Sudoku - Configuração do Jogo");
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        selectedMode = intent.getStringExtra(MainActivity.EXTRA_SELECTED_MODE);

        if(selectedMode != null)
            LoadFragment();
        else{
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_settings_menu, menu);
        useWebservice = menu.findItem(R.id.use_webservice);
        if(savedInstanceState != null) //Isto está aqui porque os menus são criados depois do onRestoreInstanceState porque os menus são criados depois!!
            useWebservice.setChecked(savedInstanceState.getBoolean("useWebservice", false));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.isChecked())
            item.setChecked(false);
        else
            item.setChecked(true);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("useWebservice", useWebservice.isChecked());
        super.onSaveInstanceState(outState);
    }
}
