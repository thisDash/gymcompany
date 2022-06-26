package com.example.gymcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.gymcompanion.authentication.LoginActivity;
import com.example.gymcompanion.services.InternetManager;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeScreen extends AppCompatActivity {

    private static final String NO_INTERNET_CONNECTION = "No Internet connection detected. Connect to the internet and restart the app.";

    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> tryToStartApplication());
    }

    protected void tryToStartApplication(){
        if(InternetManager.hasInternet((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE))){
            FirebaseAuth auth = FirebaseAuth.getInstance();

            if(auth.getCurrentUser() == null) {
                Intent intent = new Intent(WelcomeScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();

            } else {
                Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        } else {
            Toast.makeText(WelcomeScreen.this, NO_INTERNET_CONNECTION, Toast.LENGTH_LONG).show();
        }
    }
}