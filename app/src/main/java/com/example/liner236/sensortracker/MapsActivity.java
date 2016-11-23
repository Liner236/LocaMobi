package com.example.liner236.sensortracker;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double[] latitudeArray;
    double[] longitudeArray;
    LatLng me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            latitudeArray = extras.getDoubleArray("latitude");
            longitudeArray = extras.getDoubleArray("longitude");

        }





    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (latitudeArray == null || longitudeArray == null){
            System.out.println("RETUUUURN !!!");
            return;
        }
        else{
            try {
                me = new LatLng(latitudeArray[0],longitudeArray[0]);


            for (int i=1;i < latitudeArray.length;i++){
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitudeArray[i],longitudeArray[i])).title("Me" + i));
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me,16));
            }catch (Exception e){
                e.printStackTrace();
            }
        }







        /*for (int i=0;i<array.length;i++){

        }*/
    }
}
