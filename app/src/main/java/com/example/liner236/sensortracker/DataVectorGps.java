package com.example.liner236.sensortracker;

/**
 * Created by Liner236 on 26.10.2016.
 */

public class DataVectorGps {

    public double latitude;
    public double longitude;

    public DataVectorGps(double lat, double lon){
        this.latitude = lat;
        this.longitude = lon;
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
