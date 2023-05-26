package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ShowHistoryMenu extends AppCompatActivity {

    private final List<String> dates = new ArrayList<>();
    ListView listView;
    String ClickItem;
    DBuse dbHelper = new DBuse(this);
    String id;
    protected void InitUI(){
        listView = findViewById(R.id.list_view);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);
        InitUI();
        getDate();
        setData();
        setListeners();
    }
    protected void getDate(){
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
    private void setListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                ClickItem = String.valueOf(((TextView) itemClicked).getText());
                load_result();
            }
        });
    }
    @SuppressLint("Range")
    protected void load_result(){
        DBuse dbHelper = new DBuse(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id FROM Session WHERE date = '" + ClickItem + "' ORDER BY _id DESC LIMIT 1", null);
        id = null;
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex("_id"));
        }
        if (!dates.isEmpty()) {
            Intent intent = new Intent(ShowHistoryMenu.this, ResultTesting.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }
    protected void setData(){
        if (dates.isEmpty()){
            dates.add("--------- Увы, тут пока пусто (\"404 not found\") ---------");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, dates);
        listView.setAdapter(adapter);
    }

    public void back(View view) {
        Intent intent = new Intent(ShowHistoryMenu.this, MainActivity.class);
        startActivity(intent);
    }
}