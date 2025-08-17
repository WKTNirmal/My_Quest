package com.wktnirmal.myquest.Map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wktnirmal.myquest.R;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    FirebaseFirestore databaseQuests = FirebaseFirestore.getInstance();  //firestore initialization
    FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    GoogleMap gameMap;
    double liveLat;
    double liveLng;
    FirebaseUser user;
    CardView cardView_loadingMSG;




    public MapFragment() {
        // Required empty public constructor
    }


    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        cardView_loadingMSG = view.findViewById(R.id.cardView_loadingMsg);

        //firebase connect
        user = FirebaseAuth.getInstance().getCurrentUser();

        //update the map with marks
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_internal_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gameMap = googleMap;
        gameMap.clear(); // Clear previous markers
        gameMap.getUiSettings().setZoomControlsEnabled(true);

        getUserLiveLocation();

        addMarkersOnMap(gameMap);



        
    }

    //add the location markers to the map
    private void addMarkersOnMap(GoogleMap gameMap) {

//        //To wait until user's current location to be assigned
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                //add user's live location
//                LatLng liveLocation = new LatLng(liveLat, liveLng); // live location map view
//
//                gameMap.addMarker(new MarkerOptions().position(liveLocation).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//                gameMap.animateCamera(CameraUpdateFactory.newLatLngZoom(liveLocation,10.0f));
//                gameMap.getUiSettings().setZoomControlsEnabled(true);
//            }
//        }, 2000);


//        //add user's live location
//        LatLng liveLocation = new LatLng(liveLat, liveLng); // live location map view
//
//        gameMap.addMarker(new MarkerOptions().position(liveLocation).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//        gameMap.animateCamera(CameraUpdateFactory.newLatLngZoom(liveLocation,10.0f));
//        gameMap.getUiSettings().setZoomControlsEnabled(true);


        //gets the quest locations from the database and add marks
        databaseQuests.collection("Users").document(user.getUid()).collection("Quests")
                .whereEqualTo("questStatus", "1")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                        double endLat = doc.getDouble("endLat");
                        double endLng = doc.getDouble("endLng");
                        String questTitle = doc.getString("questTitle");

                        LatLng questLocation = new LatLng(endLat, endLng);
                        gameMap.addMarker(new MarkerOptions().position(questLocation).title(questTitle));
//                        gameMap.animateCamera(CameraUpdateFactory.newLatLngZoom(questLocation,15.0f));
//                        gameMap.getUiSettings().setZoomControlsEnabled(true);

                    }

                    Log.d("Firestore", "Markers added" );
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding markers", e));
    }


    private void getUserLiveLocation() {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, get the location

            CurrentLocationRequest.Builder requestBuilder = new CurrentLocationRequest.Builder();
            requestBuilder.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
            requestBuilder.setMaxUpdateAgeMillis(10000); //10 seconds update time
            requestBuilder.setMaxUpdateAgeMillis(9000); //accept cached location upto 9 seconds

            CurrentLocationRequest currentLocationRequest = requestBuilder.build();

            fusedLocationClient.getCurrentLocation(currentLocationRequest, null)
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            liveLat = location.getLatitude();
                            liveLng = location.getLongitude();

                            //Log.d("Location", "Lat: " + startLat + ", Lng: " + startLat);

                            addLiveLocationMarker();
                        }
                        cardView_loadingMSG.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Location", "Error getting location", e);
                        cardView_loadingMSG.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error getting location", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //put the live location marker
    private void addLiveLocationMarker() {
        LatLng liveLocation = new LatLng(liveLat, liveLng); // live location map view

        gameMap.addMarker(new MarkerOptions().position(liveLocation).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        gameMap.animateCamera(CameraUpdateFactory.newLatLngZoom(liveLocation,12.0f));
    }

}