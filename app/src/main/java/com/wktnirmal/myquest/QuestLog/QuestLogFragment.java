package com.wktnirmal.myquest.QuestLog;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wktnirmal.myquest.R;


public class QuestLogFragment extends Fragment {



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
}