package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
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

    private List<String> dates = new ArrayList<>();
    ListView listView;
    String ClickItem;
    String id;
    DBMatches mDBConnector;
    boolean cliked = true;
    protected void InitUI(){
        listView = findViewById(R.id.list_view);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);
        mDBConnector = new DBMatches(this);
        InitUI();
        dates = mDBConnector.getDate();
        setData();
        setListeners();

    }

    private void setListeners() {
        if (cliked) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                        long id) {
                    ClickItem = String.valueOf(((TextView) itemClicked).getText());
                    load_result(ClickItem);
                }
            });
        }
    }
    @SuppressLint("Range")
    protected void load_result(String ClickItem){
        id = mDBConnector.get_date_id(ClickItem);
        if (!dates.isEmpty()) {
            cliked = true;
            Intent intent = new Intent(ShowHistoryMenu.this, ResultTesting.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }
    protected void setData(){
        if (dates.isEmpty()){
            cliked = false;
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