package com.example.gymcompanion.plan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.workout.CreateWorkoutActivity;
import com.example.gymcompanion.workout.WorkoutNameComparator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkoutSelectionActivity extends AppCompatActivity {

    private static final String TAG = "WorkoutActivity";

    private ImageView mCancelButton;
    private Button mCreateWorkout;
    private Button mAddWorkoutsButton;
    private RecyclerView mWorkoutsList;
    private WorkoutSelectionAdapter workoutsAdapter;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_selection);

        getComponents();
        setButtonListeners();
        setWorkoutsList();
    }

    private void getComponents(){
        mCancelButton = findViewById(R.id.workouts_cancel_button);
        mCreateWorkout = findViewById(R.id.workouts_create_workout_button);
        mAddWorkoutsButton = findViewById(R.id.workouts_add_workouts_button);
        mWorkoutsList = findViewById(R.id.workouts_workouts_list_view);
        auth = FirebaseAuth.getInstance();
    }

    private void setButtonListeners(){
        mCancelButton.setOnClickListener(v -> finish());
        mCreateWorkout.setOnClickListener(v -> goToCreateWorkoutPage());
        mAddWorkoutsButton.setOnClickListener(v -> addWorkouts());
    }

    private void goToCreateWorkoutPage(){
        Intent intent = new Intent(this, CreateWorkoutActivity.class);
        startActivity(intent);
    }

    private void addWorkouts(){
        List<String> workoutsChosen = workoutsAdapter.getChosenWorkouts();
        Log.d(TAG, "addExercises: Adding " + workoutsChosen.size() + " workouts");
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.WORKOUTS_CHOSEN_FIELD,
                workoutsChosen.toArray(new String[workoutsChosen.size()]));

        for(String workoutId: workoutsChosen)
            Log.d(TAG, "addWorkouts: " + workoutId);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void setWorkoutsList(){
        String userId = auth.getUid();
        Context context = this;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUTS_PATH);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Workout> workoutsList = new ArrayList<>();

                DataSnapshot snap = dataSnapshot.child(Constants.PREMADE_PATH);

                for(DataSnapshot s: snap.getChildren()){
                    Workout workout = s.getValue(Workout.class);
                    workoutsList.add(workout);
                }

                snap = dataSnapshot.child(userId);

                for(DataSnapshot s: snap.getChildren()){
                    Workout workout = s.getValue(Workout.class);
                    workoutsList.add(workout);
                }

                LinearLayoutManager muscleGroupsLayoutManager = new LinearLayoutManager(context,
                        LinearLayoutManager.VERTICAL,
                        false);

                Collections.sort(workoutsList, new WorkoutNameComparator());
                workoutsAdapter = new WorkoutSelectionAdapter(context, workoutsList);
                mWorkoutsList.setLayoutManager(muscleGroupsLayoutManager);
                mWorkoutsList.setAdapter(workoutsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}