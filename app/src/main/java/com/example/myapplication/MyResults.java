package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MyResults extends AppCompatActivity {
    DBMatches mDBConnector;

    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_results);
        graph = findViewById(R.id.graph2);
        mDBConnector = new DBMatches(this);
        TextView results = findViewById(R.id.results);
        results.setText("Лучший результат:\n\n"+mDBConnector.best_res());

        CreateGraf();
    }
    public void CreateGraf() {

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-15);
        graph.getViewport().setMaxY(60);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(10);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);


        LineGraphSeries<DataPoint> series = mDBConnector.res_graf();
        graph.addSeries(series);
    }

        public void ext(View view) {
        Intent intent = new Intent(MyResults.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}