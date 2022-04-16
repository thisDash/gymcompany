package com.example.gymcompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.gymcompanion.authentication.LoginActivity;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.Exercise;
import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.components.WorkoutLog;
import com.example.gymcompanion.components.WorkoutPlan;
import com.example.gymcompanion.services.InternetManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class InitialScreen extends AppCompatActivity {

    private static final String TAG                    = "InitialScreen";
    private static final String LOGGING_IN             = "Logging in";
    private static final String LOGIN_FAIL             = "One of the parameters Email/Password is wrong. Please try again.";
    private static final String NO_INTERNET_CONNECTION = "No Internet connection detected. Connect to the internet and restart the app.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);

//        changeWorkoutsKey();
//        changePlansKey();
//        changeExercisesKey();

        SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        String email = pref.getString(Constants.PREF_EMAIL, null);
        String password = pref.getString(Constants.PREF_PASSWORD, null);

        if(InternetManager.hasInternet((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE))){
            if (email != null && password != null && !email.isEmpty() && !password.isEmpty()) {
                login(email, password);

            } else {
                Intent intent = new Intent(InitialScreen.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        } else {
            Toast.makeText(InitialScreen.this, NO_INTERNET_CONNECTION, Toast.LENGTH_LONG).show();
        }
    }

    private void login(String email, String password){
        final ProgressDialog pd = new ProgressDialog(InitialScreen.this);
        pd.setMessage(LOGGING_IN);
        pd.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(InitialScreen.this, task -> {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "login: login was successful");
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userId = firebaseUser.getUid();
                        pd.dismiss();

                        getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE)
                                .edit()
                                .putString(Constants.PREF_EMAIL, email)
                                .putString(Constants.PREF_PASSWORD, password)
                                .apply();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_PATH).child(userId);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String userName = dataSnapshot.child(Constants.USERNAME_FIELD).getValue().toString();

                                Intent intent = new Intent(InitialScreen.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Constants.USERNAME_FIELD, userName);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    } else {
                        pd.dismiss();
                        Toast.makeText(InitialScreen.this, LOGIN_FAIL, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void changePlansKey(){

        String userId = FirebaseAuth.getInstance().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.PLANS_PATH)
                .child(userId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.PLANS_PATH)
                .child(Constants.PREMADE_PATH);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snap: snapshot.getChildren()){
                    WorkoutPlan plan = snap.getValue(WorkoutPlan.class);
                    ref.child(plan.getPlanId()).setValue(plan);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeExercisesKey(){

        String userId = FirebaseAuth.getInstance().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUT_LOGS_PATH)
                .child("68RzRVCoUZO2A9XuDFoHsWWkmd82");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUT_LOGS_PATH)
                .child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snap: snapshot.getChildren()){
                    WorkoutLog workLog = snap.getValue(WorkoutLog.class);
                    ref.child(workLog.getWorkoutLogId()).setValue(workLog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeWorkoutsKey(){

        String userId = FirebaseAuth.getInstance().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUTS_PATH)
                .child(userId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUTS_PATH)
                .child(Constants.PREMADE_PATH);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snap: snapshot.getChildren()){
                    Workout workout = snap.getValue(Workout.class);
                    ref.child(workout.getWorkoutId()).setValue(workout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}