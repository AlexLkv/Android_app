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
import static com.example.myapplication.DBMatches.SELF_LEVEL;
import static com.example.myapplication.DBMatches.TABLE_RESULTS;
import static com.example.myapplication.DBMatches.TABLE_SESSION;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kotlin.Triple;

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
    public static final String SELF_LEVEL = "self_lvl";
    private final SQLiteDatabase db;
    String formattedDate;
    String id;

    public DBMatches(Context context) {
        DBuse mOpenHelper = new DBuse(context);
        db = mOpenHelper.getWritableDatabase();
    }


    ///////МЕТОДЫ ДЛЯ TESTING ACTIVITY
    protected void upd_db(int count_right_ans, int count_wrong_ans, String lvl) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_COUNT_RIGHT, count_right_ans);
        values.put(COLUMN_COUNT_WRONG, count_wrong_ans);
        values.put(SELF_LEVEL, lvl);
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
    public String get_lvl(String xid) {
        String lvl = null;
        Cursor cursor = db.rawQuery("SELECT self_lvl FROM Session WHERE _id = ?", new String[]{xid});
        if (cursor.moveToFirst()) {
            lvl = cursor.getString(0);
        }
        cursor.close();
        return lvl;
    }

    public Triple<LineGraphSeries<DataPoint>, PointsGraphSeries<DataPoint>, List<String>> getInfoForGraf(String xid) {
        int i = -1;
        String query = "SELECT * FROM Results WHERE id_session = '" + xid + "';";
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{});
        PointsGraphSeries<DataPoint> series2 = new PointsGraphSeries<>(new DataPoint[]{});
        List<String> result_questions = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                do {
                    i++;
                    /////ОТРИСОВКА ДЛЯ ГРАФИКА
                    int state = cursor.getInt(1);
                    series.appendData(new DataPoint(i, state), true, 40);
                    series2.appendData(new DataPoint(i, state), true, 40);

                    ////// ПОЛУЧЕНИЕ ДАННЫХ ПО ВОПРОСУ
                    String question = cursor.getString(3);
                    String RightAns = cursor.getString(4);
                    String UserAns = cursor.getString(5);
                    if (UserAns == null) UserAns = "Вопрос пропущен";
                    String temp = "Вопрос: " + question + "\n\nВаш ответ: " + UserAns + "\n\nВерный ответ: " + RightAns;
                    if (UserAns.equals(RightAns)) temp = "ВЕРНО!\n" + temp;
                    else temp = "НЕВЕРНО!\n" + temp;
                    result_questions.add(temp);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Triple<>(series, series2, result_questions);
    }

    protected String infoForTesting(String xid) {
        String text;
        Cursor cursor = db.rawQuery("SELECT count_right, count_wrong FROM Session WHERE _id = '" + xid + "'", null);
        String count_wrong_ans = null;
        String count_right_ans = null;
        if (cursor.moveToFirst()) {
            count_right_ans = cursor.getString(0);
            count_wrong_ans = cursor.getString(1);
        }
        if (count_right_ans == null) count_right_ans = String.valueOf(0);
        if (count_wrong_ans == null) count_wrong_ans = String.valueOf(0);
        text = "Кол-во вопросов: " + (Integer.parseInt(count_right_ans) + Integer.parseInt(count_wrong_ans)) +
                "\n" + "Кол-во верных ответов: " + count_right_ans +
                "\n" + "Кол-во ошибок: " + count_wrong_ans;
        cursor.close();
        return text;
    }

    @SuppressLint("Range")
    public String get_date_id(String ClickItem) {
        Cursor cursor = db.rawQuery("SELECT _id FROM Session WHERE date = '" + ClickItem + "' ORDER BY _id DESC LIMIT 1", null);
        id = null;
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex("_id"));
        }
        return id;
    }


    protected List<String> getDate() {   //// также применяктся для SHOWHISTORYMENU
        List<String> dates = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT date, count_wrong, count_right FROM Session ORDER BY _id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                String temp_data = cursor.getString(0);
                dates.add(temp_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dates;
    }
    /////МЕТОДЫ ДЛЯ MYRESULTS
    protected LineGraphSeries<DataPoint> res_graf() {
        int i = -1;
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{});
        try (Cursor cursor = db.rawQuery("SELECT count_right FROM Session", null)) {
            if (cursor.moveToFirst()) {
                do {
                    i++;
                    /////ОТРИСОВКА ДЛЯ ГРАФИКА
                    int state = cursor.getInt(0);
                    series.appendData(new DataPoint(i, state), true, 40);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return series;
    }
    protected String best_res() {
        String text;
        Cursor cursor = db.rawQuery("SELECT count_right, count_wrong, self_lvl FROM Session WHERE count_right = (SELECT MAX(count_right) FROM Session);", null);
        String count_wrong_ans = null;
        String count_right_ans = null;
        String lvl = null;
        if (cursor.moveToFirst()) {
            count_right_ans = cursor.getString(0);
            count_wrong_ans = cursor.getString(1);
            lvl = cursor.getString(2);

        }
        if (count_right_ans == null) count_right_ans = String.valueOf(0);
        if (count_wrong_ans == null) count_wrong_ans = String.valueOf(0);
        if (lvl == null) lvl = "-";
        text = "Ваш уровень владения английским языком: " + lvl +
                "\n\nКол-во вопросов: " + (Integer.parseInt(count_right_ans) + Integer.parseInt(count_wrong_ans)) +
                "\n" + "Кол-во верных ответов: " + count_right_ans +
                "\n" + "Кол-во ошибок: " + count_wrong_ans;
        cursor.close();
        return text;
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
                    + COLUMN_COUNT_WRONG + " INTEGER,"
                    + SELF_LEVEL + " TEXT"
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