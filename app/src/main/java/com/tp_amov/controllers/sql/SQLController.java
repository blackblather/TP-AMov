package com.tp_amov.controllers.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

abstract class SQLController {
    private SQLiteDatabase db;

    SQLController(Context context){
        SudokuDbHelper dbHelper = new SudokuDbHelper(context);
        db = dbHelper.getReadableDatabase();
        dbHelper.close();
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void Close(){
        db.close();
    }
}
