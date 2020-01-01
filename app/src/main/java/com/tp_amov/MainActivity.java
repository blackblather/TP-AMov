package com.tp_amov;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.tp_amov.models.sql.SudokuContract;
import com.tp_amov.models.sql.SudokuDbHelper;

public class MainActivity extends AppCompatActivity {
    static final String EXTRA_SELECTED_MODE = "com.tp_amov.SELECTED_MODE";
    private SQLiteDatabase db;

    public void OnModeBtnClick(View v){
        //Clicked button is enabled
        Intent intent = new Intent(this, SelectUserActivity.class);
        String message = ((Button) v).getTag().toString();
        intent.putExtra(EXTRA_SELECTED_MODE, message);
        startActivity(intent);
    }

    private void InitDatabase(){
        SudokuDbHelper dbHelper = new SudokuDbHelper(getApplicationContext());

        // Gets the data repository in write mode
        db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + SudokuContract.GameMode.TABLE_NAME, null);

        if (c.moveToFirst()){
            int count = c.getInt(0);
            //Checks if default values already exist
            if (count == 0){
                ContentValues rowValues = new ContentValues();

                //Insert default values
                rowValues.put(SudokuContract.GameMode.COLUMN_NAME_GAME_MODE_NAME, "M1");
                db.insert(SudokuContract.GameMode.TABLE_NAME, null, rowValues);

                rowValues.put(SudokuContract.GameMode.COLUMN_NAME_GAME_MODE_NAME, "M2");
                db.insert(SudokuContract.GameMode.TABLE_NAME, null, rowValues);

                rowValues.put(SudokuContract.GameMode.COLUMN_NAME_GAME_MODE_NAME, "M3");
                db.insert(SudokuContract.GameMode.TABLE_NAME, null, rowValues);
            }
        }

        c.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Creates database and insert default values
        InitDatabase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Set toolbar info
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Sudoku - Início");
        setSupportActionBar(toolbar);

        //By default, button is disabled
        ((Button) findViewById(R.id.btn_m3)).setEnabled(false);

        //Create listeners to change the "ENABLED" property of "btn_m3" based on network changes
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
        connMgr.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback(){
            private Button btn_m3 = (Button) findViewById(R.id.btn_m3);
            @Override
            public void onAvailable(@NonNull Network network) {
                //May be called on startup
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_m3.setEnabled(true);
                    }
                });
            }
            @Override
            public void onLost(@NonNull Network network) {
                //Is never called on startup
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_m3.setEnabled(false);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
