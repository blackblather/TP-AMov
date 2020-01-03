package com.tp_amov;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.fragment.app.Fragment;
import com.tp_amov.models.sql.SudokuContract;
import com.tp_amov.models.sql.SudokuDbHelper;

abstract class SelectUserFragment extends Fragment {
    private SQLiteDatabase db;
    private boolean useWebservice = false;

    void SetUseWebservice(boolean useWebservice) {
        this.useWebservice = useWebservice;
    }

    boolean GetUseWebservice(){
        return useWebservice;
    }

    //Database functions
    boolean UserExists(String userName) {
        SudokuDbHelper dbHelper = new SudokuDbHelper(getContext());
        db = dbHelper.getReadableDatabase();

        String Query = "Select * from " + SudokuContract.User.TABLE_NAME + " where " + SudokuContract.User.COLUMN_NAME_USERNAME + " LIKE '" + userName + "'";
        Cursor cursor = db.rawQuery(Query, null);
        int count = cursor.getCount();
        cursor.close();
        return (count == 1);
    }

    void AddUser(String userName, String imgPath){
        SudokuDbHelper dbHelper = new SudokuDbHelper(getContext());
        db = dbHelper.getWritableDatabase();
        ContentValues rowValues = new ContentValues();
        rowValues.put(SudokuContract.User.COLUMN_NAME_USERNAME, userName);
        rowValues.put(SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE,imgPath);
        db.insert(SudokuContract.User.TABLE_NAME, null, rowValues);
    }

    void UpdateImagePath(String userName, String imgPath){
        SudokuDbHelper dbHelper = new SudokuDbHelper(getContext());
        db = dbHelper.getWritableDatabase();
        ContentValues rowValues = new ContentValues();
        rowValues.put(SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE,imgPath); //These Fields should be your String values of actual column names
        db.update(SudokuContract.User.TABLE_NAME, rowValues, SudokuContract.User.COLUMN_NAME_USERNAME+" LIKE '"+userName+"'", null);
    }
    String GetImagePath(String userName){
        SudokuDbHelper dbHelper = new SudokuDbHelper(getContext());
        db = dbHelper.getReadableDatabase();

        String query = "SELECT "+SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE+" FROM "+SudokuContract.User.TABLE_NAME+" WHERE "+SudokuContract.User.COLUMN_NAME_USERNAME+" LIKE '" + userName + "'";

        Cursor  cursor = db.rawQuery(query,null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        String imageName;
        try {
            imageName = cursor.getString(cursor.getColumnIndex(SudokuContract.User.COLUMN_NAME_PROFILE_PICTURE));
        }catch (NullPointerException np){
            imageName = null;
        }
        cursor.close();
        return imageName;
    }
}
