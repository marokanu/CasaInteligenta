package com.example.firstproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Alarm extends AppCompatActivity {
    Button arm;
    Button dez;
    DatabaseReference mydb;
    TextView mesaj;
    int armval = 0;
    int armval1=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        arm = findViewById(R.id.arm);
        dez = findViewById(R.id.dezarm);
        mesaj = (TextView) findViewById(R.id.alertare);
        createNotificationChannel();

        // Create an Intent for the activity you want to start
        Intent notificationIntent = new Intent(this, Alarm.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(notificationIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Alarm")
                .setSmallIcon(R.drawable.iconita)
                .setContentTitle("Alarma")
                .setContentText("Intrus prezent ! ")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager2 = NotificationManagerCompat.from(this);

        mydb = FirebaseDatabase.getInstance().getReference().child("Alarma");
        try {

            mydb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Aceasta metoda este apelata mereu la schimbarea valorii din FireBase
                    String alarmare = dataSnapshot.child("mesaj").getValue().toString();
                    String alarm = dataSnapshot.child("val").getValue().toString();
                    String alarm2 = dataSnapshot.child("val2").getValue().toString();
                    mesaj.setText(alarmare);
                    armval=Integer.parseInt(alarm);
                    armval1=Integer.parseInt(alarm2);

                    if(armval==1 && armval1 == 1){
                        notificationManager2.notify(98, builder.build());

                    }


                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef1 = database.getReference("Alarma/mesaj");


                    if (armval == 1){

                        myRef1.setValue("ARMATA");

                    }
                    else {

                        myRef1.setValue("DEZARMATA");

                    }



                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Eroare la citire

                }
            });
        } catch (Exception e) {


        }




        arm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Alarma/val");
                myRef.setValue(1);
            }
        });
        dez.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Alarma/val");
                myRef.setValue(0);
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "alarmChannel";
            String description = "A channel for gas alert";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Alarm", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



}