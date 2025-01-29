package com.example.quick_the_conference;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    FirebaseAuth auth;
    EditText emailBox, passwordBox, namebox;
    Button loginBtn, signupBtn;

    FirebaseFirestore database;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account");

        namebox = findViewById(R.id.nameBox);
        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);

        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.createBtn);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email, pass, name;
                email = emailBox.getText().toString().trim();
                pass = passwordBox.getText().toString().trim();
                name = namebox.getText().toString().trim();


                if (email.length() > 0 && pass.length() > 0 && name.length() > 0) {
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener( task1 -> {
                                    Toast.makeText(SignupActivity.this, "Registered successfully ! Please check your email for verification.", Toast.LENGTH_LONG).show();
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("Name", name);
                                    user.put("Email", email);
                                    // Add a new document with a generated ID
                                    database.collection("Users")
                                            .add(user)
                                            .addOnSuccessListener(documentReference -> Toast.makeText(SignupActivity.this, "Data stored Successfully", Toast.LENGTH_LONG).show()
                                            )
                                            .addOnFailureListener(e -> Toast.makeText(SignupActivity.this, "Error Data not stored due to - " + e.getMessage(), Toast.LENGTH_LONG).show());

                                });

                            } else {
                                Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                else
                {
                    Toast.makeText(SignupActivity.this, "Above fields can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });


    }
}
