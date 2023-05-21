package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

public class ResultTesting extends AppCompatActivity {
    PointsGraphSeries<DataPoint> series2 = new PointsGraphSeries<DataPoint>(new DataPoint[]{});
    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{});

    @SuppressLint("Range")
    public void getInfoForGraf() {
        DBuse dbHelper = new DBuse(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id FROM Session ORDER BY _id DESC LIMIT 1", null);
        String id = null;
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex("_id"));
        }
        cursor = db.rawQuery("SELECT * FROM Results WHERE id_session = '" + id + "';", null);
        int i = -1;
        if (cursor.moveToFirst()) {
            do {
                i++;

                int state = cursor.getInt(1);

                series.appendData(new DataPoint(i, state), true, 40);
                series2.appendData(new DataPoint(i, state), true, 40);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
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

    @SuppressLint("SetTextI18n")
    public void fillTextinfo() {
        TextView text = findViewById(R.id.result_test);

        Bundle arguments = getIntent().getExtras();

        int index = arguments.getInt("index");
        int count_right_ans = arguments.getInt("count_right_ans");
        int count_wrong_ans = arguments.getInt("count_wrong_ans");
        text.setText("Поздравляем, Вы прошли тестирование! \n \n" +
                "Количество вопросов: " + (index + 1) +
                "\n" + "Количество верных ответов: " + count_right_ans +
                "\n" + "Количество ошибок: " + count_wrong_ans);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_testing);
        getInfoForGraf();
        CreateGraf();
        fillTextinfo();
    }

    public void ext(View view) {
        Intent intent = new Intent(ResultTesting.this, MainActivity.class);
        startActivity(intent);
    }
}
