package com.tp_amov;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

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

        setRetainInstance(true);

        //Register "this" in parent Activity
        ((BoardActivity)getActivity()).ib_frags.add(this);

        ViewGroup root = (ViewGroup) getView();

        //Get total children (0 if root == null)
        int childViewCount = root != null ? root.getChildCount() : 0;

        //Add children to ArrayList
        for (int i = 0; i < childViewCount; i++){
            View childV = root.getChildAt(i);
            nums.add(childV);
        }
    }

    void UpdateValue(int index, Integer value)
    {
        EditText temp = (EditText)nums.get(index);
        temp.setText(value.toString());
        int readonly_color = ResourcesCompat.getColor(getResources(), R.color.black, null);
        temp.setTextColor(readonly_color);
        temp.setEnabled(false);
    }

    ArrayList<View> GetViews(){
        return nums;
    }

    boolean ElementExists(View v)
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
