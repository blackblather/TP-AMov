package com.tp_amov;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.tp_amov.models.ProfilePictureTools;
import com.tp_amov.models.SelectUserFragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;


public class SelectUserFragmentM1M3 extends SelectUserFragment {

    public SelectUserFragmentM1M3() { }

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
        final ProfilePictureTools PPT = new ProfilePictureTools();
        //Gets button
        Button btn_start_m1m3 = (Button) view.findViewById(R.id.btn_start_m1m3);

        //Assigns an "OnClickListener" to btn_start_m1m3
        btn_start_m1m3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //if( mode == 1) -> go to board activity
                //else if (mode == 3) -> go to lobby activity
                Intent intent = new Intent(getContext(), BoardActivity.class);

                EditText usernameTxt = finalView.findViewById(R.id.txt_username);
                ImageView userImg = finalView.findViewById(R.id.profile_image);
                //Compressing image
                Bitmap compressed = PPT.GetMediumCompressedBitmapFromRawDrawable(userImg.getDrawable());
                //Save image section
                String savedImgFileName = ((SelectUserActivity)getActivity()).saveImageOnAppFileDir(compressed);
                Bitmap loaded = ((SelectUserActivity)getActivity()).LoadImage(savedImgFileName);//FOR TESTING ONLY (YET)
                ((SelectUserActivity)getActivity()).getAllImagesOnProfilePicturesDir();//FOR TESTING ONLY
                //Prepares Extras for next activity
                intent.putExtra(SelectUserActivity.EXTRA_USERNAMES, new ArrayList<String>(Collections.singletonList(usernameTxt.getText().toString())));
                intent.putExtra(SelectUserActivity.EXTRA_IMG_PATHS, new ArrayList<String>(Collections.singletonList(PPT.BitMapToString(compressed))));
                intent.putExtra(SelectUserActivity.EXTRA_USE_WEBSERVICE, GetUseWebservice());

                startActivity(intent);
            }
        });
    }
}
