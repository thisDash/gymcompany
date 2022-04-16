package com.example.gymcompanion.workoutLog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
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
import com.example.gymcompanion.workoutSummary.WorkoutSummaryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkoutLogActivity extends AppCompatActivity {

    private static final String TAG = "WorkoutLogActivity";

    private Chronometer mChronometer;
    private Chronometer mWorkoutTimeElapsed;
    private ImageView mPlayPauseChronometer;
    private ImageView mRestartChronometer;
    private ImageView mCancelWorkoutButton;
    private TextView mWorkoutName;
    private RecyclerView mExercisesListView;
    private Button mFinishButton;

    private boolean isChronometerRunning;
    private boolean isWorkoutFromPlan;
    private long pauseOffset;
    private String workoutId;
    private String currentPlanId;
    private WorkoutLogAdapter workoutLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_log);
        isChronometerRunning = false;

        Intent intent = getIntent();
        workoutId = intent.getStringExtra(Constants.WORKOUT_ID_FIELD);
        isWorkoutFromPlan = intent.getBooleanExtra(Constants.IS_WORKOUT_FROM_PLAN_FIELD, false);

        if(isWorkoutFromPlan)
            currentPlanId = intent.getStringExtra(Constants.CURRENT_PLAN_LOG_ID_FIELD);

        getComponents();
        setButtonListeners();
        setChronometer();
        getWorkoutInformation();
    }

    @Override
    public void onBackPressed() {
        showCancelWorkoutPopup();
    }

    private void getComponents(){
        mChronometer = findViewById(R.id.workout_log_workout_chronometer);
        mWorkoutTimeElapsed = findViewById(R.id.workout_log_workout_time);
        mPlayPauseChronometer = findViewById(R.id.workout_log_start_chronometer);
        mRestartChronometer = findViewById(R.id.workout_log_restart_chronometer);
        mCancelWorkoutButton = findViewById(R.id.workout_log_cancel_button);
        mWorkoutName = findViewById(R.id.workout_log_workout_name);
        mExercisesListView = findViewById(R.id.workout_log_exercises_list);
        mFinishButton = findViewById(R.id.workout_log_finish_button);
    }

    private void setButtonListeners(){
        mPlayPauseChronometer.setOnClickListener(v -> startPauseChronometer());
        mRestartChronometer.setOnClickListener(v -> restartChronometer());
        mCancelWorkoutButton.setOnClickListener(v -> showCancelWorkoutPopup());
        mFinishButton.setOnClickListener(v -> openFinishWorkoutPopup());
    }

    private void setChronometer(){
        mWorkoutTimeElapsed.setOnChronometerTickListener(cArg -> {
            long time = SystemClock.elapsedRealtime() - cArg.getBase();
            int h = (int)(time / 3600000);
            int m = (int)(time - h * 3600000) / 60000;
            int s = (int)(time - h * 3600000 - m * 60000) / 1000 ;
            String hh = h < 10 ? "0" + h : h + "";
            String mm = m < 10 ? "0" + m : m + "";
            String ss = s < 10 ? "0" + s : s + "";
            String textTime = hh + ":" + mm + ":" + ss;
            cArg.setText(textTime);
        });

        mWorkoutTimeElapsed.setBase(SystemClock.elapsedRealtime());
        mWorkoutTimeElapsed.start();
    }

    private void startPauseChronometer(){
        if(!isChronometerRunning) {
            mChronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            mChronometer.start();
            isChronometerRunning = true;
            mPlayPauseChronometer.setImageResource(R.drawable.pause);
        }

        else{
            mChronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - mChronometer.getBase();
            isChronometerRunning = false;
            mPlayPauseChronometer.setImageResource(R.drawable.play);
        }
    }

    private void restartChronometer(){
        mChronometer.stop();
        mChronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        isChronometerRunning = false;
        mPlayPauseChronometer.setImageResource(R.drawable.play);
    }

    private void showCancelWorkoutPopup(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        View popupView = getLayoutInflater().inflate(R.layout.popup_cancel_workout, null);
        Button yesButton = popupView.findViewById(R.id.popup_cancel_workout_yes_button);
        Button noButton = popupView.findViewById(R.id.popup_cancel_workout_no_button);

        dialogBuilder.setView(popupView);
        Dialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        yesButton.setOnClickListener(view -> {
            dialog.dismiss();
            finish();
        });

        noButton.setOnClickListener(view -> dialog.dismiss());
    }

    private void getWorkoutInformation() {
        String userId = FirebaseAuth.getInstance().getUid();
        Context context = this;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUTS_PATH);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot snap = dataSnapshot.child(userId);

                if(!snap.hasChild(workoutId)){
                    snap = dataSnapshot.child(Constants.PREMADE_PATH);
                }

                Workout workout = snap.child(workoutId).getValue(Workout.class);

                mWorkoutName.setText(workout.getWorkoutName());

                List<ExerciseDetailed> exerciseList = workout.getExerciseDetailedList();
                List<ExerciseLog> exerciseLogs = new ArrayList();

                for (ExerciseDetailed exerciseDetailed : exerciseList) {
                    List<SetLog> setLogs = new ArrayList();
                    String exerciseId = exerciseDetailed.getExerciseId();

                    for (int i = 0; i < Integer.parseInt(exerciseDetailed.getNumberSets()); i++) {
                        setLogs.add(new SetLog(i + 1));
                    }

                    ExerciseLog exerciseLog = new ExerciseLog(exerciseId,
                            setLogs,
                            exerciseDetailed.getName());

                    exerciseLogs.add(exerciseLog);
                }

                if(isWorkoutFromPlan) {
                    getPlanInformation(context, exerciseList, exerciseLogs);

                } else {
                    LinearLayoutManager exercisesLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    workoutLogAdapter = new WorkoutLogAdapter(context, exerciseList, exerciseLogs);
                    mExercisesListView.setLayoutManager(exercisesLayoutManager);
                    mExercisesListView.setAdapter(workoutLogAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getPlanInformation(Context context, List<ExerciseDetailed> exerciseList,
                                                 List<ExerciseLog> exerciseLogs){
        String userId = FirebaseAuth.getInstance().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.PLAN_LOGS_PATH)
                .child(userId)
                .child(currentPlanId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WorkoutPlanLog currentPlan = snapshot.getValue(WorkoutPlanLog.class);
                int currentWeek = currentPlan.getCurrentWeek();
                int currentDay = currentPlan.getCurrentDay();

                if(currentWeek == 0){
                    LinearLayoutManager exercisesLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    workoutLogAdapter = new WorkoutLogAdapter(context, exerciseList, exerciseLogs);
                    mExercisesListView.setLayoutManager(exercisesLayoutManager);
                    mExercisesListView.setAdapter(workoutLogAdapter);

                } else {
                    WorkoutPlanWeekLog prevWeekLog = currentPlan.getWeeklyLogsList().get(currentWeek - 1);
                    String previousWorkoutLogId = prevWeekLog.getWorkoutLogsIdsList().get(currentDay);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.WORKOUT_LOGS_PATH)
                            .child(userId)
                            .child(previousWorkoutLogId);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            WorkoutLog prevWorkoutLog = snapshot.getValue(WorkoutLog.class);

                            LinearLayoutManager exercisesLayoutManager = new LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL,
                                    false);
                            workoutLogAdapter = new WorkoutLogAdapter(context,
                                    exerciseList,
                                    exerciseLogs,
                                    currentPlan,
                                    prevWorkoutLog);

                            mExercisesListView.setLayoutManager(exercisesLayoutManager);
                            mExercisesListView.setAdapter(workoutLogAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openFinishWorkoutPopup(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        View popupView = getLayoutInflater().inflate(R.layout.popup_finish_workout, null);
        Button yesButton = popupView.findViewById(R.id.popup_finish_workout_yes_button);
        Button noButton = popupView.findViewById(R.id.popup_finish_workout_no_button);

        dialogBuilder.setView(popupView);
        Dialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        yesButton.setOnClickListener(view -> {
            dialog.dismiss();
            checkWorkoutLog();
        });

        noButton.setOnClickListener(view -> dialog.dismiss());
    }

    private void checkWorkoutLog() {
        boolean canFinish = true;
        float volume = 0;

        for (ExerciseLog log : workoutLogAdapter.getWorkoutLog()) {
            for (SetLog set : log.getSetsList()) {
                if (set.getNumberReps() == 0 || set.getWeightUsed() == 0)
                    canFinish = false;

                volume += set.getNumberReps() * set.getWeightUsed();
            }
        }

        final int totalVolume = (int) volume;

        if (!canFinish) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

            View popupView = getLayoutInflater().inflate(R.layout.popup_empty_workout_fields, null);
            Button yesButton = popupView.findViewById(R.id.popup_empty_workout_fields_yes_button);
            Button noButton = popupView.findViewById(R.id.popup_empty_workout_fields_no_button);

            dialogBuilder.setView(popupView);
            Dialog dialog = dialogBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            yesButton.setOnClickListener(view -> {
                dialog.dismiss();
                finishWorkout(totalVolume);
            });

            noButton.setOnClickListener(view -> dialog.dismiss());
        }

        else
            finishWorkout(totalVolume);
    }

    private void finishWorkout(int totalVolume){
        String userId = FirebaseAuth.getInstance().getUid();
        Context context = this;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUTS_PATH);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot snap = dataSnapshot.child(userId);

                if(!snap.hasChild(workoutId)){
                    snap = dataSnapshot.child(Constants.PREMADE_PATH);
                }

                Workout workout = snap.child(workoutId).getValue(Workout.class);

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String dateToSplit = formatter.format(date);

                String datePart = dateToSplit.substring(0, dateToSplit.indexOf(" "));
                String timePart = dateToSplit.substring(dateToSplit.indexOf(" ") + 1);

                String finalDate = datePart + " at " + timePart;

                formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                date = new Date();
                String workoutLogId = formatter.format(date);

                String timeElapsed = getTimeElapsed();

                WorkoutLog workoutLog = new WorkoutLog(workoutLogId,
                        workout.getWorkoutId(),
                        workoutLogAdapter.getWorkoutLog(),
                        finalDate,
                        workout.getWorkoutName(),
                        totalVolume,
                        timeElapsed);

                DatabaseReference reference = FirebaseDatabase.getInstance()
                        .getReference()
                        .child(Constants.WORKOUT_LOGS_PATH)
                        .child(userId)
                        .child(workoutLogId);

                reference.setValue(workoutLog);

                Intent intent = new Intent(context, WorkoutSummaryActivity.class);
                intent.putExtra(Constants.IS_WORKOUT_FROM_PLAN_FIELD, isWorkoutFromPlan);

                if(isWorkoutFromPlan){
                    updateWorkoutPlanLog(userId, workoutLogId, totalVolume);
                    intent.putExtra(Constants.CURRENT_PLAN_LOG_ID_FIELD, currentPlanId);
                }

                intent.putExtra(Constants.WORKOUT_LOG_ID_FIELD, workoutLogId);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateWorkoutPlanLog(String userId, String workoutLogId, int workoutVolume){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.PLAN_LOGS_PATH)
                .child(userId)
                .child(currentPlanId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WorkoutPlanLog planLog = snapshot.getValue(WorkoutPlanLog.class);
                int currentWeek = planLog.getCurrentWeek();
                int currentDay = planLog.getCurrentDay();

                WorkoutPlanWeekLog weekLog = planLog.getWeeklyLogsList().get(currentWeek);
                List<String> workoutLogIds = weekLog.getWorkoutLogsIdsList();

                if(workoutLogIds == null)
                    workoutLogIds = new ArrayList();

                workoutLogIds.add(workoutLogId);
                weekLog.setWorkoutLogsIdsList(workoutLogIds);
                weekLog.setWeeklyVolume(weekLog.getWeeklyVolume() + workoutVolume);
                planLog.setWorkoutsToGo(planLog.getWorkoutsToGo() - 1);

                if(currentDay == planLog.getNumberDays() - 1){

                    if(planLog.isIncludesDeloadWeek() && currentWeek == planLog.getNumberWeeks() - 2){
                        //TODO prepare deload week
                    }

                    if(currentWeek == planLog.getNumberWeeks() - 1){
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = new Date();
                        String planDateCode = formatter.format(date);
                        planLog.setCompletionDateCode(planDateCode);
                    }

                    else{
                        planLog.setCurrentDay(0);
                        planLog.setCurrentWeek(currentWeek + 1);
                    }
                }

                else
                    planLog.setCurrentDay(currentDay + 1);

                databaseReference.setValue(planLog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getTimeElapsed(){
        String timeElapsed = "";
        int totalSeconds = (int) ((SystemClock.elapsedRealtime() - mWorkoutTimeElapsed.getBase()) / (1000));
        int totalMinutes = totalSeconds / 60;
        int hours = totalMinutes / 60;
        int minutes = totalMinutes - 60 * hours;
        int seconds = totalSeconds % 60;

        if(hours < 10)
            timeElapsed += "0";

        timeElapsed += hours + "h:";

        if(minutes < 10)
            timeElapsed += "0";

        timeElapsed += minutes + "m:";

        if(seconds < 10)
            timeElapsed += "0";

        timeElapsed += seconds + "s";

        Log.d(TAG, "onDataChange: timeElapsed: " + timeElapsed);

        return timeElapsed;
    }
}