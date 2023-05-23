package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class ResultTesting extends AppCompatActivity {
    PointsGraphSeries<DataPoint> series2 = new PointsGraphSeries<DataPoint>(new DataPoint[]{});
    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{});
    private final List<String> result_questions = new ArrayList<>();
    //    private final List<String> right_ans = new ArrayList<>();
//    private final List<String> user_ans = new ArrayList<>();
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_testing);
        fillTextinfo();
        getInfoForGraf();
        CreateGraf();
        fill_users_ans();
    }

    @SuppressLint("Range")
    public void getInfoForGraf() {
        DBuse dbHelper = new DBuse(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT _id FROM Session ORDER BY _id DESC LIMIT 1", null);
//        String id = null;
//        if (cursor.moveToFirst()) {
//            id = cursor.getString(cursor.getColumnIndex("_id"));
//        }
        Cursor cursor = db.rawQuery("SELECT * FROM Results WHERE id_session = '" + id + "';", null);
        int i = -1;
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

        Bundle arguments = getIntent().getExtras();
        id = arguments.getString("id");
        DBuse dbHelper = new DBuse(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count_right, count_wrong FROM Session WHERE _id = '" + id + "'", null);
        String count_wrong_ans= null;
        String count_right_ans = null;
        if (cursor.moveToFirst()) {
            count_right_ans = cursor.getString(0);
            count_wrong_ans = cursor.getString(1);
        }
        text.setText("Поздравляем, Вы прошли тестирование! \n \n" +
                "Количество вопросов: " + (Integer.parseInt(count_right_ans) + Integer.parseInt(count_wrong_ans)) +
                "\n" + "Количество верных ответов: " + count_right_ans +
                "\n" + "Количество ошибок: " + count_wrong_ans);
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
}
