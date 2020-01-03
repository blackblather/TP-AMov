package com.tp_amov.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.tp_amov.models.sql.SudokuContract;

public class SudokuDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
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
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
//        db.execSQL(SQL_DELETE_ENTRIES);
//        onCreate(db);

//        if (newVersion > oldVersion) {
//            db.execSQL("ALTER TABLE "+SudokuContract.User.TABLE_NAME+" ADD COLUMN "+SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE+" TEXT");
//        }
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
