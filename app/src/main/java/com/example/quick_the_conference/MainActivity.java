package com.example.quick_the_conference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private static  int SPLASH_TIME_OUT=3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new  Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences=getSharedPreferences(LoginActivity.PREFS_NAME,0);
                boolean hasLoggedIn =sharedPreferences.getBoolean("hasLoggedIn",false);
                if(hasLoggedIn) {
                Intent intent=new Intent(MainActivity.this,DashboardActivity.class);
                startActivity(intent);
                finish();
                }
                else {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                }
            }
        },SPLASH_TIME_OUT);

    }
}