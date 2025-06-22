package com.wktnirmal.myquest;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    DatabaseHelper questData; //database instance (getContext because of the fragment)




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //initialize the database
        questData = new DatabaseHelper(getContext());
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
        //update the map with marks
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_internal_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        GoogleMap gameMap = googleMap;

        //get the locations from the database
        Cursor cursor = questData.getQuestLocations();

        //add the location markers to the map
        if (cursor != null && cursor.moveToFirst()){
            do {

                String questTitle = cursor.getString(0);
                double endLat = cursor.getDouble(1);
                double endLng = cursor.getDouble(2);

                LatLng questLocation = new LatLng(endLat, endLng);
                gameMap.addMarker(new MarkerOptions().position(questLocation).title(questTitle));
                gameMap.moveCamera(CameraUpdateFactory.newLatLngZoom(questLocation,15.0f));
                gameMap.getUiSettings().setZoomControlsEnabled(true);

            }while (cursor.moveToNext());

            cursor.close();
        }


//        //this is a testing marker
//        LatLng testingColombo = new LatLng(6.936769895547724, 79.8337259436081);
//        gameMap.addMarker(new MarkerOptions().position(testingColombo).title("Colombo PortCity"));
//        gameMap.moveCamera(CameraUpdateFactory.newLatLngZoom(testingColombo,10.0f));
//        gameMap.getUiSettings().setZoomControlsEnabled(true);
        
    }
}