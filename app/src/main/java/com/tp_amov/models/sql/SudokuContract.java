package com.tp_amov.models.sql;

import android.provider.BaseColumns;

public final class SudokuContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private SudokuContract() {}

    public static class User implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PROFILE_PICTURE = "profile_picture";
        static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_USERNAME + " TEXT,"+
                COLUMN_NAME_PROFILE_PICTURE + " TEXT); ";
    }

    public static class UserGame implements BaseColumns {
        public static final String TABLE_NAME = "user_game";
        public static final String COLUMN_NAME_ID_USER = "id_user";
        public static final String COLUMN_NAME_ID_GAME = "id_game";
        static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_ID_USER + " INTEGER," +
                COLUMN_NAME_ID_GAME + " INTEGER," +
                "FOREIGN KEY (" + COLUMN_NAME_ID_USER + ") REFERENCES " + User.TABLE_NAME + " (" + User._ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
                "FOREIGN KEY (" + COLUMN_NAME_ID_GAME + ") REFERENCES " + Game.TABLE_NAME + " (" + Game._ID + ") ON DELETE CASCADE ON UPDATE CASCADE); ";
    }

    public static class Game implements BaseColumns {
        public static final String TABLE_NAME = "game";
        public static final String COLUMN_NAME_ID_GAME_MODE = "id_game_mode";
        static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_ID_GAME_MODE + " INTEGER," +
                "FOREIGN KEY (" + COLUMN_NAME_ID_GAME_MODE + ") REFERENCES " + GameMode.TABLE_NAME + " (" + GameMode._ID + ") ON DELETE CASCADE ON UPDATE CASCADE); ";
    }

    public static class GameMode implements BaseColumns {
        public static final String TABLE_NAME = "game_mode";
        public static final String COLUMN_NAME_GAME_MODE_NAME = "name";
        static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_GAME_MODE_NAME + " TEXT); ";
    }
}
