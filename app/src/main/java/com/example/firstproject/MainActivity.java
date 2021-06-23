package com.example.firstproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.graphics.Color;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {
    DatabaseReference mydb,mydb1,mydb2,mydb3,mydb4;
    TextView temp,hum, alert,alerta,gazalert,nivelgaz1,incendiu;
    Button button;
    int progress = 0;
    int nivel2gaz=0;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temp = (TextView) findViewById(R.id.temp);
        hum = (TextView) findViewById(R.id.hum);
        alert = (TextView) findViewById(R.id.alert);
        alerta= (TextView) findViewById(R.id.alerta);
        gazalert=(TextView) findViewById(R.id.alertaGaz);
        progressBar = (ProgressBar) findViewById(R.id.progressWater);
        nivelgaz1=(TextView) findViewById(R.id.nivelGaz);
        incendiu=(TextView) findViewById(R.id.nivelFoc);
        createNotificationChannel();
        createNotificationChannel1();
        createNotificationChannel2();

        // Create an Intent for the activity you want to start
        Intent notificationIntent2 = new Intent(this, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder2 = TaskStackBuilder.create(this);
        stackBuilder2.addNextIntentWithParentStack(notificationIntent2);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent2 =
                stackBuilder2.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder2 = new NotificationCompat.Builder(this, "Foc")
                .setSmallIcon(R.drawable.iconita)
                .setContentTitle("Senzor flacara")
                .setContentText("Pericol de incendiu !")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(resultPendingIntent2)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager2 = NotificationManagerCompat.from(this);

        mydb4 = FirebaseDatabase.getInstance().getReference().child("Foc");
        try {

            mydb4.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Aceasta metoda este apelata cu valoarea initiala si dupa aceea mereu cand se schimba valoarea
                    String incendiu1 = dataSnapshot.child("mesaj").getValue().toString();
                    String flacara = dataSnapshot.child("val").getValue().toString();
                    incendiu.setText(incendiu1);
                    int flacara1 = Integer.parseInt(flacara);
                    if(flacara1 == 0){
                        notificationManager2.notify(96, builder2.build());
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Eroare la citire

                }
            });
        } catch (Exception e) {


        }

        // Create an Intent for the activity you want to start
        Intent notificationIntent = new Intent(this, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(notificationIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Gaz")
                .setSmallIcon(R.drawable.iconita)
                .setContentTitle("Senzor gaze")
                .setContentText("Nivel crescut de gaze")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        mydb3 = FirebaseDatabase.getInstance().getReference().child("Gas");
        try {

            mydb3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Aceasta metoda este apelata cu valoarea initiala si dupa aceea mereu cand se schimba valoarea
                    String Gaz = dataSnapshot.child("alerta").getValue().toString();
                    String nivelGaz = dataSnapshot.child("nivel").getValue().toString();
                    gazalert.setText(Gaz);
                    nivelgaz1.setText(nivelGaz);
                    nivel2gaz=Integer.parseInt(nivelGaz);
                    if(nivel2gaz > 50){
                        notificationManager.notify(100, builder.build());

                    }


                }


                @Override
                public void onCancelled(DatabaseError error) {
                    // Eroare la citire

                }
            });
        } catch (Exception e) {


        }
        // Create an Intent for the activity you want to start
        Intent notificationIntent1 = new Intent(this, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder1 = TaskStackBuilder.create(this);
        stackBuilder1.addNextIntentWithParentStack(notificationIntent1);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent1 =
                stackBuilder1.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder1 = new NotificationCompat.Builder(this, "Apa")
                .setSmallIcon(R.drawable.iconita)
                .setContentTitle("Senzor apa")
                .setContentText("Pericol de inundatie!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(resultPendingIntent1)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(this);



        mydb2 = FirebaseDatabase.getInstance().getReference().child("Water");
        try {

            mydb2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Aceasta metoda este apelata cu valoarea initiala si dupa aceea mereu cand se schimba valoarea
                    String Water = dataSnapshot.child("alerta").getValue().toString();
                    String Nivel = dataSnapshot.child("nivel").getValue().toString();
                    alerta.setText(Water);
                     progress=Integer.parseInt(Nivel);
                    progressBar.setProgress(progress);
                    if(progress >50){
                        notificationManager1.notify(99, builder1.build());

                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Eroare la citirea din FireBase

                }
            });
        } catch (Exception e) {


        }



        mydb1 = FirebaseDatabase.getInstance().getReference().child("Motion");
        try {

            mydb1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Aceasta metoda este apelata cu valoarea initiala si dupa aceea mereu cand se schimba valoarea
                    String pirdata = dataSnapshot.child("alert").getValue().toString();
                   alert.setText(pirdata);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Eroare la citire

                }
            });
        } catch (Exception e) {


        }

        mydb = FirebaseDatabase.getInstance().getReference().child("Sensor");
        try {

            mydb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String tempdata = dataSnapshot.child("temp").getValue().toString();
                    String humdata = dataSnapshot.child("hum").getValue().toString();
                    temp.setText(tempdata);
                    hum.setText(humdata);


                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Eroare la citire

                }
            });
        } catch (Exception e) {


        }

        button = (Button) findViewById(R.id.nextID2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });



    }

    public void openNewActivity(){
        Intent intent = new Intent(this, Window.class);
        startActivity(intent);
    }




    public void nextActivity (View v){

        Intent i = new Intent(this,Lights.class);
        startActivity(i);

    }

    public void nextActivity1 (View v){

        Intent i1 = new Intent(this,ControlLed.class);
        startActivity(i1);

    }

    public void nextActivity4 (View v){

        Intent i2 = new Intent(this,Alarm.class);
        startActivity(i2);

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "gasChannel";
            String description = "A channel for gas alert";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Gaz", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void createNotificationChannel1() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "waterChannel";
            String description = "A channel for water alert";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Apa", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager1 = getSystemService(NotificationManager.class);
            notificationManager1.createNotificationChannel(channel);
        }
    }

    private void createNotificationChannel2() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "flameChannel";
            String description = "A channel for flame alert";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Foc", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager2 = getSystemService(NotificationManager.class);
            notificationManager2.createNotificationChannel(channel);
        }
    }


}