package com.example.quick_the_conference;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class verification extends AppCompatActivity {
    Button loginBtn, resendBtn;
    FirebaseAuth auth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        auth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.loginBtn2);
        resendBtn =findViewById(R.id.resend);
        if (!auth.getCurrentUser().isEmailVerified())
        {
            resendBtn.setOnClickListener(v -> auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    Toast.makeText(verification.this, "Email sent! Please check your email for verification.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(verification.this, task1.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }));
        }
        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(verification.this, LoginActivity.class));
            }
        });


    }
}
