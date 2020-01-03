package com.tp_amov.controllers.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.tp_amov.tools.SudokuDbHelper;

abstract class SQLController {
    private SQLiteDatabase db;
    private SudokuDbHelper dbHelper;

    SQLController(Context context){
        dbHelper = new SudokuDbHelper(context);
        db = dbHelper.getReadableDatabase();
    }

    SQLController(SQLiteDatabase db, SudokuDbHelper dbHelper){
        this.dbHelper = dbHelper;
        this.db = db;
    }

    SQLiteDatabase getDb() {
        return db;
    }

    public SudokuDbHelper getDbHelper() {
        return dbHelper;
    }

    public void Close(){
        db.close();
        dbHelper.close();
    }
}
