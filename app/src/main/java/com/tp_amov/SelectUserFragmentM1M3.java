package com.tp_amov;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
}
