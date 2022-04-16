package com.example.gymcompanion.workoutSummary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.MainActivity;
import com.example.gymcompanion.R;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.ExerciseDetailed;
import com.example.gymcompanion.components.ExerciseLog;
import com.example.gymcompanion.components.SetLog;
import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.components.WorkoutLog;
import com.example.gymcompanion.components.WorkoutPlanLog;
import com.example.gymcompanion.components.WorkoutPlanWeekLog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class WorkoutSummaryActivity extends AppCompatActivity {

    private static final String TAG = "WorkoutSummary";

    private Button mGoHomeButton;
    private TextView mWorkoutLength;
    private TextView mNumberSets;
    private TextView mNumberReps;
    private TextView mTotalWeight;
    private TextView mVolumeChange;
    private RecyclerView mExerciseLogs;
    private LinearLayout mComparationLayout;

    private String workoutLogId;
    private String currentPlanLogId;
    private boolean isWorkoutFromPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_summary);

        Intent intent = getIntent();
        workoutLogId = intent.getStringExtra(Constants.WORKOUT_LOG_ID_FIELD);
        isWorkoutFromPlan = intent.getBooleanExtra(Constants.IS_WORKOUT_FROM_PLAN_FIELD, false);

        if(isWorkoutFromPlan){
            currentPlanLogId = intent.getStringExtra(Constants.CURRENT_PLAN_LOG_ID_FIELD);
        }

        getComponents();
        setButtonListeners();
        getWorkoutLogInfo();
    }

    private void getComponents(){
        mGoHomeButton = findViewById(R.id.workout_summary_home_button);
        mWorkoutLength = findViewById(R.id.workout_summary_workout_time);
        mNumberSets = findViewById(R.id.workout_summary_number_sets);
        mNumberReps = findViewById(R.id.workout_summary_number_reps);
        mTotalWeight = findViewById(R.id.workout_summary_weight_lifted);
        mExerciseLogs = findViewById(R.id.workout_summary_exercises_log);
        mVolumeChange = findViewById(R.id.workout_summary_volume_change);
        mComparationLayout = findViewById(R.id.workout_summary_last_week_comparation_layout);
    }

    private void setButtonListeners(){
        mGoHomeButton.setOnClickListener(v -> goToHomeScreen());
    }

    private void getWorkoutLogInfo(){
        String userId = FirebaseAuth.getInstance().getUid();
        Context context = this;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUT_LOGS_PATH)
                .child(userId)
                .child(workoutLogId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WorkoutLog workoutLog = snapshot.getValue(WorkoutLog.class);
                int numberSets = 0;
                int numberReps = 0;
                int weightLifted = 0;

                for(ExerciseLog log: workoutLog.getExercisesLogList()) {

                    for(SetLog setLog: log.getSetsList()){
                        int reps = setLog.getNumberReps();
                        int weight = (int)(setLog.getNumberReps() * setLog.getWeightUsed());

                        numberReps += reps;
                        weightLifted += weight;

                        if(reps != 0 && weight != 0)
                            numberSets++;
                    }
                }

                if(isWorkoutFromPlan)
                    compareWithLastWeek(weightLifted, userId);

                else
                    hideComparationLayout();

                mWorkoutLength.setText(workoutLog.getTimeElapsed());
                mNumberReps.setText(String.valueOf(numberReps));
                mNumberSets.setText(String.valueOf(numberSets));
                mTotalWeight.setText(String.valueOf(weightLifted));

                setupExerciseLogsList(workoutLog, context);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void compareWithLastWeek(int workoutVolume, String userId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.PLAN_LOGS_PATH)
                .child(userId)
                .child(currentPlanLogId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WorkoutPlanLog planLog = snapshot.getValue(WorkoutPlanLog.class);

                int currentWeek = planLog.getCurrentWeek();
                int currentDay = planLog.getCurrentDay();

                if(currentWeek == 0)
                    hideComparationLayout();

                else{
                    WorkoutPlanWeekLog pastWeekLog = planLog.getWeeklyLogsList().get(currentWeek - 1);
                    String previousWorkoutLog = pastWeekLog.getWorkoutLogsIdsList().get(currentDay);
                    getPreviousWeekLog(previousWorkoutLog, userId, workoutVolume);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPreviousWeekLog(String workoutLogId, String userId, int currentWorkoutVolume){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUT_LOGS_PATH)
                .child(userId)
                .child(workoutLogId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WorkoutLog log = snapshot.getValue(WorkoutLog.class);
                int prevVolume = log.getTotalVolume();

                Log.d(TAG, "onDataChange: prevLog: " + workoutLogId);
                Log.d(TAG, "onDataChange: prevVol: " + prevVolume);
                Log.d(TAG, "onDataChange: currVol: " + currentWorkoutVolume);

                double change = (((double)currentWorkoutVolume / prevVolume) * 100.0) - 100.0;
                Log.d(TAG, "onDataChange: change: " + change);
                double first = ((double)currentWorkoutVolume / prevVolume);
                Log.d(TAG, "onDataChange: 1: " + first);
                double second = first * 100.0;
                Log.d(TAG, "onDataChange: 2: " + second);

                DecimalFormat df = new DecimalFormat("#.##");
                String formatted = df.format(change);
                Log.d(TAG, "onDataChange: formatted: " + formatted);
                String volumeChange = formatted + "%";
                mVolumeChange.setText(volumeChange);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void hideComparationLayout(){
        mComparationLayout.setVisibility(View.GONE);
    }

    private void setupExerciseLogsList(WorkoutLog workoutLog, Context context){
        String userId = FirebaseAuth.getInstance().getUid();
        String workoutId = workoutLog.getWorkoutId();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUTS_PATH);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot snap = dataSnapshot.child(userId);

                if(!snap.hasChild(workoutId)){
                    snap = dataSnapshot.child(Constants.PREMADE_PATH);
                }
                Workout workout = snap.child(workoutId).getValue(Workout.class);

                List<ExerciseDetailed> exerciseList = workout.getExerciseDetailedList();
                List<ExerciseLog> exerciseLogs = workoutLog.getExercisesLogList();

                for(ExerciseDetailed exerciseDetailed: exerciseList)
                    exerciseDetailed.setExpandable(true);

                LinearLayoutManager exercisesLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                WorkoutSummaryAdapter workoutSummaryAdapter = new WorkoutSummaryAdapter(context, exerciseList, exerciseLogs);
                mExerciseLogs.setLayoutManager(exercisesLayoutManager);
                mExerciseLogs.setAdapter(workoutSummaryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void goToHomeScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}