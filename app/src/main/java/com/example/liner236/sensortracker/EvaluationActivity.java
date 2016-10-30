package com.example.liner236.sensortracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class EvaluationActivity extends AppCompatActivity {

    private double[] light_value_arr;
    private long[] time_arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);



        Bundle extras = getIntent().getExtras();
        if (extras != null){
            light_value_arr = extras.getDoubleArray("lightValue");
            time_arr = extras.getLongArray("time");

        }

        lightGraph();


    }

    public void lightGraph(){
        GraphView light_graph = (GraphView)findViewById(R.id.graph_light);
        DataPoint[] points = new DataPoint[light_value_arr.length];

        for (int i=0;i<points.length;i++){
            points[i] = new DataPoint(time_arr[i],(int)light_value_arr[i]);
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

        light_graph.getViewport().setScrollable(true);
        light_graph.getViewport().setScalable(true);
        light_graph.getViewport().setScrollableY(true);
        light_graph.getViewport().setScalableY(true);
        light_graph.addSeries(series);
    }
}
