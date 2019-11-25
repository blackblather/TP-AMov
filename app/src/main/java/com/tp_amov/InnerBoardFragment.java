package com.tp_amov;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class InnerBoardFragment extends Fragment {
    private ArrayList<View> nums = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String f_tag = this.getTag();
        String[]f_id = f_tag.split("InnerBoard_");
        int index_f = Integer.parseInt(f_id[1]);
        InnerBoardFragment ibf = (InnerBoardFragment)this;
        ((BoardActivity)this.getActivity()).ib_frags.add(index_f-1,ibf);
        ViewGroup rootView = (ViewGroup) getView();
        int childViewCount = rootView.getChildCount();
        for (int i=0; i<childViewCount;i++){
            View childV = rootView.getChildAt(i);
            try {
                String id_tag = ((BoardActivity)this.getActivity()).getIDString(childV,R.id.class);
                if(id_tag.contains("btn_box_m"))
                {
                    String[]id = id_tag.split("btn_box_m");
                    int index_btn = Integer.parseInt(id[1]);
                    nums.add(index_btn-1,childV);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void UpdateValue(int index, Integer value)
    {
        EditText temp = (EditText)nums.get(index);
        temp.setText(value.toString());
        int readonly_color = ResourcesCompat.getColor(getResources(), R.color.black, null);
        temp.setTextColor(readonly_color);
        temp.setEnabled(false);
    }

    public ArrayList<View> GetViews(){
        return nums;
    }

    public boolean ElementExists(View v)
    {
        return nums.contains(v);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inner_board, container, false);
    }
}
