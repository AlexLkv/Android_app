package com.example.myapplication;

import static com.example.myapplication.DBMatches.COLUMN_COUNT_RIGHT;
import static com.example.myapplication.DBMatches.COLUMN_COUNT_WRONG;
import static com.example.myapplication.DBMatches.COLUMN_DATE;
import static com.example.myapplication.DBMatches.COLUMN_ID;
import static com.example.myapplication.DBMatches.COLUMN_ID2;
import static com.example.myapplication.DBMatches.COLUMN_ID_SESSION;
import static com.example.myapplication.DBMatches.COLUMN_QUESTION;
import static com.example.myapplication.DBMatches.COLUMN_RIGHT_ANS;
import static com.example.myapplication.DBMatches.COLUMN_STATE;
import static com.example.myapplication.DBMatches.COLUMN_USER_ANS;
import static com.example.myapplication.DBMatches.DATABASE_NAME;
import static com.example.myapplication.DBMatches.DATABASE_VERSION;
import static com.example.myapplication.DBMatches.TABLE_RESULTS;
import static com.example.myapplication.DBMatches.TABLE_SESSION;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class DBMatches {
    public static final String DATABASE_NAME = "TestingSession.db";
    public static final int DATABASE_VERSION = 2;

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

    private SQLiteDatabase db;
    String formattedDate;
    String id;

    public DBMatches(Context context) {
        DBuse mOpenHelper = new DBuse(context);
        db = mOpenHelper.getWritableDatabase();
    }


    ///////МЕТОДЫ ДЛЯ TESTING ACTIVITY
    protected void upd_db(int count_right_ans, int count_wrong_ans) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_COUNT_RIGHT, count_right_ans);
        values.put(COLUMN_COUNT_WRONG, count_wrong_ans);
        String whereClause = COLUMN_ID2 + " = ?";
        String[] whereArgs = {id};
        db.update(TABLE_SESSION, values, whereClause, whereArgs);

    }

    public String getId() {
        return id;
    }

    @SuppressLint("Range")
    protected void get_Id() {
        Cursor cursor = db.rawQuery("SELECT _id FROM Session ORDER BY _id DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex("_id"));
        }
        cursor.close();
    }

    protected void dbConrol(int count_right_ans, int count_wrong_ans, String question, String Right_ans, String User_ans) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATE, count_right_ans - count_wrong_ans);
        values.put(COLUMN_ID_SESSION, id);
        values.put(COLUMN_QUESTION, question);
        values.put(COLUMN_RIGHT_ANS, Right_ans);
        values.put(COLUMN_USER_ANS, User_ans);

        db.insert("Results", null, values);
    }

    protected void getPrettyDate() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("Время - HH:mm:ss | Дата - dd MMMM yyyy'г.'");
        dateFormat.setDateFormatSymbols(new DateFormatSymbols() {{
            setMonths(new String[]{
                    "января", "февраля", "марта", "апреля",
                    "мая", "июня", "июля", "августа",
                    "сентября", "октября", "ноября", "декабря"});
        }});
        formattedDate = dateFormat.format(calendar.getTime());
    }

    protected void createDBsession() {
        ContentValues values = new ContentValues();
        getPrettyDate();
        values.put(COLUMN_DATE, formattedDate);
        db.insert("Session", null, values);
        get_Id();
    }

    public void deleteSession() {
        get_Id();
        db.execSQL("DELETE FROM Session WHERE count_right IS NULL AND count_wrong IS NULL;");
        db.execSQL("DELETE FROM Session WHERE _id = " + id + ";");
        db.execSQL("DELETE FROM Results WHERE id_session = " + id + ";");
    }


    /////МЕТОДЫ ДЛЯ RESULTTESTING
    protected void getDate() {
        DBuse dbHelper = new DBuse(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT date, count_wrong, count_right FROM Session ORDER BY _id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                String temp_data = cursor.getString(0);
                dates.add(temp_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }
}

public class DBuse extends SQLiteOpenHelper {

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
                    + ");";

    private static final String CREATE_TABLE_QUERY2 =
            "CREATE TABLE " + TABLE_SESSION + "("
                    + COLUMN_ID2 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_DATE + " TEXT,"
                    + COLUMN_COUNT_RIGHT + " INTEGER,"
                    + COLUMN_COUNT_WRONG + " INTEGER"
                    + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY2);
        db.execSQL(CREATE_TABLE_QUERY1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
        onCreate(db);
    }

    public void clearTable(SQLiteDatabase db, String tableName) {
        db.execSQL("DELETE FROM " + tableName);
    }


}