package com.example.liner236.sensortracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    // GPS Stuff
    private LocationManager locationManager;
    private Location location;
    private static final long MIN_TIME_TO_REFRESH = 40000L;
    private static final float MIN_DISTANCE_TO_REFRESH = 10F;
    private double latitude;
    private double longitude;
    Vector<DataVectorGps> latVec;
    double[] latArray;
    double[] lonArray;




    //------------------------------------------------------------

    // Accel Stuff
    private SensorManager sensorManager = null;
    private Sensor accelSensor;
    double x,y,z;

    //Database Stuff



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        locateViaGps();

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelSensor,SensorManager.SENSOR_DELAY_NORMAL);

        // Evtl für besser Datenübertragung
        latVec = new Vector<DataVectorGps>(20,10);

        Button btn_map = (Button)findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vecInArray(latVec);
                changeAktivity(v);
            }
        });





    }

    private void locateViaGps(){

        System.out.println("!!!!!!!!!WAS GEHT AB!!!!!!!!!!!!!!!");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_TO_REFRESH, MIN_DISTANCE_TO_REFRESH, this);
        this.location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());

        ((TextView) findViewById(R.id.tv_gps_cordinates)).setText("Latitude: " + getLatitude() + "\n" + "Longitude: " + getLongitude());

    }

    public void changeAktivity(View view){
        Intent i = new Intent(this,MapsActivity.class);
        Bundle b = new Bundle();

        b.putDoubleArray("latitude",latArray);
        b.putDoubleArray("longitude",lonArray);

        i.putExtras(b);

        startActivity(i);
    }

    public void vecInArray(Vector<DataVectorGps> vec){
        this.latArray = new double[vec.size()];
        this.lonArray = new double[vec.size()];

        for (int i = 0;i < vec.size();i++){
            this.latArray[i] = vec.get(i).getLatitude();
            this.lonArray[i] = vec.get(i).getLongitude();
            System.out.println("Schleife:::::::::::: " + i);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        ((TextView) findViewById(R.id.tv_accel)).setText("X: " + event.values[0] + "\n" + "Y: " + event.values[1] + "\n" + "X: " + event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {

        locateViaGps();
        latVec.add(new DataVectorGps(getLatitude(),getLongitude()));
        Toast.makeText(this, "GPS collected", Toast.LENGTH_LONG).show();
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




    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
