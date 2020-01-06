package com.tp_amov.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.tp_amov.models.sql.SudokuContract;

public class SudokuDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "Sudoku.db";

    public SudokuDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SudokuContract.User.SQL_CREATE_TABLE);
        db.execSQL(SudokuContract.GameMode.SQL_CREATE_TABLE);
        db.execSQL(SudokuContract.Game.SQL_CREATE_TABLE);
        db.execSQL(SudokuContract.UserGame.SQL_CREATE_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SudokuContract.UserGame.SQL_DELETE_TABLE);
        db.execSQL(SudokuContract.Game.SQL_DELETE_TABLE);
        db.execSQL(SudokuContract.GameMode.SQL_DELETE_TABLE);
        db.execSQL(SudokuContract.User.SQL_DELETE_TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
