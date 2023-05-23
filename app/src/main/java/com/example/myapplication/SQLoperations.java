package com.example.myapplication;

import static com.example.myapplication.DBuse.COLUMN_COUNT_RIGHT;
import static com.example.myapplication.DBuse.COLUMN_COUNT_WRONG;
import static com.example.myapplication.DBuse.COLUMN_ID2;
import static com.example.myapplication.DBuse.TABLE_SESSION;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class SQLoperations {
    DBuse dbHelper = new DBuse(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    protected void upd_db(){
        ContentValues values = new ContentValues();
        values.put(COLUMN_COUNT_RIGHT, count_right_ans);
        values.put(COLUMN_COUNT_WRONG, count_wrong_ans);
        String whereClause = COLUMN_ID2 + " = ?";
        String[] whereArgs = {id};
        db.update(TABLE_SESSION, values, whereClause, whereArgs);
        db.close();
    }
}
