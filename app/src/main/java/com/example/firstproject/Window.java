package com.example.firstproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Window extends AppCompatActivity {

    Button Up;
    Button Down;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window);

        Up = findViewById(R.id.button_up);
        Down = findViewById(R.id.button_down);

        Up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef1 = database.getReference("WINDOW");

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        myRef1.setValue(1);
                        break;
                    case MotionEvent.ACTION_UP:
                        myRef1.setValue(0);
                        break;
                }
                return false;
            }
        });

        Down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef1 = database.getReference("WINDOW");

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        myRef1.setValue(2);
                        break;
                    case MotionEvent.ACTION_UP:
                        myRef1.setValue(0);
                        break;
                }
                return false;
            }
        });




    }
}