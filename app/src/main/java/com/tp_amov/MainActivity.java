package com.tp_amov;

import android.content.Context;
import android.net.*;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                });
            }
            @Override
            public void onLost(@NonNull Network network) {
                //runOnUiThread(() ->btn_m3.setEnabled(false));
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
