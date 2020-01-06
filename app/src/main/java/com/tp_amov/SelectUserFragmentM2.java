package com.tp_amov;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.tp_amov.models.sql.User;
import com.tp_amov.tools.ProfilePictureTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        super.onViewCreated(view, savedInstanceState);

        //View to be used inside OnClickListener
        final View finalView = view;
        final ProfilePictureTools PPT = new ProfilePictureTools();
        //Gets button
        Button btn_start = (Button) view.findViewById(R.id.btn_start);

        //Assigns an "OnClickListener" to btn_start
        btn_start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //To know if user wants to update image on DB

                EditText usernameEditText_P1 = finalView.findViewById(R.id.txt_username_p1);
                EditText usernameEditText_P2 = finalView.findViewById(R.id.txt_username_p2);
                ImageView userImageView_P1 = finalView.findViewById(R.id.profile_image);
                ImageView userImageView_P2 = finalView.findViewById(R.id.profile_image2);

                String username_P1 = usernameEditText_P1.getText().toString();
                String username_P2 = usernameEditText_P2.getText().toString();

                List<Object> data_P1 = PopulateImagesAndUsers(username_P1,userImageView_P1);
                List<Object> data_P2 = PopulateImagesAndUsers(username_P2,userImageView_P2);

                User user_P1 = (User) data_P1.get(0);
                User user_P2 = (User) data_P2.get(0);
                Bitmap compressed_P1 = (Bitmap) data_P1.get(1);
                Bitmap compressed_P2 = (Bitmap) data_P2.get(1);

                //TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING
                //PPT.getAllImagesOnProfilePicturesDir(getActivity().getApplicationContext());
                //TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING

                //Create array of Users
                ArrayList<User> users = new ArrayList<>();
                users.add(0,user_P1);
                users.add(1,user_P2);
                //Create array of Bitmap encoded Strings
                ArrayList<String> encodedImages = new ArrayList<>();
                encodedImages.add(0,PPT.BitMapToString(compressed_P1));
                encodedImages.add(1,PPT.BitMapToString(compressed_P2));
                //Create intent
                Intent intent = new Intent(getContext(), BoardActivity.class);
                //Prepares Extras for next activity
                intent.putExtra(SelectUserActivity.EXTRA_USERS, users);
                intent.putExtra(SelectUserActivity.EXTRA_ENCODED_IMAGES, encodedImages);
                intent.putExtra(SelectUserActivity.EXTRA_GAME_MODE, game_mode);
                intent.putExtra(SelectUserActivity.EXTRA_USE_WEBSERVICE, GetUseWebservice());

                startActivity(intent);
            }
        });
    }

    private List<Object> PopulateImagesAndUsers(String username, ImageView userImageView){
        List<Object> list = new ArrayList<>();
        User user;
        Bitmap compressed;
        String imagePath;
        boolean overwritePic = ((SelectUserActivity)getActivity()).isOverwriteDBPictureChecked();
        final ProfilePictureTools PPT = new ProfilePictureTools();
        if(getUserController().UserExists(username)){
            //User exists on the DB
            if(!overwritePic) {
                //Get its image from DB
                imagePath = getUserController().GetUser(username).getImagePath();
                Bitmap loaded = PPT.LoadImage(imagePath);
                if(loaded == null) {
                    //Compressing image
                    compressed = PPT.GetMediumCompressedBitmapFromRawDrawable(userImageView.getDrawable());
                }else
                    compressed = loaded;
                user = new User(username,imagePath);
            }else {
                //Overwrites db img with current one
                compressed = PPT.GetMediumCompressedBitmapFromRawDrawable(userImageView.getDrawable());
                imagePath = PPT.saveImage(compressed,getActivity().getApplicationContext());
                user = new User(username,imagePath);
                getUserController().UpdateImagePath(user);
            }
        }else{
            //User does not exists on the DB
            compressed = PPT.GetMediumCompressedBitmapFromRawDrawable(userImageView.getDrawable());
            imagePath = PPT.saveImage(compressed,getActivity().getApplicationContext());
            user = new User(username,imagePath);
            getUserController().AddUser(user);
        }
        list.add(0,user);
        list.add(1,compressed);
        return list;
    }
}
