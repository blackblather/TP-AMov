package com.tp_amov;

import android.content.Intent;
import android.os.Bundle;
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

    private void LoadFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(selectedMode.equals(getString(R.string.modo_1))){
            fragment = new SelectUserFragmentM1M3();
        } else if(selectedMode.equals(getString(R.string.modo_2))){
            fragment = new SelectUserFragmentM2();
        } else if(selectedMode.equals(getString(R.string.modo_3))){
            fragment = new SelectUserFragmentM1M3();    //TODO
        } else return;

        fragmentTransaction.add(R.id.fragment_layout, fragment);
        fragmentTransaction.commitNow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        //Set toolbar info
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
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
}
