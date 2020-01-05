package com.tp_amov.controllers.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tp_amov.models.sql.SudokuContract;
import com.tp_amov.models.sql.User;
import com.tp_amov.models.sql.UserGame;
import com.tp_amov.tools.SudokuDbHelper;

import java.util.List;

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
        rowValues.put(SudokuContract.User.COLUMN_NAME_PICTURE_PATH, user.getImagePath());

        getDb().insert(SudokuContract.User.TABLE_NAME, null, rowValues);
    }

    //Get user by: username
    public User GetUser(String username){
        UserGameController userGameController = new UserGameController(getDb(), getDbHelper());
        User user;

        String query = "SELECT * FROM " + SudokuContract.User.TABLE_NAME + " WHERE " + SudokuContract.User.COLUMN_NAME_USERNAME + " LIKE '" + username + "'";

        Cursor  cursor = getDb().rawQuery(query,null);
        cursor.moveToFirst();

        int id = cursor.getInt(cursor.getColumnIndex(SudokuContract.User._ID));
        String profilePicture = cursor.getString(cursor.getColumnIndex(SudokuContract.User.COLUMN_NAME_PICTURE_PATH));
        List<UserGame> userGames = userGameController.GetUserGame(id, UserGameController.SearchType.userId);

        user = new User(id, username, profilePicture, userGames);

        cursor.close();

        return user;
    }

    //Get user by: id (used in UserGameController.java)
    User GetUser(Integer id){
        User user;

        String query = "SELECT * FROM " + SudokuContract.User.TABLE_NAME + " WHERE " + SudokuContract.User.COLUMN_NAME_USERNAME + " = " + id;

        Cursor  cursor = getDb().rawQuery(query,null);
        cursor.moveToFirst();

        String username = cursor.getString(cursor.getColumnIndex(SudokuContract.User.COLUMN_NAME_USERNAME));
        String profilePicture = cursor.getString(cursor.getColumnIndex(SudokuContract.User.COLUMN_NAME_PICTURE_PATH));

        user = new User(id, username, profilePicture);  //not using list<UserGame> here to avoid infinite recursion

        cursor.close();

        return user;
    }

    public void UpdateImagePath(User user){
        ContentValues rowValues = new ContentValues();

        //These Fields should be your String values of actual column names
        rowValues.put(SudokuContract.User.COLUMN_NAME_PICTURE_PATH,user.getImagePath());

        getDb().update(SudokuContract.User.TABLE_NAME, rowValues, SudokuContract.User.COLUMN_NAME_USERNAME+" LIKE '"+user.getUsername()+"'", null);
    }
}
