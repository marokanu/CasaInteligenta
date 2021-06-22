package com.example.firstproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Lights extends AppCompatActivity {
    Button on;
    Button off;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights);
        on = findViewById(R.id.button_on);
        off = findViewById(R.id.button_off);



        on.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Scriem valoarea in baza de date FireBase
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("LED_STATUS");//LED_STATUS is Firebase database LED_STATUS
                myRef.setValue(1);
            }
        });
        off.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("LED_STATUS");//LED_STATUS is Firebase database LED_STATUS
                myRef.setValue(0);
            }
        });
    }
}