package com.wktnirmal.myquest.QuestLog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wktnirmal.myquest.MainActivity;
import com.wktnirmal.myquest.R;

//TODO add the repititive ui function/ delete quest UI
public class ViewQuest extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap previewMap;
    TextView questTitleText, questDistanceText, questDescriptionText;
    String title, description, questID;
    int reward;
    double endLat;
    double endLng;
    int repetitive;
    Button completeQuestButton, cancelButton;

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
        questID = getIntent().getStringExtra("questID");
        title = getIntent().getStringExtra("questTitle");
        reward = getIntent().getIntExtra("questReward", 0);
        description = getIntent().getStringExtra("questDescription");
        endLat = getIntent().getDoubleExtra("questEndLat", 0.0);
        endLng = getIntent().getDoubleExtra("questEndLng", 0.0);
        repetitive = getIntent().getIntExtra("questRepetitive", 0);

        questTitleText = findViewById(R.id.textView_questTitlePreview);
        questDistanceText = findViewById(R.id.textView_rewardPreview);
        questDescriptionText = findViewById(R.id.textView_descriptionPreview);
        completeQuestButton = findViewById(R.id.btn_completeQuest);
        cancelButton = findViewById(R.id.btn_cancel);

        questTitleText.setText(title);
        questDistanceText.setText(String.valueOf(reward));
        questDescriptionText.setText(description);

        completeQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewQuest.this, QuestCompletion.class);
                intent.putExtra("questID", questID);
                intent.putExtra("questReward", reward);
                intent.putExtra("endLat", endLat);
                intent.putExtra("endLng", endLng);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        previewMap = googleMap;

        LatLng questLocation = new LatLng(endLat, endLng);
        previewMap.animateCamera(CameraUpdateFactory.newLatLngZoom(questLocation, 15.0f)); // Low zoom to show a large area
        previewMap.addMarker(new MarkerOptions().position(questLocation).title("Quest Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.quest_markeronmap)));


    }


}