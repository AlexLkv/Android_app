package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBuse extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TestingSession.db";
    public static final int DATABASE_VERSION = 1;

    /////1АЯ ТАБЛИЦА
    public static final String TABLE_RESULTS = "Results";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_ID_SESSION = "id_session";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_RIGHT_ANS = "rightAns";
    public static final String COLUMN_USER_ANS = "userAns";


    /////2АЯ ТАБЛИЦА

    public static final String TABLE_SESSION = "Session";

    public static final String COLUMN_ID2 = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COUNT_RIGHT = "count_right";
    public static final String COLUMN_COUNT_WRONG = "count_wrong";

    public DBuse(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_TABLE_QUERY1 =
            "CREATE TABLE " + TABLE_RESULTS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_STATE + " TEXT,"
                    + COLUMN_ID_SESSION + " INTEGER,"
                    + COLUMN_QUESTION + " TEXT,"
                    + COLUMN_RIGHT_ANS + " TEXT,"
                    + COLUMN_USER_ANS + " TEXT"
            +");";

    private static final String CREATE_TABLE_QUERY2 =
            "CREATE TABLE " + TABLE_SESSION + "("
                    + COLUMN_ID2 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_DATE + " TEXT,"
                    +COLUMN_COUNT_RIGHT +" TEXT,"
                    +COLUMN_COUNT_WRONG +" TEXT"
                    + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY2);
        db.execSQL(CREATE_TABLE_QUERY1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
        onCreate(db);
    }

    public void clearTable(SQLiteDatabase db, String tableName) {
        db.execSQL("DELETE FROM " + tableName);
        db.execSQL("DELETE FROM " + tableName);
    }
}
