package com.wktnirmal.myquest;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    Button loginButton;
    Button gotoRegisterButton;
    EditText loginEmail;
    EditText loginPassword ;
    ProgressBar progressBar;
    boolean accountJustRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //connect elements
        loginButton = findViewById(R.id.btn_login);
        gotoRegisterButton = findViewById(R.id.btn_register);
        loginEmail = findViewById(R.id.input_regEmail);
        loginPassword = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.progressBar_login);
        //check if the user just registered
        accountJustRegistered = getIntent().getBooleanExtra("Account_Just_Registered", false);

        //firebase connect
        fAuth = FirebaseAuth.getInstance();


        if (accountJustRegistered == true){
            Toast.makeText(LoginActivity.this, "Account created successfully. Please Log in", Toast.LENGTH_SHORT).show();
        } else {
            //check if user is already logged in
            if(fAuth.getCurrentUser() != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }

        }

        loginUser();

        //navigate to the register activity
        gotoRegisterButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

    }

    public void loginUser(){
        loginButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                if (email.isEmpty()){
                    loginEmail.setError("Email is required");
                    loginEmail.requestFocus();
                    return;
                }
//                if (username.isEmpty()){
//                    loginUsername.setError("Username is required");
//                    loginUsername.requestFocus();
//                    return;
//                }
                if (password.isEmpty()){
                    loginPassword.setError("Password is required");
                    loginPassword.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate the user
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });




//                Intent intent = new Intent(LoginActivity.this, MainActivity.class); //this was for testing TODO remove this
//                startActivity(intent);
//                finish();
            }
        });



    }

}