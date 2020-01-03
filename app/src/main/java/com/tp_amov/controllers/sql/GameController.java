package com.tp_amov.controllers.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tp_amov.models.sql.Game;
import com.tp_amov.models.sql.GameMode;
import com.tp_amov.models.sql.SudokuContract;

public class GameController extends SQLController {
    GameController(Context context) {
        super(context);
    }

    GameController(SQLiteDatabase db) {
        super(db);
    }

    public void AddGame(Game game){
        ContentValues rowValues = new ContentValues();

        rowValues.put(SudokuContract.Game.COLUMN_NAME_ID_GAME_MODE, game.getGameMode().getId());

        getDb().insert(SudokuContract.Game.TABLE_NAME, null, rowValues);
    }

    public Game GetGame(int id){
        GameModeController gameModeController = new GameModeController(getDb());

        String query = "SELECT * FROM " + SudokuContract.Game.TABLE_NAME + " WHERE " + SudokuContract.Game._ID + " = " + id;

        Cursor cursor = getDb().rawQuery(query,null);
        cursor.moveToFirst();

        int gameModeId = cursor.getInt(cursor.getColumnIndex(SudokuContract.Game.COLUMN_NAME_ID_GAME_MODE));

        GameMode gameMode = gameModeController.GetGameMode(gameModeId);

        Game game = new Game(id, gameMode);

        cursor.close();
        return game;
    }
}
