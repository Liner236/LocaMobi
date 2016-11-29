package com.example.liner236.sensortracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Vector;

import static android.R.attr.x;
import static android.R.attr.y;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class ArthursEvaluation extends AppCompatActivity implements LocationListener, SensorEventListener, View.OnClickListener{

    //Normal GPS Stuff
    private LocationManager locationManager;
    private Location location;
    private static final long MIN_TIME_TO_REFRESH = 2000L;
    private static final float MIN_DISTANCE_TO_REFRESH = 10F;
    private double latitude;
    private double longitude;
    int i=0;
    int zähler=0;

    //Vektoren für die Werte
    Vector lokalgpsLong = new Vector();
    Vector lokalgpsLati = new Vector();

    Vector googlemapsLong = new Vector();
    Vector googlemapsLati = new Vector();

    //Graph lokal
    GraphView lokal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // OnClick für Buttons
        Button btn_normalesgps = (Button) findViewById(R.id.btn_lokal_gps);
        btn_normalesgps.setOnClickListener(this);

        Button btn_savepunkt = (Button) findViewById(R.id.btn_speicherpunkt);
        btn_savepunkt.setOnClickListener(this);

        Button btn_ausgabepunkte = (Button)findViewById(R.id.btn_ausgabepunkte);
        btn_ausgabepunkte.setOnClickListener(this);

        Button btn_eva = (Button)findViewById(R.id.btn_eva);
        btn_eva.setOnClickListener(this);

        // Lokal GPS LocationManager
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        //Statische Werte von Googlemaps bis BO HBF
        googlemapsLati.add(0,51.508426);     googlemapsLong.add(0,7.204687);
        googlemapsLati.add(1,51.508571);     googlemapsLong.add(1,7.211457);
        googlemapsLati.add(2,51.502377);     googlemapsLong.add(2,7.212470);
        googlemapsLati.add(3,51.493975);     googlemapsLong.add(3,7.212948);
        googlemapsLati.add(4,51.488419);     googlemapsLong.add(4,7.214022);
        googlemapsLati.add(5,51.482847);     googlemapsLong.add(5,7.215731);
        googlemapsLati.add(6,51.479120);     googlemapsLong.add(6,7.222418);

        //Graph Einstellungen

        lokal=(GraphView)findViewById(R.id.lokal);
        lokal.setTitle("GPS Evaluation");
        lokal.getGridLabelRenderer().setVerticalAxisTitle("Latitude");
        lokal.getGridLabelRenderer().setHorizontalAxisTitle("Longitude");

    }

    public void normalgps(){

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_TO_REFRESH, MIN_DISTANCE_TO_REFRESH, this);
        this.location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location != null) {

            setLatitude(location.getLatitude());
            setLongitude(location.getLongitude());

            ((TextView) findViewById(R.id.txt_lati)).setText("Latitude: " + getLatitude());
            ((TextView) findViewById(R.id.txt_long)).setText("Longitude: " + getLongitude());
        }
    }

    public void eva(){

        // Google Maps Graph
        DataPoint[] punkte = new DataPoint[googlemapsLati.size()];

        String hilf1, hilf2;
        double x, y;

        for (int i=0;i<punkte.length;i++){

            hilf1 = String.valueOf(googlemapsLati.get(i));
            y = Double.parseDouble(hilf1);

            hilf2 = String.valueOf(googlemapsLong.get(i));
            x = Double.parseDouble(hilf2);

            punkte[i] = new DataPoint(x,y);
        }

        //Lokal GPS Graph

        DataPoint[] punkte2 = new DataPoint[zähler];

        String hilf3, hilf4;
        double x2, y2;

        for (int i=0;i<punkte2.length;i++){

            hilf3 = String.valueOf(lokalgpsLati.get(i));
            y2 = Double.parseDouble(hilf3);

            hilf4 = String.valueOf(lokalgpsLong.get(i));
            x2 = Double.parseDouble(hilf4);

            punkte2[i] = new DataPoint(x2,y2);
        }

        LineGraphSeries<DataPoint> points = new LineGraphSeries<>(punkte);
        LineGraphSeries<DataPoint> points2 = new LineGraphSeries<>(punkte2);

        points.setColor(Color.RED);
        points2.setColor(Color.GREEN);

        lokal.addSeries(points);
        lokal.addSeries(points2);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case  R.id.btn_lokal_gps:
                normalgps();
                break;

            case R.id.btn_speicherpunkt:
                lokalgpsLati.add(i, getLatitude());
                lokalgpsLong.add(i, getLongitude());
                ((TextView) findViewById(R.id.txt_gespeichert)).setText("Punkt " + (i+1) +" save");
                i++;
                zähler++;
                break;

            case R.id.btn_ausgabepunkte:

                int l = lokalgpsLati.size();

                for(int z=0; z < l; z++){

                    System.out.println(z+" Long: "+lokalgpsLong.get(z));
                    System.out.println(z+" Lati: "+lokalgpsLati.get(z));
                    System.out.println(" ");
                }
                break;

            case R.id.btn_eva:
                eva();
                break;

            default:
                break;
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

}
