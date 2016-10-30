package com.example.liner236.sensortracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.BuddhistCalendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Vector;

public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    //General Stuff
    private boolean start = false;
    private long[] time_array;
    Vector<Long> time_vec;

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
    private double x,y,z;
    Vector<Double>[] accel_vec;


    //Light Sensor Stuff
    private SensorManager sensorManager_light = null;
    private Sensor light_sensor;
    private double light_value;
    Vector<Double> light_vec;
    double[] light_value_array;


    //Pressure Stuff
    private SensorManager sensorManager_pressure = null;
    private  Sensor pressure_sensor;


    //Database Stuff



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        sensorManager_light = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        sensorManager_pressure = (SensorManager) this.getSystemService(SENSOR_SERVICE);


        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelSensor,SensorManager.SENSOR_DELAY_NORMAL);

        light_sensor = sensorManager_light.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager_light.registerListener(this,light_sensor,SensorManager.SENSOR_DELAY_NORMAL);

        pressure_sensor = sensorManager_pressure.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager_pressure.registerListener(this,pressure_sensor,SensorManager.SENSOR_DELAY_NORMAL);

        Button btn_start = (Button)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = true;
                locateViaGps();
            }
        });

        Button btn_stop = (Button)findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = false;

            }
        });

        Button btn_beenden = (Button)findViewById(R.id.btn_beenden);
        btn_beenden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


        // Evtl für besser Datenübertragung-----------------------
        latVec = new Vector<DataVectorGps>(20,10);

        Button btn_map = (Button)findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vecInArray(latVec);
                changeAktivity(v);
            }
        });
        //---------------------------------------------------------

        light_vec = new Vector<Double>(20,10);
        time_vec = new Vector<Long>(20,10);

        Button btn_sensor_eva = (Button)findViewById(R.id.btn_sensor_eva);
        btn_sensor_eva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                light_value_array = vecInDoubleArray(light_vec);
                time_array = vecInLongArray(time_vec);
                changeToEvaluation(v);
            }
        });

        ToggleButton toggle_trackData = (ToggleButton) findViewById(R.id.tbtn_trackData);
        toggle_trackData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    trackSensorData();
                } else {
                    // The toggle is disabled
                }
            }
        });






    }




    private void accelValueChange(double x,double y,double z){
        if (x <= 0){
            setX(x * (-1));
        }
        else{
            setX(x + 20);
        }

        if (y <= 0){
            setY(y * (-1));
        }
        else{
            setY(y + 20);
        }

        if (z <= 0){
            setZ(z * (-1));
        }
        else{
            setZ(z + 20);
        }
    }

    private void locateViaGps(){


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_TO_REFRESH, MIN_DISTANCE_TO_REFRESH, this);
        this.location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location != null){
            setLatitude(location.getLatitude());
            setLongitude(location.getLongitude());

            ((TextView) findViewById(R.id.tv_gps_cordinates)).setText("Latitude: " + getLatitude() + "\n" + "Longitude: " + getLongitude());
        }
        else{
            Toast.makeText(this, "location = NULL", Toast.LENGTH_LONG).show();
        }


    }

    public void changeAktivity(View view){
        Intent i = new Intent(this,MapsActivity.class);
        Bundle b = new Bundle();

        b.putDoubleArray("latitude",latArray);
        b.putDoubleArray("longitude",lonArray);

        i.putExtras(b);

        startActivity(i);
    }

    public void changeToEvaluation(View view){
        Intent i = new Intent(this,EvaluationActivity.class);
        Bundle b = new Bundle();

        b.putDoubleArray("lightValue",light_value_array);
        b.putLongArray("time",time_array);

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

    public double[] vecInDoubleArray(Vector<Double> vec){
        double[] array = new double[vec.size()];

        for (int i = 0;i < vec.size();i++){
            array[i] = vec.get(i);
        }

        return array;
    }

    public long[] vecInLongArray(Vector<Long> vec){
        long[] array = new long[vec.size()];

        for (int i = 0;i < vec.size();i++){
            array[i] = vec.get(i);
        }

        return array;
    }


    public void trackSensorData() {
        new Thread(new Runnable() {
            public void run() {

            time_vec.add(System.currentTimeMillis() / 1000);
            light_vec.add(getLight_value());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Halloooooo");

            }
        }).start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (start){
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                setX(event.values[0]);
                setY(event.values[1]);
                setZ(event.values[2]);
                ((TextView) findViewById(R.id.tv_accel)).setText("X: " + getX() + "\n" + "Y: " + getY() + "\n" + "Z: " + getZ());
            }

            if (event.sensor.getType() == Sensor.TYPE_LIGHT){
                ((TextView) findViewById(R.id.tv_light)).setText(String.valueOf("Illuminance: " + event.values[0]) + " lx");
            }

            if (event.sensor.getType() == Sensor.TYPE_PRESSURE){
                setLight_value(event.values[0]);
                ((TextView) findViewById(R.id.tv_pressure)).setText(String.valueOf("Pressure: " + getLight_value()) + " hPa");
            }


        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        sensorManager_light.unregisterListener(this);
        sensorManager_pressure.unregisterListener(this);

        locationManager.removeUpdates(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (start){
            locateViaGps();
            latVec.add(new DataVectorGps(getLatitude(),getLongitude()));
            Toast.makeText(this, "GPS collected", Toast.LENGTH_LONG).show();
        }
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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getLight_value() {
        return light_value;
    }

    public void setLight_value(double light_value) {
        this.light_value = light_value;
    }
}
