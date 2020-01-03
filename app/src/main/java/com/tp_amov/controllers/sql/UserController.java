package com.tp_amov.controllers.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.tp_amov.models.sql.SudokuContract;
import com.tp_amov.models.sql.User;

public class UserController extends SQLController{

    public UserController(Context context) {
        super(context);
    }

    public boolean UserExists(String userName) {
        String Query = "Select * from " + SudokuContract.User.TABLE_NAME + " where " + SudokuContract.User.COLUMN_NAME_USERNAME + " LIKE '" + userName + "'";
        Cursor cursor = getDb().rawQuery(Query, null);
        int count = cursor.getCount();
        cursor.close();
        return (count == 1);
    }

    public void AddUser(User user){
        ContentValues rowValues = new ContentValues();
        rowValues.put(SudokuContract.User.COLUMN_NAME_USERNAME, user.getUsername());
        rowValues.put(SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE, user.getProfilePicture());
        getDb().insert(SudokuContract.User.TABLE_NAME, null, rowValues);
    }

    public User GetUser(String username){
        User user;

        String query = "SELECT " + SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE + " FROM " + SudokuContract.User.TABLE_NAME + " WHERE " + SudokuContract.User.COLUMN_NAME_USERNAME + " LIKE '" + username + "'";

        Cursor  cursor = getDb().rawQuery(query,null);
        cursor.moveToFirst();

        user = new User(username, cursor.getString(cursor.getColumnIndex(SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE)));

        cursor.close();

        return user;
    }

    public void UpdateImagePath(User user){
        ContentValues rowValues = new ContentValues();
        rowValues.put(SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE,user.getProfilePicture()); //These Fields should be your String values of actual column names
        getDb().update(SudokuContract.User.TABLE_NAME, rowValues, SudokuContract.User.COLUMN_NAME_USERNAME+" LIKE '"+user.getUsername()+"'", null);
    }


}
