package com.example.liner236.sensortracker;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.Vector;


public class GpsErrEvaluation extends AppCompatActivity implements LocationListener {

    private LocationManager locationmanagerGPS;
    private Location locationGPS = null;
    private static final long MIN_TIME_TO_REFRESH = 500L;
    private static final float MIN_DISTANCE_TO_REFRESH = 10F;

    private double latitudeGPS;
    private double longitudeGPS;
    private float metersGPS;

    Vector<Float> vec_meters;
    private int tracking_counter;
    double[] err_percent_array;

    GraphView error_graph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_err_evaluation);

        locationmanagerGPS = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        vec_meters = new Vector<Float>(50,20);
        err_percent_array = new double[100];


        Button btn_start = (Button)findViewById(R.id.btn_start_two);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locateViaGps();
            }
        });

        Button btn_auswertung = (Button)findViewById(R.id.btn_auswertung);
        btn_auswertung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fehlerAuswertung();
                draw();
            }
        });

        error_graph = (GraphView)findViewById(R.id.graph_error);
        error_graph.setTitle("Accelerometer");
        error_graph.setTitleColor(Color.BLACK);
        error_graph.getGridLabelRenderer().setGridColor(Color.BLACK);
        //.getGridLabelRenderer().setVerticalAxisTitle("Y");
        //.getGridLabelRenderer().setHorizontalAxisTitle("Seconds");
        error_graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);
        error_graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLACK);
        error_graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        error_graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        error_graph.getViewport().setXAxisBoundsManual(true);
        error_graph.getViewport().setYAxisBoundsManual(true);
        error_graph.getViewport().setMaxX(110);
        error_graph.getViewport().setMaxY(100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationmanagerGPS.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        locateViaGps();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "GPS is ON", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "GPS is OFF", Toast.LENGTH_LONG).show();
    }

    private void locateViaGps() {


        locationmanagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_TO_REFRESH,MIN_DISTANCE_TO_REFRESH, this);
        locationGPS = locationmanagerGPS.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(locationGPS != null){
            latitudeGPS = locationGPS.getLatitude();
            longitudeGPS = locationGPS.getLongitude();

            metersGPS = locationGPS.getAccuracy();
            vec_meters.add(locationGPS.getAccuracy());
            tracking_counter +=1;

            System.out.println("Meters: " + metersGPS);
            Toast.makeText(this, "GPS tracked", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "location = NULL", Toast.LENGTH_LONG).show();
        }


    }

    private void fehlerAuswertung(){
        int count;
        int saver;

        // Meter-Scheife
        for (int i=0;i<100;i++){
            count = 0;
            // Meter-Mengen-Schleife
            for (int j=0;j<vec_meters.size();j++){
                if (vec_meters.elementAt(j) == i){
                    count+=1;
                }
            }
            //Umrechnung in Prozent
            saver = count / tracking_counter; // Unsinnigster Scheiß oder bestes Beispiel für Eliminierung !?
            err_percent_array[i] = saver * 100;
            System.out.println("i: " + i +"\tCount: " + count + "\tTracking_Counter: " + tracking_counter + "\t Err.Percent: " + err_percent_array[i]);
        }
    }

    private void draw(){

        DataPoint[] pointsY = new DataPoint[err_percent_array.length];

        for (int i=0;i<pointsY.length;i++){
            pointsY[i] = new DataPoint(i,err_percent_array[i]);
        }

        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(pointsY);
        error_graph.addSeries(series);


    }
}
