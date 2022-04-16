package com.example.gymcompanion.plan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.MainActivity;
import com.example.gymcompanion.R;
import com.example.gymcompanion.barChart.BarChartBuilder;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.components.WorkoutPlan;
import com.example.gymcompanion.components.WorkoutPlanLog;
import com.example.gymcompanion.components.WorkoutPlanWeekLog;
import com.github.mikephil.charting.charts.BarChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlanOverviewActivity extends AppCompatActivity {

    private static final String TAG = "PlanOverviewActivity";

    private static final String YES_TEXT = "YES";
    private static final String NO_TEXT = "NO";
    private static final String PLANS_PATH = "Plans";
    private static final String WORKOUTS_PATH = "Workouts";

    private RecyclerView mWorkoutsList;
    private BarChart mBarChart;
    private TextView mPlanName;
    private TextView mNumberWeeks;
    private TextView mIncludeIntroWeek;
    private TextView mIncludeDeloadWeek;
    private Button mStartPlanButton;
    private ImageView mCancelButton;

    private FirebaseAuth auth;
    private String planId;
    private BarChartBuilder chartBuilder;
    private PlanOverviewWorkoutsListAdapter workoutsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_overview);

        Intent intent = getIntent();
        planId = intent.getStringExtra(Constants.PLAN_ID_FIELD);

        getComponents();
        setButtonListeners();
        getPlanInformation();
        setBarChart();
    }

    private void getComponents(){
        mWorkoutsList = findViewById(R.id.plan_overview_workouts_list);
        mBarChart = findViewById(R.id.plan_overview_summary_chart);
        mPlanName = findViewById(R.id.plan_overview_plan_name);
        mNumberWeeks = findViewById(R.id.plan_overview_number_weeks);
        mIncludeIntroWeek = findViewById(R.id.plan_overview_include_intro_week);
        mIncludeDeloadWeek = findViewById(R.id.plan_overview_include_deload_week);
        mStartPlanButton = findViewById(R.id.plan_overview_start_plan_button);
        mCancelButton = findViewById(R.id.plan_overview_cancel_button);
        auth = FirebaseAuth.getInstance();
        chartBuilder = new BarChartBuilder();
    }

    private void setButtonListeners(){
        mStartPlanButton.setOnClickListener(v -> startNewPlan());
        mCancelButton.setOnClickListener(v -> finish());
    }

    private void getPlanInformation(){
        String userId = auth.getUid();
        Context context = this;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(PLANS_PATH);

        LinearLayoutManager workoutsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mWorkoutsList.setLayoutManager(workoutsLayoutManager);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot snap = dataSnapshot.child(userId);

                if(!snap.hasChild(planId)){
                    snap = dataSnapshot.child(Constants.PREMADE_PATH);
                }

                WorkoutPlan plan = snap.child(planId).getValue(WorkoutPlan.class);

                mPlanName.setText(plan.getPlanName());
                mNumberWeeks.setText(String.valueOf(plan.getNumberWeeks()));

                if(plan.isIncludeDeloadWeek())
                    mIncludeDeloadWeek.setText(YES_TEXT);
                else
                    mIncludeDeloadWeek.setText(NO_TEXT);

                if(plan.isIncludeIntroWeek())
                    mIncludeIntroWeek.setText(YES_TEXT);
                else
                    mIncludeIntroWeek.setText(NO_TEXT);

                getWorkoutsInformation(userId, context, plan);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getWorkoutsInformation(String userId, Context context, WorkoutPlan plan){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUTS_PATH);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Workout> workoutList = new ArrayList();
                DataSnapshot snap;

                for(String workoutId: plan.getWorkoutsIds()) {
                    snap = dataSnapshot.child(userId);

                    if(!snap.hasChild(workoutId)){
                        snap = dataSnapshot.child(Constants.PREMADE_PATH);
                    }

                    Workout workout = snap.child(workoutId).getValue(Workout.class);
                    workoutList.add(workout);
                }

                workoutsAdapter = new PlanOverviewWorkoutsListAdapter(context, workoutList, plan);
                mWorkoutsList.setAdapter(workoutsAdapter);
                Map<String, Integer> setsPerMuscleGroup = getSetsPerMuscleGroup(workoutsAdapter.getWorkoutsInPlan());
                chartBuilder.updateBarChart(getResources(), setsPerMuscleGroup, mBarChart);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Map<String, Integer> getSetsPerMuscleGroup(List<Workout> workoutsInPlan){
        List<String> muscleGroups = new ArrayList();
        Collections.addAll(muscleGroups, getResources().getStringArray(R.array.muscle_groups));

        Map<String, Integer> setsPerMuscle = new HashMap();

        for(String muscleGroup: muscleGroups){
            setsPerMuscle.put(muscleGroup,0);
        }

        for(Workout workout: workoutsInPlan){
            List<Integer> setsPerMuscleGroupWorkout = workout.getSetsPerMuscleGroup();
            int i = 0;

            for(String muscleGroup: muscleGroups){
                setsPerMuscle.put(muscleGroup,setsPerMuscle.get(muscleGroup) + setsPerMuscleGroupWorkout.get(i));
                i++;
            }
        }

        return setsPerMuscle;
    }

    private void setBarChart(){
        chartBuilder.setupBarChart(getResources(), mBarChart);
    }

    private void startNewPlan(){
        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS_PATH)
                .child(userId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentPlanId = snapshot.child(Constants.CURRENT_PLAN_LOG_ID_FIELD).getValue(String.class);

                if(currentPlanId.isEmpty())
                    showNewPlanPopup(false);

                else
                    showNewPlanPopup(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showNewPlanPopup(boolean alreadyHasOnePlan){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        View popupView = null;
        Button yesButton = null;
        Button noButton = null;

        if(alreadyHasOnePlan){
            popupView = getLayoutInflater().inflate(R.layout.popup_swap_plan, null);
            yesButton = popupView.findViewById(R.id.popup_swap_plan_yes_button);
            noButton = popupView.findViewById(R.id.popup_swap_plan_no_button);
        }

        else{
            popupView = getLayoutInflater().inflate(R.layout.popup_start_new_plan, null);
            yesButton = popupView.findViewById(R.id.popup_start_new_plan_yes_button);
            noButton = popupView.findViewById(R.id.popup_start_new_plan_no_button);
        }

        dialogBuilder.setView(popupView);
        Dialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        yesButton.setOnClickListener(view -> {
            dialog.dismiss();
            setupNewPlan(alreadyHasOnePlan);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        noButton.setOnClickListener(view -> dialog.dismiss());
    }

    private void setupNewPlan(boolean alreadyHasPlan){
        String planLogId = UUID.randomUUID().toString();
        List<Workout> workoutList = workoutsAdapter.getWorkoutsInPlan();
        List<String> workoutIds = new ArrayList();
        List<WorkoutPlanWeekLog> weeklyLogsList = new ArrayList();
        WorkoutPlan plan = workoutsAdapter.getPlan();

        int numberWeeks = plan.getNumberWeeks();
        String planName = plan.getPlanName();

        if(plan.isIncludeIntroWeek())
            numberWeeks++;

        if(plan.isIncludeDeloadWeek())
            numberWeeks++;

        for(int i = 0; i < numberWeeks; i++){
            String weeklyLogId = UUID.randomUUID().toString();
            weeklyLogsList.add(new WorkoutPlanWeekLog(weeklyLogId));
        }

        for(Workout workout: workoutList)
            workoutIds.add(workout.getWorkoutId());

        //TODO intro week

        WorkoutPlanLog planLog = new WorkoutPlanLog(planLogId,
                planId,
                planName,
                workoutIds,
                weeklyLogsList,
                plan.isIncludeDeloadWeek());

        String userId = FirebaseAuth.getInstance().getUid();

        if(alreadyHasPlan){
            finishCurrentPlan(userId);
        }

        FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS_PATH)
                .child(userId)
                .child(Constants.CURRENT_PLAN_LOG_ID_FIELD)
                .setValue(planLogId);

        FirebaseDatabase.getInstance().getReference()
                .child(Constants.PLAN_LOGS_PATH)
                .child(userId)
                .child(planLogId)
                .setValue(planLog);
    }

    private void finishCurrentPlan(String userId){

        Log.d(TAG, "finishCurrentPlan: started");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS_PATH)
                .child(userId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentPlanLogId = snapshot.child(Constants.CURRENT_PLAN_LOG_ID_FIELD)
                        .getValue(String.class);

                Log.d(TAG, "onDataChange: currentPlanLogId: " + currentPlanLogId);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.PLAN_LOGS_PATH)
                        .child(userId)
                        .child(currentPlanLogId)
                        .child(Constants.COMPLETION_DATE_CODE_FIELD);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String currentFinishDate = snapshot.getValue(String.class);

                        if(currentFinishDate.isEmpty()){
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                            Date date = new Date();
                            String planDateCode = formatter.format(date);
                            Log.d(TAG, "onDataChange: planDateCode: " + planDateCode);
                            ref.setValue(planDateCode);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}