package com.example.gymcompanion.plan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.barChart.BarChartBuilder;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.components.WorkoutPlanLog;
import com.example.gymcompanion.components.WorkoutPlanWeekLog;
import com.example.gymcompanion.workoutLog.WorkoutLogActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentPlanActivity extends AppCompatActivity {

    private static final Float GRAPH_DATA_SIZE = 12f;

    private TextView mPlanName;
    private TextView mCurrentDay;
    private TextView mNumberDays;
    private TextView mCurrentWeek;
    private TextView mNumberWeeks;
    private ImageView mCancelButton;
    private RecyclerView mWorkoutsList;
    private BarChart mSetsPerMuscleGroupChart;
    private LineChart mVolumeChart;
    private Button mStartNextWorkout;
    private CardView mDaysCard;
    private CardView mWeeksCard;

    private CurrentPlanWorkoutsListAdapter workoutsAdapter;
    private BarChartBuilder chartBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_plan);

        getComponents();
        setButtonsListeners();
        getPlanInfo();
        setupSummaryChart();
    }

    private void getComponents(){
        mCancelButton = findViewById(R.id.current_plan_overview_cancel_button);
        mWorkoutsList = findViewById(R.id.current_plan_overview_workouts_list);
        mSetsPerMuscleGroupChart = findViewById(R.id.current_plan_overview_summary_chart);
        mStartNextWorkout = findViewById(R.id.current_plan_overview_start_next_workout_button);
        mVolumeChart = findViewById(R.id.current_plan_overview_volume_chart);
        mPlanName = findViewById(R.id.current_plan_overview_plan_name);
        mCurrentDay = findViewById(R.id.current_plan_overview_current_day);
        mNumberDays = findViewById(R.id.current_plan_overview_number_days);
        mCurrentWeek = findViewById(R.id.current_plan_overview_current_week);
        mNumberWeeks = findViewById(R.id.current_plan_overview_number_weeks);
        mDaysCard = findViewById(R.id.current_plan_overview_days_card);
        mWeeksCard = findViewById(R.id.current_plan_overview_weeks_card);
        chartBuilder = new BarChartBuilder();
    }

    private void setButtonsListeners(){
        mCancelButton.setOnClickListener(v -> finish());
        mStartNextWorkout.setOnClickListener(v -> startNextWorkout());
    }

    private void getPlanInfo(){
        String userId = FirebaseAuth.getInstance().getUid();
        Context context = this;

        mDaysCard.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));
        mWeeksCard.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

        LinearLayoutManager workoutsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mWorkoutsList.setLayoutManager(workoutsLayoutManager);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS_PATH)
                .child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String planLogId = snapshot
                        .child(Constants.CURRENT_PLAN_LOG_ID_FIELD)
                        .getValue(String.class);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.PLAN_LOGS_PATH)
                        .child(userId)
                        .child(planLogId);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        WorkoutPlanLog planLog = snapshot.getValue(WorkoutPlanLog.class);

                        setupVolumeChart(planLog);
                        mPlanName.setText(planLog.getPlanName());

                        int currentDay = (planLog.getNumberWeeks() * planLog.getNumberDays()) -
                                planLog.getWorkoutsToGo();

                        if(planLog.getCompletionDateCode().isEmpty())
                            mCurrentWeek.setText(String.valueOf(planLog.getCurrentWeek()));

                        else
                            mCurrentWeek.setText(String.valueOf(planLog.getNumberWeeks()));

                        mCurrentDay.setText(String.valueOf(currentDay));
                        mNumberDays.setText(String.valueOf((planLog.getNumberWeeks() * planLog.getNumberDays())));
                        mNumberWeeks.setText(String.valueOf(planLog.getNumberWeeks()));

                        if(!planLog.getCompletionDateCode().isEmpty())
                            mStartNextWorkout.setVisibility(View.GONE);

                        getWorkoutsInfo(userId, context, planLog);
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

    private void getWorkoutsInfo(String userId, Context context, WorkoutPlanLog planLog){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUTS_PATH);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Workout> workoutList = new ArrayList();
                DataSnapshot snap;

                for(String workoutId: planLog.getWorkoutsIdsList()) {
                    snap = dataSnapshot.child(userId);

                    if(!snap.hasChild(workoutId)){
                        snap = dataSnapshot.child(Constants.PREMADE_PATH);
                    }

                    Workout workout = snap.child(workoutId).getValue(Workout.class);
                    workoutList.add(workout);
                }

                if(planLog.getCompletionDateCode().isEmpty())
                    workoutsAdapter = new CurrentPlanWorkoutsListAdapter(context, workoutList, planLog.getCurrentDay());

                else
                    workoutsAdapter = new CurrentPlanWorkoutsListAdapter(context, workoutList, planLog.getCurrentDay() + 1);

                mWorkoutsList.setAdapter(workoutsAdapter);
                Map<String, Integer> setsPerMuscleGroup = getSetsPerMuscleGroup(workoutsAdapter.getWorkoutsInPlan());
                chartBuilder.updateBarChart(getResources(), setsPerMuscleGroup, mSetsPerMuscleGroupChart);
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

    private void setupSummaryChart(){
        chartBuilder.setupBarChart(getResources(), mSetsPerMuscleGroupChart);
    }

    private void setupVolumeChart(WorkoutPlanLog planLog){
        List<String> xValues = new ArrayList();
        List<Entry> entries = new ArrayList();

        for(int i = 0; i < planLog.getNumberWeeks(); i++){
            xValues.add("Week " + (i + 1));
            WorkoutPlanWeekLog weekLog = planLog.getWeeklyLogsList().get(i);
            entries.add(new Entry(i, weekLog.getWeeklyVolume()));
        }

        Resources resources = getResources();

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(resources.getColor(R.color.white));
        dataSet.setCircleColor(resources.getColor(R.color.white));

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData data = new LineData(dataSets);

        data.setValueTextSize(GRAPH_DATA_SIZE);
        data.setValueTextColor(resources.getColor(R.color.white));
        data.setHighlightEnabled(false);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if((int) Math.floor(value) == 0)
                    return "";

                return (int) Math.floor(value) + "";
            }
        });

        mVolumeChart.setData(data);
        mVolumeChart.setHighlightPerTapEnabled(false);
        mVolumeChart.setTouchEnabled(true);
        mVolumeChart.setDragEnabled(true);
        mVolumeChart.setScaleYEnabled(false);
        mVolumeChart.setDoubleTapToZoomEnabled(false);
        mVolumeChart.setDrawGridBackground(false);
        mVolumeChart.setExtraBottomOffset(15);
        mVolumeChart.setExtraTopOffset(15);
        mVolumeChart.getLegend().setEnabled(false);
        mVolumeChart.getDescription().setEnabled(false);
        mVolumeChart.setBackgroundColor(resources.getColor(R.color.dark_gray));
        mVolumeChart.setMinOffset(25f);

        XAxis xAxis = mVolumeChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setTextSize(GRAPH_DATA_SIZE);
        xAxis.setTextColor(resources.getColor(R.color.white));
        xAxis.setLabelCount(entries.size(),false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6);

        YAxis yAxis = mVolumeChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawLabels(false);
        yAxis.setTextColor(resources.getColor(R.color.white));

        yAxis = mVolumeChart.getAxisRight();
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawLabels(false);

        mVolumeChart.animateY(2000);
    }

    private void startNextWorkout(){
        String userId = FirebaseAuth.getInstance().getUid();
        Context context = this;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS_PATH)
                .child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String planLogId = snapshot
                        .child(Constants.CURRENT_PLAN_LOG_ID_FIELD)
                        .getValue(String.class);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.PLAN_LOGS_PATH)
                        .child(userId)
                        .child(planLogId);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        WorkoutPlanLog planLog = snapshot.getValue(WorkoutPlanLog.class);
                        int currentDay = planLog.getCurrentDay();
                        String workoutId = planLog.getWorkoutsIdsList().get(currentDay);

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

                        View popupView = getLayoutInflater().inflate(R.layout.popup_start_workout, null);
                        Button yesButton = popupView.findViewById(R.id.popup_start_workout_yes_button);
                        Button noButton = popupView.findViewById(R.id.popup_start_workout_no_button);

                        dialogBuilder.setView(popupView);
                        Dialog dialog = dialogBuilder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        yesButton.setOnClickListener(view -> {
                            dialog.dismiss();
                            Intent intent = new Intent(context, WorkoutLogActivity.class);
                            intent.putExtra(Constants.WORKOUT_ID_FIELD, workoutId);
                            intent.putExtra(Constants.IS_WORKOUT_FROM_PLAN_FIELD, true);
                            intent.putExtra(Constants.CURRENT_PLAN_LOG_ID_FIELD, planLogId);
                            startActivity(intent);
                            finish();
                        });

                        noButton.setOnClickListener(view -> dialog.dismiss());

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