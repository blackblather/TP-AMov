package com.tp_amov;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tp_amov.models.sql.User;

import java.util.ArrayList;

public class ResultsActivityM2 extends AppCompatActivity {
    private Toolbar toolbar;

    public void OnClickMenu(View v){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_m2);

        //Set toolbar info
        ArrayList<User> users;
        ArrayList<Integer> scores;
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Sudoku - Resultados");
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        users = (ArrayList<User>) intent.getSerializableExtra(SelectUserActivity.EXTRA_USERS);
        scores = (ArrayList<Integer>) intent.getIntegerArrayListExtra("scores");
        TextView userView1 = findViewById(R.id.user);
        TextView userView2 = findViewById(R.id.user2);
        TextView scoreView1 = findViewById(R.id.txtScore);
        TextView scoreView2 = findViewById(R.id.txtScore2);
        userView1.setText(users.get(0).getUsername());
        userView2.setText(users.get(1).getUsername());
        scoreView1.setText(Integer.toString(scores.get(0)));
        scoreView2.setText(Integer.toString(scores.get(1)));
    }
}
