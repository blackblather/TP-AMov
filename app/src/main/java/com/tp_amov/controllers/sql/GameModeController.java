package com.tp_amov.controllers.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tp_amov.models.sql.GameMode;
import com.tp_amov.models.sql.SudokuContract;
import com.tp_amov.tools.SudokuDbHelper;

public class GameModeController extends SQLController {
    public GameModeController(Context context) {
        super(context);
    }

    public GameModeController(SQLiteDatabase db, SudokuDbHelper dbHelper) {
        super(db,dbHelper);
    }

    public GameMode GetGameMode(int id){
        String query = "SELECT * FROM " + SudokuContract.GameMode.TABLE_NAME + " WHERE " + SudokuContract.GameMode._ID + " = " + id;

        Cursor cursor = getDb().rawQuery(query,null);
        cursor.moveToFirst();

        String name = cursor.getString(cursor.getColumnIndex(SudokuContract.GameMode.COLUMN_NAME_GAME_MODE_NAME));

        cursor.close();

        return new GameMode(id, name);
    }

    public GameMode GetGameMode(String name){
        String query = "SELECT * FROM " + SudokuContract.GameMode.TABLE_NAME + " WHERE " + SudokuContract.GameMode.COLUMN_NAME_GAME_MODE_NAME + " LIKE '" + name + "'";

        Cursor cursor = getDb().rawQuery(query,null);
        cursor.moveToFirst();

        int id = cursor.getInt(cursor.getColumnIndex(SudokuContract.GameMode._ID));

        cursor.close();

        return new GameMode(id, name);
    }
}
