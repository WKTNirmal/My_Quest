package com.wktnirmal.myquest.QuestLog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wktnirmal.myquest.Quest;
import com.wktnirmal.myquest.R;

import java.util.ArrayList;
import java.util.List;


public class QuestLogFragment extends Fragment {

    private RecyclerView recyclerView; // Declare RecyclerView
    FirebaseFirestore databaseQuests = FirebaseFirestore.getInstance();  //firestore initialization
    List<Quest> questList = new ArrayList<>();
    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quest_log, container, false);
        //return inflater.inflate(R.layout.fragment_quest_log, container, false);

        //handles the create quest button
        FloatingActionButton toCreateQuestButton = view.findViewById(R.id.btn_toCreateQuest);
        toCreateQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateQuest.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ProgressBar
        progressBar = view.findViewById(R.id.progressBar_questLog);
        progressBar.setVisibility(View.VISIBLE);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.questListRecyclerView);

        //clear the quest list everytime the fragment is created
        questList.clear();

        // check if recyclerView is not null before using it
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            // Fetch from Firestore
            databaseQuests.collection("Quests").whereEqualTo("questStatus", "1")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        progressBar.setVisibility(View.GONE);
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Quest quest = doc.toObject(Quest.class);
                            if (quest != null) {
                                questList.add(quest);
                            }
                        }
                        QuestLogAdapter adapter = new QuestLogAdapter(questList, getContext());
                        recyclerView.setAdapter(adapter);
                    });

            // Example:
            // QuestAdapter adapter = new QuestAdapter(questList);
            // recyclerView.setAdapter(adapter);
        }
    }
}