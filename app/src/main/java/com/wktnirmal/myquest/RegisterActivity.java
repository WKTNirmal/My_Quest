package com.wktnirmal.myquest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    Button registerButton;
    EditText registerEmail;
    EditText registerUsername;
    EditText registerPassword;
    EditText registerConfirmPassword;
    String userID;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    int userXpAmount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //connect the elements
        registerButton = findViewById(R.id.btn_register);
        registerEmail = findViewById(R.id.input_regEmail);
        registerUsername = findViewById(R.id.input_username);
        registerPassword = findViewById(R.id.input_password);
        registerConfirmPassword = findViewById(R.id.input_reenterPassword);
        progressBar = findViewById(R.id.progressBar_register);

        //firebase connect
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

//        //check if the user is already logged in
//        if (fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//        }


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registerEmail.getText().toString().trim();
                String username = registerUsername.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();
                String confirmPassword = registerConfirmPassword.getText().toString().trim();

                if (email.isEmpty()){
                    registerEmail.setError("Email is required");
                    registerEmail.requestFocus();
                    return;
                }
                if (username.isEmpty()){
                    registerUsername.setError("Username is required");
                    registerUsername.requestFocus();
                    return;
                }
                if (password.isEmpty()){
                    registerPassword.setError("Password is required");
                    registerPassword.requestFocus();
                    return;
                }
                if (confirmPassword.isEmpty()){
                    registerConfirmPassword.setError("Confirm password is required");
                    registerConfirmPassword.requestFocus();
                    return;
                }
                if (!password.equals(confirmPassword)){
                    registerConfirmPassword.setError("Passwords do not match");
                    registerConfirmPassword.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //register the user
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Toast.makeText(RegisterActivity.this, "User has been registered successfully", Toast.LENGTH_SHORT).show();

                            //add user data to the account data
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", username);
                            user.put("email", email);
                            user.put("userXpAmount", userXpAmount);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG", "onSuccess: user profile data added for " + userID);

                                    //redirect to the login screen
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    intent.putExtra("Account_Just_Registered", true);
                                    startActivity(intent);
                                    finish();
                                }
                            });



                        }else{
                            Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });


    }
}