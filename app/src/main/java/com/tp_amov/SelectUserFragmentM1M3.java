package com.tp_amov;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.annotation.Nullable;

import com.tp_amov.models.ProfilePictureTools;
import com.tp_amov.models.SelectUserFragment;
import com.tp_amov.models.sql.SudokuContract;
import com.tp_amov.models.sql.SudokuDbHelper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SelectUserFragmentM1M3 extends SelectUserFragment {
    private SQLiteDatabase db;

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
                boolean overwritePic = ((SelectUserActivity)getActivity()).isUseDBPictureChecked(); //To know if user wants to update image on DB
                //if( mode == 1) -> go to board activity
                //else if (mode == 3) -> go to lobby activity
                Intent intent = new Intent(getContext(), BoardActivity.class);

                EditText usernameTxt = finalView.findViewById(R.id.txt_username);
                ImageView userImg = finalView.findViewById(R.id.profile_image);

                Bitmap compressed;
                //Compressing image
                if(CheckUser(usernameTxt.getText().toString())){
                    //User exists on the DB
                    if(!overwritePic) {
                        //Get its image from DB
                        String imgName = GetUserImagePathFromDatabase(usernameTxt.getText().toString());
                        Bitmap loaded = ((SelectUserActivity) getActivity()).LoadImage(imgName);
                        if(loaded == null) {
                            compressed = PPT.GetMediumCompressedBitmapFromRawDrawable(userImg.getDrawable());
                        }else
                            compressed = loaded;
                    }else {
                        //Overwrites db img with current one
                        compressed = PPT.GetMediumCompressedBitmapFromRawDrawable(userImg.getDrawable());
                        String savedImgFileName = ((SelectUserActivity)getActivity()).saveImageOnAppFileDir(compressed);
                        UpdateProfilePicturePathsOnDB(usernameTxt.getText().toString(),savedImgFileName);
                    }
                }else{
                    //User does not exists on the DB
                    compressed = PPT.GetMediumCompressedBitmapFromRawDrawable(userImg.getDrawable());
                    String savedImgFileName = ((SelectUserActivity)getActivity()).saveImageOnAppFileDir(compressed);
                    InsertNewUserOnDB(usernameTxt.getText().toString(),savedImgFileName);
                }
                //FOR TESTING ONLY (YET)
                ((SelectUserActivity)getActivity()).getAllImagesOnProfilePicturesDir();//FOR TESTING ONLY
                //Prepares Extras for next activity
                intent.putExtra(SelectUserActivity.EXTRA_USERNAMES, new ArrayList<String>(Collections.singletonList(usernameTxt.getText().toString())));
                intent.putExtra(SelectUserActivity.EXTRA_IMG_PATHS, new ArrayList<String>(Collections.singletonList(PPT.BitMapToString(compressed))));
                intent.putExtra(SelectUserActivity.EXTRA_USE_WEBSERVICE, GetUseWebservice());

                startActivity(intent);
            }
        });
    }

    public boolean CheckUser(String userName) {
        SudokuDbHelper dbHelper = new SudokuDbHelper(getContext());
        db = dbHelper.getReadableDatabase();

        String Query = "Select * from " + SudokuContract.User.TABLE_NAME + " where " + SudokuContract.User.COLUMN_NAME_USERNAME + " LIKE '" + userName + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public String GetUserImagePathFromDatabase(String userName){
        SudokuDbHelper dbHelper = new SudokuDbHelper(getContext());
        db = dbHelper.getReadableDatabase();

        String query = "SELECT "+SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE+" FROM "+SudokuContract.User.TABLE_NAME+" WHERE "+SudokuContract.User.COLUMN_NAME_USERNAME+" LIKE '" + userName + "'";

        Cursor  cursor = db.rawQuery(query,null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        String imageName;
        try {
            imageName = cursor.getString(cursor.getColumnIndex(SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE));
        }catch (NullPointerException np){
            imageName = null;
        }
        cursor.close();
        return imageName;
    }

    public void InsertNewUserOnDB(String userName, String imgPath){
        SudokuDbHelper dbHelper = new SudokuDbHelper(getContext());
        db = dbHelper.getWritableDatabase();
        ContentValues rowValues = new ContentValues();
        rowValues.put(SudokuContract.User.COLUMN_NAME_USERNAME, userName);
        rowValues.put(SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE,imgPath);
        db.insert(SudokuContract.User.TABLE_NAME, null, rowValues);
    }

    public void UpdateProfilePicturePathsOnDB(String userName, String imgPath){
        SudokuDbHelper dbHelper = new SudokuDbHelper(getContext());
        db = dbHelper.getWritableDatabase();
        ContentValues rowValues = new ContentValues();
        rowValues.put(SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE,imgPath); //These Fields should be your String values of actual column names
        db.update(SudokuContract.User.TABLE_NAME, rowValues, SudokuContract.User.COLUMN_NAME_USERNAME+" LIKE '"+userName+"'", null);
    }

    public void CaptureAllUsers(){
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

    }
}
