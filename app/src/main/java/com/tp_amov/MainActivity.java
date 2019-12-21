package com.tp_amov;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    static final String EXTRA_SELECTED_MODE = "com.tp_amov.SELECTED_MODE";

    public void OnModeBtnClick(View v){
        //Clicked button is enabled
        Intent intent = new Intent(this, SelectUserActivity.class);
        String message = ((Button) v).getText().toString();
        intent.putExtra(EXTRA_SELECTED_MODE, message);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
