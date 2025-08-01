package com.wktnirmal.myquest.QuestLog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wktnirmal.myquest.MainActivity;
import com.wktnirmal.myquest.Quest;
import com.wktnirmal.myquest.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateQuest extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseFirestore databaseQuests = FirebaseFirestore.getInstance();  //firestore initialization

    GoogleMap inputMap;
    Button submitNewQuestButton;
    EditText questTitleInput, questLocationInput, questDescriptionInput;
    Switch repetitiveQuestSwitch;
    double startLat;
    double startLng;
    double endLat ;
    double endLng ;
    int distance;
    String repetitive = "0";
    List<Address> addressList;
    FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_quest);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.createQuestScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //connect map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_internal_fragment);
        mapFragment.getMapAsync(this);

        //connect button and text fields
        submitNewQuestButton = findViewById(R.id.btn_submitNewQuest);
        questTitleInput = findViewById(R.id.questTitle);
        questLocationInput = findViewById(R.id.questLocationInput);
        questDescriptionInput = findViewById(R.id.questDescriptionScrollView);
        repetitiveQuestSwitch = findViewById(R.id.switch_repititive);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //firebase connect
        fAuth = FirebaseAuth.getInstance();


        updateLocationOnMinimap();

        submitNewQuest();

    }




    //OnFocusChange to update user input location and the Map.
    public void updateLocationOnMinimap() {
        questLocationInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getLocationData();
                }
            }
        });
    }


    //get the location, data to the database, navigate to the quest log
    private void submitNewQuest() {
        submitNewQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!questTitleInput.equals("") && endLat != 0.0 && endLng != 0.0) {
                    //process the user input location and assign end lat,lng variables
                    getLocationData();

                    //assign the distance in meters to the variable
                    calculateDistance();

                    //assign user's current lat, lang
                    getUserLiveLocation();

                    //insert the data to the firebase
                    addDataToFirebase();

//                    //navigate back to the quest log
//                    Intent intent = new Intent(CreateQuest.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
                }else{
                    Toast.makeText(CreateQuest.this, "Please fill title and location", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }




    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        inputMap = googleMap;

        getUserLiveLocation();

        LatLng initialLocation = new LatLng(startLat, startLng);
        inputMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15.0f)); // Low zoom to show a large area


    }


    //Gets endLat and endLng from the user input location and show it on the mini map & assign values
    public void getLocationData(){
        String userInputLocation = questLocationInput.getText().toString().trim();
        Geocoder geocoder = new Geocoder(CreateQuest.this);


        try {
            addressList = geocoder.getFromLocationName(userInputLocation, 1);

            //show the place on the map
            if (addressList != null && !addressList.isEmpty()){
                Address address = addressList.get(0);
                LatLng latLngLocation = new LatLng(address.getLatitude(), address.getLongitude());

                //assign the end Lat & Lang to the variables
                endLat = address.getLatitude();
                endLng = address.getLongitude();




                inputMap.clear(); // Clear previous markers

                getUserLiveLocation();

                //LatLng initialLocation = new LatLng(startLat, startLng); // live location map view
//                inputMap.addMarker(new MarkerOptions().position(initialLocation).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                inputMap.addMarker(new MarkerOptions().position(latLngLocation).title(userInputLocation));
                inputMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 15.0f));
            } else {
                // Handle the case where no address is found
                Toast.makeText(CreateQuest.this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) { //catch exceptions
            if (!userInputLocation.equals("")) {
                e.printStackTrace();
                Toast.makeText(CreateQuest.this, "Check your network connection", Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            Toast.makeText(CreateQuest.this, "Invalid location", Toast.LENGTH_SHORT).show();
        }
    }


    //get user's current location and assign it to startLat and startLng variables
    private void getUserLiveLocation() {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, get the location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            startLat = location.getLatitude();
                            startLng = location.getLongitude();

                            //Log.d("Location", "Lat: " + startLat + ", Lng: " + startLat);

                            //Set an initial location and zoom level for the map
                            LatLng initialLocation = new LatLng(startLat, startLng); // live location map view
                            inputMap.addMarker(new MarkerOptions().position(initialLocation).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                            if (endLat == 0.0 && endLng == 0.0) { //so the camera will only zoom at the live location only at first
                                inputMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15.0f)); // Low zoom to show a large area
                                inputMap.getUiSettings().setZoomControlsEnabled(true);
                            }
                        }
                    });


        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //method for calculate the straight line distance and assign it to the "distance" variable
    public void calculateDistance(){
        Location startLocation = new Location("start");
        startLocation.setLatitude(startLat);
        startLocation.setLongitude(startLng);

        Location endLocation = new Location("end");
        endLocation.setLatitude(endLat);
        endLocation.setLongitude(endLng);

        distance = (int) startLocation.distanceTo(endLocation); //returns the distance in meters as a float and converted to int
    }

    public void addDataToFirebase(){
        if (!questTitleInput.equals("") && endLat != 0.0 && endLng != 0.0) {
            if (repetitiveQuestSwitch.isChecked()) {
                repetitive = "1";
            }

            Quest newquest = new Quest(questTitleInput.getText().toString(), questDescriptionInput.getText().toString(), startLat, startLng, endLat, endLng, distance, "1", repetitive);
            Map<String, Object> Quest = new HashMap<>();

            databaseQuests.collection("users").document(fAuth.getCurrentUser().getUid()).collection("Quests").add(newquest)
                    .addOnSuccessListener(docRef -> {
                        String questId = docRef.getId(); // Get the generated document ID
                        databaseQuests.collection("users").document(fAuth.getCurrentUser().getUid()).collection("Quests").document(questId).update("id", questId);

                        //navigate back to the quest log
                        Intent intent = new Intent(CreateQuest.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Quest added successfully"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error adding quest", e));

//            databaseQuests.collection("Quests").add(newquest)
//                    .addOnSuccessListener(docRef -> {
//                        String questId = docRef.getId();
//                        databaseQuests.collection("quests").document(questId).update("id", questId);
//                    });
        }
    }










}
