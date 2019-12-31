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
                //Gets the Bitmap
                Bitmap img = drawableToBitmap(userImg.getDrawable());
                //Get Bitmap center
                Bitmap centeredImg = cropImageToCenter(img);
                //Resize image
                Bitmap resized = Bitmap.createScaledBitmap(centeredImg, 240, 240, true);
                //Compressing image
                Bitmap compressed = mediumBitmapCompression(resized);
                //Save image section
                String savedImgFileName = ((SelectUserActivity)getActivity()).saveImageOnAppFileDir(compressed);
                Bitmap loaded = ((SelectUserActivity)getActivity()).LoadImage(savedImgFileName);//FOR TESTING ONLY (YET)
                ((SelectUserActivity)getActivity()).getAllImagesOnProfilePicturesDir();//FOR TESTING ONLY
                //Prepares Extras for next activity
                intent.putExtra(SelectUserActivity.EXTRA_USERNAMES, new ArrayList<String>(Collections.singletonList(usernameTxt.getText().toString())));
                intent.putExtra(SelectUserActivity.EXTRA_IMG_PATHS, new ArrayList<String>(Collections.singletonList(BitMapToString(compressed))));
                intent.putExtra(SelectUserActivity.EXTRA_USE_WEBSERVICE, GetUseWebservice());

                startActivity(intent);
            }
        });
    }

    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private Bitmap mediumBitmapCompression(Bitmap bitmap){
        // Initialize a new ByteArrayStream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //Sets compression attributes
        bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
        //Creates the new compressed image
        byte[] byteArray = stream.toByteArray();
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
    }

    private Bitmap cropImageToCenter(Bitmap bitmap){
        Bitmap centeredImg;
        if (bitmap.getWidth() >= bitmap.getHeight()){

            centeredImg = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );

        }else{

            centeredImg = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }
        return centeredImg;
    }
}
