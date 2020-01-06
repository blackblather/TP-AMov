package com.tp_amov;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

public class SelectUserFragmentM2 extends SelectUserFragment {
    public static final String GAME_MODE = "M2";

    public SelectUserFragmentM2() {
        // Required empty public constructor
        game_mode = SelectUserActivity.GAME_MODE_2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_user_fragment_m2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

    }
}
