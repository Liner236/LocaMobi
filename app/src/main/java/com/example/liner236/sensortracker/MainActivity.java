package com.example.liner236.sensortracker;


import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.TextView;
import android.widget.Toast;


import java.util.Vector;

public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    //General Stuff
    private boolean start = false;
    private boolean stopT = false;
    private boolean track_accel = false;
    private boolean track_light = false;
    private  boolean track_pressure = false;


    //Time
    private long[] time_array;
    Vector<Long> time_vec;
    private long timeSec = 0;



    //Threads
    Runnable runnable;
    Thread thread_TrackData;



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
    double[] xArray;
    double[] yArray;
    double[] zArray;


    //Light Sensor Stuff
    private SensorManager sensorManager_light = null;
    private Sensor light_sensor;
    private double light_value;
    Vector<Double> light_vec;
    double[] light_value_array;


    //Pressure Stuff
    private SensorManager sensorManager_pressure = null;
    private  Sensor pressure_sensor;
    private double pressure_value;
    Vector<Double> pressure_vec;
    double[] pressure_value_array;


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
        final CheckBox cb_trackData = (CheckBox)findViewById(R.id.cb_trackData);
        final CheckBox cb_gps = (CheckBox)findViewById(R.id.cb_gps);
        final CheckBox cb_accel = (CheckBox)findViewById(R.id.cb_accel);
        final CheckBox cb_light = (CheckBox)findViewById(R.id.cb_light);
        final CheckBox cb_pressure = (CheckBox)findViewById(R.id.cb_pressure);


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!start){
                    start = true;
                    locateViaGps();

                    if (cb_trackData.isChecked()){
                        trackSensorData();
                    }

                    if (cb_accel.isChecked()){
                        track_accel = true;
                    }

                    if (cb_light.isChecked()){
                        track_light = true;
                    }

                    if (cb_pressure.isChecked()){
                        track_pressure = true;
                    }

                    cb_trackData.setEnabled(false);
                    cb_accel.setEnabled(false);
                    cb_light.setEnabled(false);
                    cb_pressure.setEnabled(false);
                }

            }
        });

        Button btn_stop = (Button)findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start){
                    start = false;
                    cb_trackData.setEnabled(true);
                    cb_light.setEnabled(true);
                    cb_accel.setEnabled(true);
                    cb_pressure.setEnabled(true);

                    light_vec.clear();
                    accel_vec[0].clear();
                    accel_vec[1].clear();
                    accel_vec[2].clear();
                    pressure_vec.clear();
                }
            }
        });

        Button btn_beenden = (Button)findViewById(R.id.btn_beenden);
        btn_beenden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        Button btn_lightsaber = (Button)findViewById(R.id.btn_lightsaber);
        btn_lightsaber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    changeToLightsaber(v);
            }
        });


        // Evtl für besser Datenübertragung-----------------------
        latVec = new Vector<DataVectorGps>(20,10);

        Button btn_map = (Button)findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vecInArray(latVec);
                if(latArray != null || lonArray != null){
                    changeAktivity(v);
                }
                else {
                    Toast.makeText(new AppCompatActivity(), "Keine Daten verfügbar", Toast.LENGTH_LONG).show();
                }

            }
        });
        //---------------------------------------------------------

        // Track Time and Data----------------------------------------------------------------
        light_vec = new Vector<Double>(20,10);
        time_vec = new Vector<Long>(20,10);
        pressure_vec = new Vector<Double>(20,10);
        accel_vec = (Vector<Double>[]) new Vector[3];
        accel_vec[0] = new Vector<Double>(20,10);
        accel_vec[1] = new Vector<Double>(20,10);
        accel_vec[2] = new Vector<Double>(20,10);

        Button btn_sensor_eva = (Button)findViewById(R.id.btn_sensor_eva);
        btn_sensor_eva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_array = vecInLongArray(time_vec);
                light_value_array = vecInDoubleArray(light_vec);
                pressure_value_array = vecInDoubleArray(pressure_vec);
                xArray = vecInDoubleArray(accel_vec[0]);
                yArray = vecInDoubleArray(accel_vec[1]);
                zArray = vecInDoubleArray(accel_vec[2]);

                changeToEvaluation(v);
            }
        });

        //----------------------------------------------------------------------------------






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

    public void changeToLightsaber(View view){
        Intent i = new Intent(this,LightsaberActivity.class);
        startActivity(i);
    }

    public void changeToGpsErrorEva(View view){
        Intent i = new Intent(this,.class);
        startActivity(i);
    }

    public void changeToEvaluation(View view){
        Intent i = new Intent(this,EvaluationActivity.class);
        Bundle b = new Bundle();

        b.putDoubleArray("lightValue",light_value_array);
        b.putLongArray("time",time_array);
        b.putDoubleArray("pressureValue",pressure_value_array);
        b.putDoubleArray("x",xArray);
        b.putDoubleArray("y",yArray);
        b.putDoubleArray("z",zArray);

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

        runnable = new Runnable() {
            @Override
            public void run() {
                startTime();

                while(true){
                    if (start == false){
                        break;
                    }
                    time_vec.add(timeSec);

                    if (track_light)
                        light_vec.add(getLight_value());

                    if (track_pressure)
                        pressure_vec.add(getPressure_value());

                    if (track_accel){
                        accel_vec[0].add(getX());
                        accel_vec[1].add(getY());
                        accel_vec[2].add(getZ());
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread_TrackData = new Thread(runnable);
        thread_TrackData.start();

    }

    public void startTime() {
        new Thread(new Runnable() {
            public void run() {
                while (true){
                    if (start == false){
                        timeSec = 0;
                        break;
                    }
                    timeSec ++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (start){
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && track_accel){
                setX(event.values[0]);
                setY(event.values[1]);
                setZ(event.values[2]);
                ((TextView) findViewById(R.id.tv_accel)).setText("X: " + Math.round(getX()* Math.pow(10d,5))/Math.pow(10d,5) + "\n" + "Y: " +  Math.round(getY()* Math.pow(10d,5))/Math.pow(10d,5)
                        + "\n" + "Z: " +  Math.round(getZ()* Math.pow(10d,5))/Math.pow(10d,5));
            }

            if (event.sensor.getType() == Sensor.TYPE_LIGHT && track_light){
                setLight_value(event.values[0]);
                ((TextView) findViewById(R.id.tv_light)).setText(String.valueOf("Illuminance: " + getLight_value()) + " lx");
            }

            if (event.sensor.getType() == Sensor.TYPE_PRESSURE && track_pressure){
                setPressure_value(event.values[0]);
                ((TextView) findViewById(R.id.tv_pressure)).setText(String.valueOf("Pressure: " + Math.round(getPressure_value()* Math.pow(10d,3))/Math.pow(10d,3)) + " hPa");
            }


        }

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

    public double getPressure_value() {
        return pressure_value;
    }

    public void setPressure_value(double pressure_value) {
        this.pressure_value = pressure_value;
    }
}
