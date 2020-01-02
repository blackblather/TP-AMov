package com.tp_amov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ResultsActivity extends AppCompatActivity {
    private Toolbar toolbar;

    public void OnClickMenu(View v){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //Set toolbar info
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Sudoku - Resultados");
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        int score = intent.getIntExtra("score",0);
        TextView scoreView = findViewById(R.id.txtScore);
        scoreView.setText(Integer.toString(score));
    }
}
