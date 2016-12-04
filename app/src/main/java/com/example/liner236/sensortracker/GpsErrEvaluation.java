package com.example.liner236.sensortracker;

import android.content.Intent;
import android.graphics.Color;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;
import java.util.Vector;


public class GpsErrEvaluation extends AppCompatActivity implements LocationListener, com.google.android.gms.location.LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private LocationManager locationmanagerGPS;
    private Location locationGPS = null;
    private static final long MIN_TIME_TO_REFRESH = 1000L;
    private static final float MIN_DISTANCE_TO_REFRESH = 0F;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean gApi_isConnected =  false;


    private double latitudeGPS;
    private double longitudeGPS;
    private int metersGPS;
    private long time;

    Vector<Integer> vec_meters;
    Vector<Long> vec_time;
    private int tracking_counter;
    double[] err_percent_array;

    GraphView error_graph;

    private RadioGroup rg_accuracy;
    private RadioGroup rg_gps;

    private RadioButton rb_andorid_gps;
    private RadioButton rb_google_gps;
    private RadioButton rb_wifi;

    private RadioButton rb_high_accu;
    private RadioButton rb_balan_power;
    private RadioButton rb_low_power;
    private RadioButton rb_no_power;

    private TextView tv_gps;
    private TextView tv_gapi;
    private TextView tv_wlan;
    private TextView tv_ticks;
    private TextView tv_latlon;
    private TextView tv_dist_err;

    boolean isRunning = false;

    //Groundtruth und Locations
    private double[] myLatitude_arr;
    private double[] myLongitude_arr;
    private double[] gtLatitude_arr;
    private double[] gtLongitude_arr;
    private double[] distGTtoMY;
    private long[] track_time;


    private int i;

    // Mediaplayer Files
    MediaPlayer click_sound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_err_evaluation);

        locationmanagerGPS = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        vec_meters = new Vector<Integer>(50, 20);
        vec_time = new Vector<Long>(50, 20);
        err_percent_array = new double[100];

        rg_accuracy = (RadioGroup) findViewById(R.id.rg_accuracy);
        rg_gps = (RadioGroup) findViewById(R.id.rg_gps);

        rb_andorid_gps = (RadioButton) findViewById(R.id.rb_android_gps);
        rb_google_gps = (RadioButton) findViewById(R.id.rb_google_gps);
        rb_wifi = (RadioButton) findViewById(R.id.rb_wifi);

        rb_high_accu = (RadioButton) findViewById(R.id.rb_high_accu);
        rb_balan_power = (RadioButton) findViewById(R.id.rb_balanc_power);
        rb_low_power = (RadioButton) findViewById(R.id.rb_low_power);
        rb_no_power = (RadioButton) findViewById(R.id.rb_no_power);

        rb_andorid_gps.setEnabled(true);
        rb_high_accu.setEnabled(true);

        tv_gps = (TextView)findViewById(R.id.tv_gps);
        tv_gapi = (TextView)findViewById(R.id.tv_gapi);
        tv_wlan = (TextView)findViewById(R.id.tv_wlan);
        tv_ticks = (TextView)findViewById(R.id.tv_ticks);
        tv_latlon = (TextView)findViewById(R.id.tv_latlon);
        tv_dist_err = (TextView)findViewById(R.id.tv_dist_err);

        click_sound = MediaPlayer.create(this, R.raw.adriantnt_bubble_clap);

        //Groundtruth ...
        myLatitude_arr = new double[6];
        myLongitude_arr = new double[6];
        distGTtoMY = new double[6];
        track_time = new long[6];
        i = 0;

        gtLatitude_arr = new double[6];
        gtLatitude_arr[0] = 51.488758;
        gtLatitude_arr[1] = 51.488470;
        gtLatitude_arr[2] = 51.487853;
        gtLatitude_arr[3] = 51.487768;
        gtLatitude_arr[4] = 51.488007;
        gtLatitude_arr[5] = 51.488484;

        gtLongitude_arr = new double[6];
        gtLongitude_arr[0] = 7.206915;
        gtLongitude_arr[1] = 7.205716;
        gtLongitude_arr[2] = 7.205090;
        gtLongitude_arr[3] = 7.206373;
        gtLongitude_arr[4] = 7.207404;
        gtLongitude_arr[5] = 7.207112;





        Button btn_start = (Button) findViewById(R.id.btn_start_two);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                isRunning = true;

                if (rb_wifi.isChecked() || rb_andorid_gps.isChecked()) {
                    startAndroidLocationUpdates();
                    System.out.println("CHOOSED ANDROID");
                }
                else if(rb_google_gps.isChecked()){
                    startGoogleLocationUpdates();
                    System.out.println("CHOOSED GOOGLE");
                }
                checkConnections();
            }
        });

        Button btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                isRunning = false;
                tracking_counter = 0;
                tv_ticks.setText("Ticks: " + 0);
                locationGPS = null;
                error_graph.removeAllSeries();
                vec_meters.removeAllElements();
                vec_time.removeAllElements();
                killGPSTracking();
                tv_latlon.setText("Latitude: \tLongitude:");
                tv_dist_err.setText("Distance:");

            }
        });

        Button btn_track = (Button) findViewById(R.id.btn_track);
        btn_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPosition();

            }
        });

        Button btn_dist_err = (Button) findViewById(R.id.btn_dist_err);
        btn_dist_err.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distBettweenPoits();

            }
        });

        Button btn_map = (Button) findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToGTMaps(v);

            }
        });

        Button btn_auswertung = (Button) findViewById(R.id.btn_auswertung);
        btn_auswertung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                try {
                    fehlerAuswertung();
                    draw();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        Button btn_arthur_eva = (Button) findViewById(R.id.btn_arthur_eva);
        btn_arthur_eva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToArthurEva(v);
            }
        });


        // GOOGLE SHIIIIIIIIIIIIIIIIIIIIIIIT

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }



        //------------------------------------------------------------------------


        error_graph = (GraphView) findViewById(R.id.graph_error);
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

        checkConnections();

    }

    private void trackPosition(){
        if (i < 6){
            myLatitude_arr[i] = latitudeGPS;
            myLongitude_arr[i] = longitudeGPS;
            track_time[i] = time;
            tv_latlon.setText(tv_latlon.getText() + "\n" + "TS: " + track_time[i] + "\n" + myLatitude_arr[i] + "\t\t" + myLongitude_arr[i]);
            i++;
            System.out.println("i: ------------- : " + i);
        }
        else{
            Toast.makeText(this, "Value limit reached", Toast.LENGTH_LONG).show();
        }


    }

    private void distBettweenPoits(){
        Location myLoca = new Location(LocationManager.GPS_PROVIDER);
        Location gtLoca = new Location(LocationManager.GPS_PROVIDER);
        for (int i = 0;i < 6;i++){
            myLoca.setLatitude(this.myLatitude_arr[i]);
            myLoca.setLongitude(this.myLongitude_arr[i]);
            gtLoca.setLatitude(this.myLatitude_arr[i]);
            gtLoca.setLongitude(this.myLongitude_arr[i]);
            distGTtoMY[i] = myLoca.distanceTo(gtLoca);

            tv_dist_err.setText(tv_dist_err.getText() + "\n" + distGTtoMY[i]);
        }
    }

    protected void killGPSTracking(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        locationmanagerGPS.removeUpdates(this);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationmanagerGPS.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        if (isRunning) {
            trackGpsValues(location);
            tv_ticks.setText("Ticks: " + tracking_counter);
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

    private void trackGpsValues(Location locationGPS) {

        if (locationGPS != null) {
            latitudeGPS = locationGPS.getLatitude();
            longitudeGPS = locationGPS.getLongitude();

            time = locationGPS.getTime();
            metersGPS = (int) locationGPS.getAccuracy();

            vec_meters.add((int) locationGPS.getAccuracy());
            vec_time.add(locationGPS.getTime());

            tracking_counter += 1;

            System.out.println("Meters: " + metersGPS + "Lat: " + locationGPS.getLatitude() + "Lon: " + locationGPS.getLongitude());
        } else {
            Toast.makeText(this, "location = NULL", Toast.LENGTH_LONG).show();
        }
    }

    private void fehlerAuswertung() {
        int count;
        double saver;
        double t_c = tracking_counter; // benötigt für division <= 0
        double c; // benötigt für division <= 0

        // Meter-Scheife
        for (int i = 0; i < 100; i++) {
            count = 0;
            c = 0.0;
            // Meter-Mengen-Schleife
            for (int j = 0; j < vec_meters.size(); j++) {
                if (vec_meters.elementAt(j) == i) {
                    count += 1;
                }
            }
            c = count; // benötigt für division <= 0
            //Umrechnung in Prozent
            saver = c / t_c; // Unsinnigster Scheiß oder bestes Beispiel für Eliminierung !? benötigt für division <= 0
            err_percent_array[i] = saver * 100;
            System.out.println("i: " + i + "\tCount: " + c + "\tTracking_Counter: " + t_c + "\t Err.Percent: " + err_percent_array[i] + "\tSaver: " + saver);
        }
    }

    private void draw() {

        DataPoint[] pointsY = new DataPoint[err_percent_array.length];

        for (int i = 0; i < pointsY.length; i++) {
            pointsY[i] = new DataPoint(i, err_percent_array[i]);
        }

        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(pointsY);
        error_graph.addSeries(series);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        gApi_isConnected = true;
        checkConnections();
    }

    @Override
    public void onConnectionSuspended(int i) {
        gApi_isConnected = false;
        checkConnections();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        gApi_isConnected = false;
        checkConnections();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        //mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setInterval(1000);

        if (rb_high_accu.isChecked()){
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        else if(rb_balan_power.isChecked()){
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
        else if(rb_low_power.isChecked()){
            mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        }
        else{
            mLocationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
        }

    }

    protected void startGoogleLocationUpdates(){
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        //locationGPS = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    protected void startAndroidLocationUpdates(){
        if (rb_andorid_gps.isChecked()) {
            locationmanagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_TO_REFRESH, MIN_DISTANCE_TO_REFRESH, this);
            System.out.println("startAndoidLocationUpdates: Android GPS");
            //locationGPS = locationmanagerGPS.getLastKnownLocation(locationmanagerGPS.GPS_PROVIDER);
        } else if (rb_wifi.isChecked()) {
            locationmanagerGPS.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_TO_REFRESH, MIN_DISTANCE_TO_REFRESH, this);
            System.out.println("startAndoidLocationUpdates: Wifi");
            //locationGPS = locationmanagerGPS.getLastKnownLocation(locationmanagerGPS.NETWORK_PROVIDER);
        }
    }

    private void checkConnections(){
        if(locationmanagerGPS.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            tv_gps.setText("GPS: Ok");
            tv_gps.setTextColor(getResources().getColor(R.color.green_access));
        }
        else{
            tv_gps.setText("GPS: N.A");
            tv_gps.setTextColor(getResources().getColor(R.color.red_failure));
        }

        if (gApi_isConnected == false){
            tv_gapi.setText("gAPI: N.A");
            tv_gapi.setTextColor(getResources().getColor(R.color.red_failure));
        }
        else{
            tv_gapi.setText("gAPI: Ok");
            tv_gapi.setTextColor(getResources().getColor(R.color.green_access));
        }

        if(locationmanagerGPS.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            tv_wlan.setText("WLAN: Ok");
            tv_wlan.setTextColor(getResources().getColor(R.color.green_access));
        }
        else{
            tv_wlan.setText("WLAN: N.A");
            tv_wlan.setTextColor(getResources().getColor(R.color.red_failure));
        }
    }

    public void changeToArthurEva(View view){
        Intent i = new Intent(this,ArthursEvaluation.class);
        startActivity(i);
    }

    public void changeToGTMaps(View view){
        Intent i = new Intent(this,GTMapsActivity.class);
        Bundle b = new Bundle();

        b.putDoubleArray("myLatitude",myLatitude_arr);
        b.putDoubleArray("myLongitude",myLongitude_arr);
        b.putDoubleArray("gtLatitude",gtLatitude_arr);
        b.putDoubleArray("gtLongitude",gtLongitude_arr);

        i.putExtras(b);

        startActivity(i);
    }

}
