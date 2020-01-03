package com.tp_amov.controllers.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.tp_amov.models.sql.SudokuContract;
import com.tp_amov.models.sql.UserGame;
import com.tp_amov.tools.SudokuDbHelper;

public class UserGameController extends SQLController {
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
}
