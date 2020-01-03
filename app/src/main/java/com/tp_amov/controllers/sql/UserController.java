package com.tp_amov.controllers.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tp_amov.models.sql.SudokuContract;
import com.tp_amov.models.sql.User;
import com.tp_amov.tools.SudokuDbHelper;

public class UserController extends SQLController{

    public UserController(Context context) {
        super(context);
    }

    UserController(SQLiteDatabase db, SudokuDbHelper dbHelper) {
        super(db,dbHelper);
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

        String query = "SELECT * FROM " + SudokuContract.User.TABLE_NAME + " WHERE " + SudokuContract.User.COLUMN_NAME_USERNAME + " LIKE '" + username + "'";

        Cursor  cursor = getDb().rawQuery(query,null);
        cursor.moveToFirst();

        int id = cursor.getInt(cursor.getColumnIndex(SudokuContract.User._ID));
        String profilePicture = cursor.getString(cursor.getColumnIndex(SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE));

        user = new User(id, username, profilePicture);

        cursor.close();

        return user;
    }

    public void UpdateImagePath(User user){
        ContentValues rowValues = new ContentValues();

        //These Fields should be your String values of actual column names
        rowValues.put(SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE,user.getProfilePicture());

        getDb().update(SudokuContract.User.TABLE_NAME, rowValues, SudokuContract.User.COLUMN_NAME_USERNAME+" LIKE '"+user.getUsername()+"'", null);
    }
}
