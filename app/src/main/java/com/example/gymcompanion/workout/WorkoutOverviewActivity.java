package com.example.gymcompanion.workout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.barChart.BarChartBuilder;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.ExerciseDetailed;
import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.workoutLog.WorkoutLogActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WorkoutOverviewActivity extends AppCompatActivity {

    private TextView mWorkoutName;
    private RecyclerView mExercisesList;
    private BarChart mBarChart;
    private Button mStartWorkoutButton;
    private ImageView mCancelButton;

    private FirebaseAuth auth;

    private String workoutId;
    private BarChartBuilder barChartBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_overview);

        Intent intent = getIntent();
        workoutId = intent.getStringExtra(Constants.WORKOUT_ID_FIELD);

        getComponents();
        setButtonListeners();
        getWorkoutInformation();
    }

    private void getComponents(){
        mWorkoutName = findViewById(R.id.workout_overview_workout_name);
        mExercisesList = findViewById(R.id.workout_overview_exercises_list);
        mBarChart = findViewById(R.id.workout_overview_summary_chart);
        mStartWorkoutButton = findViewById(R.id.workout_overview_start_workout_button);
        mCancelButton = findViewById(R.id.workout_overview_cancel_button);
        auth = FirebaseAuth.getInstance();
        barChartBuilder = new BarChartBuilder();
    }

    private void setButtonListeners(){
        mCancelButton.setOnClickListener(v -> finish());
        mStartWorkoutButton.setOnClickListener(v -> startWorkout());
    }

    private void getWorkoutInformation() {
        String userId = auth.getUid();

        barChartBuilder.setupBarChart(getResources(), mBarChart);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUTS_PATH);

        List<ExerciseDetailed> exerciseList = new ArrayList();
        LinearLayoutManager exercisesLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        WorkoutOverviewExercisesListAdapter exercisesAdapter = new WorkoutOverviewExercisesListAdapter(exerciseList, this);
        mExercisesList.setLayoutManager(exercisesLayoutManager);
        mExercisesList.setAdapter(exercisesAdapter);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot snap = dataSnapshot.child(userId);

                if(!snap.hasChild(workoutId))
                    snap = dataSnapshot.child(Constants.PREMADE_PATH);

                Workout workout = snap.child(workoutId).getValue(Workout.class);

                mWorkoutName.setText(workout.getWorkoutName());

                for (ExerciseDetailed exerciseDetailed : workout.getExerciseDetailedList()) {
                    exercisesAdapter.addExerciseToWorkout(exerciseDetailed);
                }

                exercisesAdapter.notifyDataSetChanged();
                barChartBuilder.updateBarChart(getResources(), workout.getSetsPerMuscleGroup(), mBarChart);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startWorkout(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        View popupView = getLayoutInflater().inflate(R.layout.popup_start_workout, null);
        Button yesButton = popupView.findViewById(R.id.popup_start_workout_yes_button);
        Button noButton = popupView.findViewById(R.id.popup_start_workout_no_button);

        dialogBuilder.setView(popupView);
        Dialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        yesButton.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(this, WorkoutLogActivity.class);
            intent.putExtra(Constants.WORKOUT_ID_FIELD, workoutId);
            intent.putExtra(Constants.IS_WORKOUT_FROM_PLAN_FIELD, false);
            startActivity(intent);
            finish();
        });

        noButton.setOnClickListener(view -> dialog.dismiss());
    }
}