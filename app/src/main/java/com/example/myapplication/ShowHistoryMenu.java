package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ShowHistoryMenu extends AppCompatActivity {

    private final List<String> dates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);
        getDate();
        setData();

    }
    protected void getDate(){
        DBuse dbHelper = new DBuse(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT date FROM Session ORDER BY _id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                String temp_data = cursor.getString(0);
                dates.add(temp_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }
    protected void setData(){
        ListView listView = findViewById(R.id.list_view);
        if (dates.isEmpty()){
            dates.add("--------- Увы, тут пока пусто (\"404 not found\") ---------");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, dates);
        listView.setAdapter(adapter);
    }
}