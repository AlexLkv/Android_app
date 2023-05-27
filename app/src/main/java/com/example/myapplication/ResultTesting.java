package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.Triple;

public class ResultTesting extends AppCompatActivity {
    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{});
    PointsGraphSeries<DataPoint> series2 = new PointsGraphSeries<DataPoint>(new DataPoint[]{});
    List<String> result_questions = new ArrayList<>();
    String id;
    DBMatches mDBConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_testing);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Bundle arguments = getIntent().getExtras();
        id = arguments.getString("id");
        mDBConnector = new DBMatches(this);
        fillTextinfo();
        getInfoGrafSQL();
        CreateGraf();
        fill_users_ans();
    }

    protected void getInfoGrafSQL() {
        Triple<LineGraphSeries<DataPoint>, PointsGraphSeries<DataPoint>, List<String>> result = mDBConnector.getInfoForGraf(id);
        series = result.getFirst();
        series2 = result.getSecond();
        result_questions = result.getThird();
    }

    public void CreateGraf() {
        GraphView graph = findViewById(R.id.graph);

        series2.setSize(7);
        series2.setColor(Color.parseColor("#dc143c"));

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-15);
        graph.getViewport().setMaxY(20);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(50);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.addSeries(series);
        graph.addSeries(series2);


    }

    public void fill_users_ans() {
        ListView listView = findViewById(R.id.list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, result_questions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                String itemText = result_questions.get(position);
                if (itemText.charAt(0) == 'В') {
                    tv.setTextColor(Color.GREEN);
                } else tv.setTextColor(Color.RED);
                return view;
            }
        };
        listView.setAdapter(arrayAdapter);
    }

    @SuppressLint("SetTextI18n")
    public void fillTextinfo() {
        TextView text = findViewById(R.id.result_test);
        TextView tv_lvl = findViewById(R.id.result_lvl);
        String txt = mDBConnector.infoForTesting(id);
        String lvl = mDBConnector.get_lvl(id);
        tv_lvl.setText("Ваш уровень: " + lvl);
        text.setText(txt);
    }

    public void ext(View view) {
        Intent intent = new Intent(ResultTesting.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Чтобы вернуться назад, используйте кнопку", Toast.LENGTH_SHORT).show();
    }

    public void history(View view) {
        Intent intent = new Intent(ResultTesting.this, ShowHistoryMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}