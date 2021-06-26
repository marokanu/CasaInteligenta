package com.example.firstproject;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.SeekBar.OnSeekBarChangeListener;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ControlLed extends AppCompatActivity {

    public CircularBar seekBar;
    int progresie;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_led);
        seekBar = (CircularBar) findViewById(R.id.seekbar_point);

        seekBar.setOnCircularBarChangeListener(new CircularBar.OnCircularBarChangeListener() {
            @Override
            public void onPointsChanged(CircularBar swagPoints, int points, boolean fromUser) {
                      progresie=seekBar.getPoints();
                      FirebaseDatabase database = FirebaseDatabase.getInstance();
                      DatabaseReference myRef = database.getReference("BEC");
                      myRef.setValue(progresie);
            }

            @Override
            public void onStartTrackingTouch(CircularBar seekBar) {
                // Nothing to do
            }

            @Override
            public void onStopTrackingTouch(CircularBar seekBar) {
                // Nothing to do
            }

            @Override
            public void onProgressChanged(CircularBar seekBar, int progress, boolean fromUser) {

            }
        });






    }
}