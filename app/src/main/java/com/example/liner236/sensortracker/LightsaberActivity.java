package com.example.liner236.sensortracker;

import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class LightsaberActivity extends AppCompatActivity implements SensorEventListener {


    private SensorManager sensorManager = null;
    private Sensor gyro_sensor;
    private double x,y,z;
    boolean check = false;

    private SensorManager sensorManager_light = null;
    private Sensor light_sensor;
    private double light_value;

    MediaPlayer saber_on;
    MediaPlayer saber_off;
    MediaPlayer saber_swing;
    MediaPlayer saber_normal_hit;
    MediaPlayer saber_hard_hit;
    MediaPlayer vader_breath;

    double new_diff;
    double old_diff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightsaber);

        check = true;
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        gyro_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this,gyro_sensor,SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager_light = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        light_sensor = sensorManager_light.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager_light.registerListener(this,light_sensor,SensorManager.SENSOR_DELAY_NORMAL);

        saber_on = MediaPlayer.create(this, R.raw.lightsaber_on);
        saber_off = MediaPlayer.create(this,R.raw.saber_off);
        saber_swing = MediaPlayer.create(this,R.raw.saber_swing);
        saber_normal_hit = MediaPlayer.create(this,R.raw.saber_normal_hit);
        saber_hard_hit = MediaPlayer.create(this,R.raw.saber_hard_hit);
        vader_breath = MediaPlayer.create(this,R.raw.vader_breath);

        saber_on.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        check = false;
        saber_off.start();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE && check){
            System.out.print(event.values[0]+ " , ");
            System.out.print(event.values[1] + " , ");
            System.out.println(event.values[2]);

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            /*if (x > 2 && x <= 7 || x >= -7 && x < -2 || y > 2 && y <= 7 || y >= -7 && y < -2 || z > 2 && z <= 7 || z >= -7 && z < -2){  // [-7,-2] oder [2,7]
                // saber swing
                saber_swing.start();

            }*/

            if (x > 5 && x <=10 || x > -10 && x < -5 || y > 5 && y <=10 || y > -10 && y < -5 || z > 5 && z <=10 || z > -10 && z < -5){ // (7,10] oder [-10,-7)
                // Normal saber hit
                saber_normal_hit.start();
            }

            if (x > 10 || y > 10 || z > 12 || x < -10 || y < -10 || z < -10){ // [10,unendlich] oder [uendlich,-10]
                // Hard saber hit
                saber_hard_hit.start();
            }


        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT){
            light_value = event.values[0];
            if (light_value < 5){
                vader_breath.start();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
