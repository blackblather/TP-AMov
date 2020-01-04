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


public class SelectUserFragmentM1 extends SelectUserFragment {
    public static final String GAME_MODE = "M1";

    public SelectUserFragmentM1() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_user_fragment_m1, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
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
                boolean overwritePic = ((SelectUserActivity)getActivity()).isOverwriteDBPictureChecked();

                EditText usernameEditText = finalView.findViewById(R.id.txt_username);
                ImageView userImageView = finalView.findViewById(R.id.profile_image);

                String username = usernameEditText.getText().toString();

                Bitmap compressed;
                String imagePath;
                User user;

                if(getUserController().UserExists(username)){
                    //User exists on the DB
                    if(!overwritePic) {
                        //Get its image from DB
                        imagePath = getUserController().GetUser(username).getImagePath();
                        Bitmap loaded = ((SelectUserActivity) getActivity()).LoadImage(imagePath);
                        if(loaded == null) {
                            //Compressing image
                            compressed = PPT.GetMediumCompressedBitmapFromRawDrawable(userImageView.getDrawable());
                        }else
                            compressed = loaded;
                        user = new User(username,imagePath);
                    }else {
                        //Overwrites db img with current one
                        compressed = PPT.GetMediumCompressedBitmapFromRawDrawable(userImageView.getDrawable());
                        imagePath = ((SelectUserActivity)getActivity()).saveImage(compressed);
                        user = new User(username,imagePath);
                        getUserController().UpdateImagePath(user);
                    }
                }else{
                    //User does not exists on the DB
                    compressed = PPT.GetMediumCompressedBitmapFromRawDrawable(userImageView.getDrawable());
                    imagePath = ((SelectUserActivity)getActivity()).saveImage(compressed);
                    user = new User(username,imagePath);
                    getUserController().AddUser(user);
                }

                //TESTING TESTING TESTING TESTING TESTING TESTING TESTING
                //((SelectUserActivity)getActivity()).getAllImagesOnProfilePicturesDir();
                //TESTING TESTING TESTING TESTING TESTING TESTING TESTING

                //Create array of Users
                ArrayList<User> users = new ArrayList<>();
                users.add(user);

                //Create intent
                Intent intent = new Intent(getContext(), BoardActivity.class);
                //Prepares Extras for next activity
                intent.putExtra(SelectUserActivity.EXTRA_USERS, users);
                intent.putExtra(SelectUserActivity.EXTRA_ENCODED_IMAGES, new ArrayList<String>(Collections.singletonList(PPT.BitMapToString(compressed))));
                intent.putExtra(SelectUserActivity.EXTRA_GAME_MODE, GAME_MODE);
                intent.putExtra(SelectUserActivity.EXTRA_USE_WEBSERVICE, GetUseWebservice());

                startActivity(intent);
            }
        });
    }

/*    public void CaptureAllUsers(){
        //FOR TESTING PURPOSES
        SudokuDbHelper dbHelper = new SudokuDbHelper(getContext());
        db = dbHelper.getReadableDatabase();

        List<String> uNames = new ArrayList<>();
        List<String> imgPaths = new ArrayList<>();
        String query = "select * from "+SudokuContract.User.TABLE_NAME;
        Cursor  cursor1 = db.rawQuery(query,null);

        if (cursor1.moveToFirst()) {
            while (!cursor1.isAfterLast()) {
                String name = cursor1.getString(cursor1.getColumnIndex(SudokuContract.User.COLUMN_NAME_USERNAME));
                String img = cursor1.getString(cursor1.getColumnIndex(SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE));
                uNames.add(name);
                imgPaths.add(img);
                cursor1.moveToNext();
            }
        }
    }*/
}
