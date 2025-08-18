package com.wktnirmal.myquest.Settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wktnirmal.myquest.LoginActivity;
import com.wktnirmal.myquest.R;

public class SettingFragment extends Fragment {
    FirebaseUser user;
    TextView username;
    Button signOut;
    FirebaseFirestore database;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //connect firebase
        database = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //connect elements
        username = view.findViewById(R.id.textView_Username);
        signOut = view.findViewById(R.id.btn_signOut);

        //update username
        database.collection("Users").document(user.getUid())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (queryDocumentSnapshots != null) {
                                        String Un = queryDocumentSnapshots.getString("username");
                                        username.setText(Un);
                                    }
                        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutTheUser();

            }
        });

    }

    private void signOutTheUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out?");
        builder.setPositiveButton("Sign Out", (dialog, which) ->{

            //sign out the user and navigate to the login activity
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //completely clear the nav stack
            startActivity(intent);
            getActivity().finish();

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
        }).show();




    }


}