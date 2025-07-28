package com.wktnirmal.myquest.QuestLog;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

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
import com.wktnirmal.myquest.R;

//TODO add the repititive ui / delete quest UI
public class ViewQuest extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap previewMap;
    TextView questTitleText, questDistanceText, questDescriptionText;
    String title, description;
    int reward;
    double endLat;
    double endLng;
    int repetitive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_quest);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //connect map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.locationPreviewMapFragment);
        mapFragment.getMapAsync(this);


        //get the data from the intent
        title = getIntent().getStringExtra("questTitle");
        reward = getIntent().getIntExtra("questReward", 0);
        description = getIntent().getStringExtra("questDescription");
        endLat = getIntent().getDoubleExtra("questEndLat", 0.0);
        endLng = getIntent().getDoubleExtra("questEndLng", 0.0);
        repetitive = getIntent().getIntExtra("questRepetitive", 0);

        questTitleText = findViewById(R.id.textView_questTitlePreview);
        questDistanceText = findViewById(R.id.textView_rewardPreview);
        questDescriptionText = findViewById(R.id.textView_descriptionPreview);

        questTitleText.setText(title);
        questDistanceText.setText(String.valueOf(reward));
        questDescriptionText.setText(description);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        previewMap = googleMap;

        LatLng questLocation = new LatLng(endLat, endLng);
        previewMap.animateCamera(CameraUpdateFactory.newLatLngZoom(questLocation, 15.0f)); // Low zoom to show a large area
        previewMap.addMarker(new MarkerOptions().position(questLocation).title("Quest Location"));


    }
}