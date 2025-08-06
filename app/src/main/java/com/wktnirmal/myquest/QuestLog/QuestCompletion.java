package com.wktnirmal.myquest.QuestLog;

import static android.app.PendingIntent.getActivity;


import static java.security.AccessController.getContext;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wktnirmal.myquest.MainActivity;
import com.wktnirmal.myquest.R;

public class QuestCompletion extends AppCompatActivity implements SensorEventListener {
    String questID;
    int reward;
    double endLat;
    double endLng;
    double liveLat;
    double liveLng;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    SensorManager sensorManager;
    double shakeThreshold = 5;
    Sensor linearAccelerationSensor;;
    boolean isAccelerometerAvailable;
    boolean completionInProgress;
    FirebaseFirestore database;
    FirebaseUser user;
    ProgressBar progressBar;
    String isTheQuestCompleted = "0";
    FusedLocationProviderClient fusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quest_completion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        //connect elements
        progressBar = findViewById(R.id.progressBar_completed);
        progressBar.setVisibility(android.view.View.INVISIBLE);

        //get the quest data from the intent
        questID = getIntent().getStringExtra("questID");
        reward = getIntent().getIntExtra("questReward", 0);
        endLat = getIntent().getDoubleExtra("endLat", 0.0);
        endLng = getIntent().getDoubleExtra("endLng", 0.0);

        //firebase connect
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseFirestore.getInstance();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //get the sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //check if the sensor is available
        if (linearAccelerationSensor != null) {
            isAccelerometerAvailable = true;
        } else {
            isAccelerometerAvailable = false;
            Toast.makeText(this, "Can't detect motion", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(QuestCompletion.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //completely clear the nav stack
            startActivity(intent);
            finish();
        }


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        double acceleration = Math.sqrt(x * x + y * y + z * z);

        if (acceleration >= shakeThreshold && completionInProgress == false) {
            progressBar.setVisibility(android.view.View.VISIBLE);
            completionInProgress = true;
            completeTheQuest();
        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAccelerometerAvailable) {
            sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

    }

    private void completeTheQuest() {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, get the location
            CurrentLocationRequest.Builder requestBuilder = new CurrentLocationRequest.Builder();
            requestBuilder.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
            requestBuilder.setMaxUpdateAgeMillis(5000); //5 seconds update time
            requestBuilder.setMaxUpdateAgeMillis(4000); //accept cached location upto 4 seconds

            CurrentLocationRequest currentLocationRequest = requestBuilder.build();
            fusedLocationClient.getCurrentLocation(currentLocationRequest, null)
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            liveLat = location.getLatitude();
                            liveLng = location.getLongitude();

                            //check if the user is close enough to the quest location
                            int liveLatRoundUp = (int) Math.round(liveLat*1000);
                            int liveLngRoundUp = (int) Math.round(liveLng*1000);
                            int endLatRoundUp = (int) Math.round(endLat*1000);
                            int endLngRoundUp = (int) Math.round(endLng*1000);

                            if (liveLatRoundUp == endLatRoundUp && liveLngRoundUp == endLngRoundUp){
                                syncDataToServer();
                            }else{
                                Toast.makeText(this, "You are not in the quest location", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(QuestCompletion.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(QuestCompletion.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });

        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }



    }


    private void syncDataToServer(){
        //check the quest status to prevent spam cheating
        database.collection("Users").document(user.getUid()).collection("Quests").document(questID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshot -> {
                    isTheQuestCompleted = queryDocumentSnapshot.getString("questStatus");

                    //update the user's xp (with anti-spamcheat)
                    if (isTheQuestCompleted.equals("1")) {
                        database.collection("Users").document(user.getUid())
                                .update("userXpAmount", FieldValue.increment(reward))
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Account Xp updated");
                                }).addOnFailureListener(e -> {
                                    Log.d("Firestore", "Error updating account Xp");
                                    Toast.makeText(this, "Error connecting to the server", Toast.LENGTH_SHORT).show();
                                    finish();
                                });

                        //mark the quest as completed (questStatus = 0) and navigate to the main activity
                        database.collection("Users").document(user.getUid()).collection("Quests").document(questID)
                                .update("questStatus", "0")
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Quest completed", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(QuestCompletion.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //completely clear the nav stack
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error completing the quest", Toast.LENGTH_SHORT).show());
                    }

                });

    }






}