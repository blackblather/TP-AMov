package com.tp_amov;

import android.content.Context;
import android.net.*;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.btn_m3)).setEnabled(false); //By default, button is disabled

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build();
        connMgr.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback(){
            private Button btn_m3 = (Button) findViewById(R.id.btn_m3);
            @Override
            public void onAvailable(@NonNull Network network) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_m3.setEnabled(true);
                    }
                }); //May be called on startup
            }
            @Override
            public void onLost(@NonNull Network network) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_m3.setEnabled(false);
                    }
                }); //Is never called on startup
            }
        });
    }
}
