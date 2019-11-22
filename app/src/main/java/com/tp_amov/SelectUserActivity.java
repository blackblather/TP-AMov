package com.tp_amov;

import android.content.Intent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SelectUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        Intent intent = getIntent();
        String selectedMode = intent.getStringExtra(MainActivity.EXTRA_SELECTED_MODE);

        TextView textView = findViewById(R.id.textView);
        textView.setText(selectedMode);
    }
}
