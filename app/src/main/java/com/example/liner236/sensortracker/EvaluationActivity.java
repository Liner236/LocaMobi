package com.example.liner236.sensortracker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class EvaluationActivity extends AppCompatActivity {

    private double[] light_value_arr;
    private long[] time_arr;
    private double[] pressure_value_arr;
    private double[] xArray;
    private double[] yArray;
    private double[] zArray;

    GraphView accel_graph;
    GraphView light_graph;
    GraphView pressure_graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);



        Bundle extras = getIntent().getExtras();
        if (extras != null){
            light_value_arr = extras.getDoubleArray("lightValue");
            time_arr = extras.getLongArray("time");
            pressure_value_arr = extras.getDoubleArray("pressureValue");
            xArray = extras.getDoubleArray("x");
            yArray = extras.getDoubleArray("y");
            zArray = extras.getDoubleArray("z");
        }

        accel_graph = (GraphView)findViewById(R.id.graph_accel);
        light_graph = (GraphView)findViewById(R.id.graph_light);
        pressure_graph = (GraphView)findViewById(R.id.graph_pressure);

        if (xArray != null || yArray != null || zArray != null)
            accelGraph();

        if (light_value_arr != null)
            lightGraph();

        if (pressure_value_arr != null)
            pressureGraph();


    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    public void accelGraph(){

        accel_graph.setTitle("Accelerometer");
        accel_graph.setTitleColor(Color.WHITE);
        accel_graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        //accel_graph.getGridLabelRenderer().setVerticalAxisTitle("Y");
        //accel_graph.getGridLabelRenderer().setHorizontalAxisTitle("Seconds");
        accel_graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.WHITE);
        accel_graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.WHITE);
        accel_graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        accel_graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);

        DataPoint[] pointsX = new DataPoint[xArray.length];
        DataPoint[] pointsY = new DataPoint[yArray.length];
        DataPoint[] pointsZ = new DataPoint[zArray.length];

        for (int i=0;i<pointsX.length;i++){
            pointsX[i] = new DataPoint(time_arr[i],(int)xArray[i]);
        }

        for (int i=0;i<pointsY.length;i++){
            pointsY[i] = new DataPoint(time_arr[i],(int)yArray[i]);
        }

        for (int i=0;i<pointsZ.length;i++){
            pointsZ[i] = new DataPoint(time_arr[i],(int)zArray[i]);
        }

        LineGraphSeries<DataPoint> seriesX = new LineGraphSeries<>(pointsX);
        LineGraphSeries<DataPoint> seriesY = new LineGraphSeries<>(pointsY);
        LineGraphSeries<DataPoint> seriesZ = new LineGraphSeries<>(pointsZ);
        seriesX.setColor(Color.RED);
        seriesY.setColor(Color.BLUE);
        seriesZ.setColor(Color.GREEN);

       /* accel_graph.getViewport().setScrollable(true);
        accel_graph.getViewport().setScalable(true);
        accel_graph.getViewport().setScrollableY(true);
        accel_graph.getViewport().setScalableY(true);*/

        accel_graph.addSeries(seriesX);
        accel_graph.addSeries(seriesY);
        accel_graph.addSeries(seriesZ);


    }



    public void lightGraph(){

        light_graph.setTitle("Light Sensor");
        light_graph.setTitleColor(Color.WHITE);
        light_graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        //.getGridLabelRenderer().setVerticalAxisTitle("Y");
        //.getGridLabelRenderer().setHorizontalAxisTitle("Seconds");
        light_graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.WHITE);
        light_graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.WHITE);
        light_graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        light_graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);

        DataPoint[] points = new DataPoint[light_value_arr.length];



        for (int i=0;i<points.length;i++){
            points[i] = new DataPoint(time_arr[i],(int)light_value_arr[i]);
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

        /*light_graph.getViewport().setScrollable(true);
        light_graph.getViewport().setScalable(true);
        light_graph.getViewport().setScrollableY(true);
        light_graph.getViewport().setScalableY(true);*/

        series.setColor(Color.YELLOW);
        light_graph.addSeries(series);
    }

    public  void pressureGraph(){

        pressure_graph.setTitle("Pressure Sensor");
        pressure_graph.setTitleColor(Color.WHITE);
        pressure_graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        //.getGridLabelRenderer().setVerticalAxisTitle("Y");
        //.getGridLabelRenderer().setHorizontalAxisTitle("Seconds");
        pressure_graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.WHITE);
        pressure_graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.WHITE);
        pressure_graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        pressure_graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);

        DataPoint[] points = new DataPoint[pressure_value_arr.length];



        for (int i=0;i<points.length;i++){
            points[i] = new DataPoint(time_arr[i],(int)pressure_value_arr[i]);
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

       /* pressure_graph.getViewport().setScrollable(true);
        pressure_graph.getViewport().setScalable(true);
        pressure_graph.getViewport().setScrollableY(true);
        pressure_graph.getViewport().setScalableY(true);*/

        pressure_graph.addSeries(series);
    }


}
