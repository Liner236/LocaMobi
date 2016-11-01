package com.example.liner236.sensortracker;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class LightsaberActivity extends AppCompatActivity {

    ImageView iv_lightsaber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightsaber);

        iv_lightsaber = (ImageView)findViewById(R.id.iv_lightsaber);
        iv_lightsaber.setBackground(Drawable.createFromPath());
    }
}
