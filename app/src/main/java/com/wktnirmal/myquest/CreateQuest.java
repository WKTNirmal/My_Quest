package com.wktnirmal.myquest;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class CreateQuest extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap inputMap;
    Button submitNewQuestButton;
    EditText questTitleInput, questLocationInput, questDescriptionInput;
    Switch repetitiveQuestSwitch;
    DatabaseHelper questData; // database instance
    //get the location lat and lng
    double startLat = 6.93260339209919;
    double startLng = 79.84594898435564;
    public double endLat ;
    public double endLng ;
    public int distance;
    public String repetitive;
    List<Address> addressList;

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
        questTitleInput = findViewById(R.id.questTitleInput);
        questLocationInput = findViewById(R.id.questLocationInput);
        questDescriptionInput = findViewById(R.id.questDescriptionInput);
        repetitiveQuestSwitch = findViewById(R.id.switch_repititive);


        //connect to the database
        questData = new DatabaseHelper(this);



        updateLocation();

        submitNewQuest();



    }






    //OnFocusChange to update user input location and the Map.
    public void updateLocation() {
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
                //process the user input location and assign end lat,lng variables
                getLocationData();

                //assign the distance in meters to the variable
                calculateDistance();


                //insert all the data to the database
                addData();


                //navigate back to the quest log
                Intent intent = new Intent(CreateQuest.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        inputMap = googleMap;

        //Set an initial location and zoom level for the map
        LatLng initialLocation = new LatLng(0, 0); // default map view
        inputMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 2.0f)); // Low zoom to show a large area
        inputMap.getUiSettings().setZoomControlsEnabled(true);

    }


    //method for get endLat and endLng from the user input location and show it on the mini map
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


                //create a marker on the map
                inputMap.clear(); // Clear previous markers
                inputMap.addMarker(new MarkerOptions().position(latLngLocation).title(userInputLocation));
                inputMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 15.0f));
            } else {
                // Handle the case where no address is found
                Toast.makeText(CreateQuest.this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) { //catch exceptions
            e.printStackTrace();
            Toast.makeText(CreateQuest.this, "Check your network connection", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            Toast.makeText(CreateQuest.this, "Invalid location", Toast.LENGTH_SHORT).show();
        }
    }

    //method for insert data to the database
    public void addData (){
        if (questTitleInput != null && endLat != 0.0 && endLng != 0.0){
            if (repetitiveQuestSwitch.isChecked()){
                repetitive = "1";
            }
            boolean isInserted = questData.insertData(questTitleInput.getText().toString(),questDescriptionInput.getText().toString(),startLat,startLng,endLat,endLng,distance,"1",repetitive );
            if (isInserted == true){
                Toast.makeText(this, "Quest created", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
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







}
