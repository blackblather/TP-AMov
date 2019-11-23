package com.tp_amov;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class SelectUserFragmentM1M3 extends Fragment {
    public SelectUserFragmentM1M3() { }

    static SelectUserFragmentM1M3 newInstance(String param1, String param2) {
        return new SelectUserFragmentM1M3();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_user_fragment_m1_m3, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        //View to be used inside OnClickListener
        final View finalView = view;

        //Gets button
        Button btn_start_m1m3 = (Button) view.findViewById(R.id.btn_m1);

        //Assigns an "OnClickListener" to btn_start_m1m3
        btn_start_m1m3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;

                EditText editText = (EditText) finalView.findViewById(R.id.txt_username);
                String username = editText.getText().toString();

                Toast toast = Toast.makeText(context, username, duration);
                toast.show();
            }
        });
    }
}
