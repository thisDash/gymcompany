package com.example.gymcompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gymcompanion.authentication.LoginActivity;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private static String TAG = "ProfileActivity";

    private ImageView backButton;
    private ConstraintLayout profilePhotoButton;
    private TextView userName;
    private TextView gender;
    private TextView birthDate;
    private TextView height;
    private TextView weight;
    private LinearLayout genderButton;
    private LinearLayout birthButton;
    private LinearLayout weightButton;
    private LinearLayout heightButton;
    private LinearLayout settingsButton;
    private LinearLayout logoutButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getComponents();
        getUserInfo();
        setButtonListeners();
    }

    private void getComponents(){
        backButton = findViewById(R.id.profile_back_button);
        profilePhotoButton = findViewById(R.id.profile_photo_button);
        userName = findViewById(R.id.profile_name);
        gender = findViewById(R.id.profile_gender);
        birthDate = findViewById(R.id.profile_birth);
        height = findViewById(R.id.profile_height);
        weight = findViewById(R.id.profile_weight);
        genderButton = findViewById(R.id.profile_gender_layout);
        birthButton = findViewById(R.id.profile_birth_layout);
        weightButton = findViewById(R.id.profile_weight_layout);
        heightButton = findViewById(R.id.profile_height_layout);
        settingsButton = findViewById(R.id.profile_settings_layout);
        logoutButton = findViewById(R.id.profile_logout_layout);

        auth = FirebaseAuth.getInstance();
    }

    private void getUserInfo(){

    }

    private void setButtonListeners(){
        backButton.setOnClickListener(v -> finish());
        profilePhotoButton.setOnClickListener(v -> selectNewPhoto());
        genderButton.setOnClickListener(v -> changeGender());
        birthButton.setOnClickListener(v -> changeBirth());
        weightButton.setOnClickListener(v -> changeWeight());
        heightButton.setOnClickListener(v -> changeHeight());
        settingsButton.setOnClickListener(v -> goToSettingsPage());
        logoutButton.setOnClickListener(v -> logout());
    }

    private void selectNewPhoto(){

    }

    private void changeGender(){

    }

    private void changeBirth(){

    }

    private void changeWeight(){

    }

    private void changeHeight(){

    }

    private void goToSettingsPage(){
        //TODO
        // Intent intent = new Intent(this, SettingsActivity.class);
        // startActivity(intent);
    }

    private void logout(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        View popupView = getLayoutInflater().inflate(R.layout.popup_logout, null);
        Button yesButton = popupView.findViewById(R.id.popup_logout_yes_button);
        Button noButton = popupView.findViewById(R.id.popup_logout_no_button);

        dialogBuilder.setView(popupView);
        Dialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        yesButton.setOnClickListener(view -> {
            dialog.dismiss();
            auth.signOut();

            GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);

            if(googleAccount != null) {
                GoogleSignInOptions googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                GoogleSignInClient googleClient = GoogleSignIn.getClient(this, googleOptions);
                googleClient.signOut();
                Log.d(TAG, "Logged out from google");

            } else {
                AccessToken token = AccessToken.getCurrentAccessToken();

                if(token != null) {
                    AccessToken.setCurrentAccessToken(null);
                    Log.d(TAG, "Logged out from facebook");
                }
            }

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        noButton.setOnClickListener(view -> dialog.dismiss());
    }
}