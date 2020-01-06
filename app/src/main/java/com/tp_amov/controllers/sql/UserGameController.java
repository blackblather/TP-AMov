package com.tp_amov.controllers.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tp_amov.models.sql.Game;
import com.tp_amov.models.sql.SudokuContract;
import com.tp_amov.models.sql.User;
import com.tp_amov.models.sql.UserGame;
import com.tp_amov.tools.SudokuDbHelper;

import java.util.ArrayList;

public class UserGameController extends SQLController {
    public enum SearchType {
        userId,
        gameId
    }

    UserGameController(Context context) {
        super(context);
    }

    public UserGameController(SQLiteDatabase db, SudokuDbHelper dbHelper) {
        super(db,dbHelper);
    }

    public void AddUserGame(UserGame userGame){
        ContentValues rowValues = new ContentValues();

        rowValues.put(SudokuContract.UserGame.COLUMN_NAME_ID_USER, userGame.getUser().getId());
        rowValues.put(SudokuContract.UserGame.COLUMN_NAME_ID_GAME, userGame.getGame().getId());
        rowValues.put(SudokuContract.UserGame.COLUMN_NAME_SCORE, userGame.getScore());

        getDb().insert(SudokuContract.UserGame.TABLE_NAME, null, rowValues);
    }

    ArrayList<UserGame> GetUserGame(int id, SearchType searchFor){
        String query = "";

        switch (searchFor){
            case userId: query = "SELECT * FROM " + SudokuContract.UserGame.TABLE_NAME + " WHERE " + SudokuContract.UserGame.COLUMN_NAME_ID_USER + " = " + id; break;
            case gameId: query = "SELECT * FROM " + SudokuContract.UserGame.TABLE_NAME + " WHERE " + SudokuContract.UserGame.COLUMN_NAME_ID_GAME + " = " + id; break;
        }

        ArrayList<UserGame> userGames = new ArrayList<>();

        Cursor cursor = getDb().rawQuery(query,null);
        if(cursor != null){
            UserController userController = new UserController(getDb(), getDbHelper());
            GameController gameController = new GameController(getDb(), getDbHelper());
            while(cursor.moveToNext()) {
                User user = userController.GetUser(cursor.getInt(cursor.getColumnIndex(SudokuContract.UserGame.COLUMN_NAME_ID_USER)));
                Game game = gameController.GetGame(cursor.getInt(cursor.getColumnIndex(SudokuContract.UserGame.COLUMN_NAME_ID_GAME)), GameController.GetType.withUserGames);
                int score = cursor.getInt(cursor.getColumnIndex(SudokuContract.UserGame.COLUMN_NAME_SCORE));
                userGames.add(new UserGame(id, user, game, score));
            }
            cursor.close();
        }

        return userGames;
    }
}
