package com.wktnirmal.myquest.Settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wktnirmal.myquest.R;
import com.wktnirmal.myquest.RegisterActivity;

public class AccountSettings extends AppCompatActivity {
    Button cancel,confirm;
    TextView email;
    EditText newUsername, password, newPassword, newPasswordConfirm;
    ProgressBar progressBar;
    FirebaseFirestore database;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //connect firebase
        database = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //connect elements
        cancel = findViewById(R.id.btn_cancelChanges);
        confirm = findViewById(R.id.btn_confirm);
        email = findViewById(R.id.textView_email);
        newUsername = findViewById(R.id.input_newUsername);
        password = findViewById(R.id.input_currentPassword);
        newPassword = findViewById(R.id.input_newPassword);
        newPasswordConfirm = findViewById(R.id.input_newPasswordConfirm);
        progressBar = findViewById(R.id.progressBar_updatingAccountDetails);

        //show current details
        database.collection("Users").document(user.getUid()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        String Un = queryDocumentSnapshots.getString("username");
                        String Em = queryDocumentSnapshots.getString("email");
                        email.setText(Em);
                        newUsername.setHint(Un);
                        }
                });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(false);
                processDetails();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    private void processDetails() {
        progressBar.setVisibility(View.VISIBLE);

        String updatedUsername = this.newUsername.getText().toString().trim();
        String currentPassword = this.password.getText().toString().trim();
        String updatedPassword = this.newPassword.getText().toString().trim();
        String updatedPasswordConfirm = this.newPasswordConfirm.getText().toString().trim();



        //update the username
        if (!updatedUsername.isEmpty()){
            processUsername(updatedUsername);


        }
        //update the password
        else if (!currentPassword.isEmpty() && !updatedPassword.isEmpty() && !updatedPasswordConfirm.isEmpty()){
            if (updatedPassword.equals(updatedPasswordConfirm)){
                updatePassword(currentPassword, updatedPassword);
            }else{
                newPasswordConfirm.setError("Password do not match");
                progressBar.setVisibility(View.INVISIBLE);
                confirm.setEnabled(true);
            }

        }else{
            Toast.makeText(AccountSettings.this, "No changes detected", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            confirm.setEnabled(true);
        }


    }

    private void processUsername(String updatedUsername){
        database.collection("Users").whereEqualTo("username", updatedUsername).get()
                .addOnCompleteListener(task -> {
                    //check if the username already exists
                    if (task.isSuccessful()){
                        if (!task.getResult().isEmpty()){
                            newUsername.setError("Username already exists");
                            progressBar.setVisibility(View.INVISIBLE);
                            confirm.setEnabled(true);
                        } else{
                            updateTheNewUsername(updatedUsername);
                        }
                    }else {
                        // Error checking username
                        Toast.makeText(AccountSettings.this, "Error checking username: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void updatePassword(String currentPassword, String updatedPassword){
        //to change the password successfully
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),currentPassword);

        user.reauthenticate(credential).addOnSuccessListener(aVoid -> {
            user.updatePassword(updatedPassword).addOnSuccessListener(aVoid1 -> {
                Toast.makeText(AccountSettings.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                confirm.setEnabled(true);

            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.INVISIBLE);
                password.setError("Something went wrong");
                confirm.setEnabled(true);
            });
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            password.setError("Wrong password");
            confirm.setEnabled(true);
        });


    }

    private void updateTheNewUsername(String updatedUsername) {
        database.collection("Users").document(user.getUid()).update("username",updatedUsername)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AccountSettings.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        confirm.setEnabled(true);
                    }
                });
    }
}