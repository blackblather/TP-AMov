package com.tp_amov;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SelectUserActivity extends AppCompatActivity {

    private void LoadFragment(String selectedMode){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment;
        if(selectedMode.equals(getString(R.string.modo_1))){
            fragment = new SelectUserFragmentM1M3();
        } else if(selectedMode.equals(getString(R.string.modo_2))){
            fragment = new SelectUserFragmentM2();    //TODO: Create SelectUserFragmentM2
        } else if(selectedMode.equals(getString(R.string.modo_3))){
            fragment = new SelectUserFragmentM1M3();    //TODO
        } else return;

        fragmentTransaction.add(R.id.select_user, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        Intent intent = getIntent();
        String selectedMode = intent.getStringExtra(MainActivity.EXTRA_SELECTED_MODE);

        if(selectedMode != null)
            LoadFragment(selectedMode);
        else{
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }
}
