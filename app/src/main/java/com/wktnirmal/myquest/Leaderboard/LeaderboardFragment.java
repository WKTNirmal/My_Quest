package com.wktnirmal.myquest.Leaderboard;

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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.wktnirmal.myquest.R;

import java.util.ArrayList;
import java.util.List;


public class LeaderboardFragment extends Fragment {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    List<UserEntry> leaderboardList = new ArrayList<>();
    RecyclerView recyclerView;
    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        //connect elements
        recyclerView = view.findViewById(R.id.leaderboardRecyclerView);
        progressBar = view.findViewById(R.id.progressBar_Leaderboard);

        //clear the quest list everytime the fragment is created
        leaderboardList.clear();

        // check if recyclerView is not null before using it
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            // Fetch from Firestore
            database.collection("Users")
                    .orderBy("userXpAmount", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        progressBar.setVisibility(View.GONE);
                        int placement =1;
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            String username = doc.getString("username");
                            Long xp = doc.getLong("userXpAmount");
                            if (xp != 0) {
                                leaderboardList.add(new UserEntry(username, xp.intValue(), placement));
                                placement++;
                            }

                        }

                        LeaderboardAdapter adapter = new LeaderboardAdapter(leaderboardList);
                        recyclerView.setAdapter(adapter);
                    });







        }
    }
}