package com.example.gymcompanion.plan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.barChart.BarChartBuilder;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.components.WorkoutPlan;
import com.github.mikephil.charting.charts.BarChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreatePlanActivity extends AppCompatActivity {

    private static final String TAG = "CreatePlanActivity";

    private static final int SELECT_WORKOUT_ACTIVITY_CODE = 1;

    private static final String PLAN_HINT = "Plan Name";

    private static final String NO_PLAN_NAME_WARNING = "Please insert a plan name.";
    private static final String NO_WORKOUTS_WARNING = "A plan needs to have at least one workout.";

    private EditText mPlanName;
    private TextView mNumberWeeks;
    private RecyclerView mWorkoutsInPlanList;
    private ImageView mAddWorkoutToPlanButton;
    private ImageView mDeleteWorkoutFromPlanButton;
    private ImageView mAddNumberWeeksButton;
    private ImageView mSubtractNumberWeeksButton;
    private ImageView mCancelButton;
    private BarChart mBarChart;
    private Switch mIncludeIntroWeek;
    private Switch mIncludeDeloadWeek;
    private Button mCreatePlan;

    private CreatePlanWorkoutsAdapter workoutsAdapter;
    private Map<String, Integer> setsPerMuscle;
    private BarChartBuilder barChartBuilder;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        getComponents();
        setButtonListeners();
        setEditTextsListeners();
        setWorkoutsList();
        setBarChart();
    }

    private void getComponents(){
        mPlanName = findViewById(R.id.create_plan_plan_name);
        mNumberWeeks = findViewById(R.id.create_plan_number_weeks);
        mWorkoutsInPlanList = findViewById(R.id.create_plan_workouts_list);
        mAddWorkoutToPlanButton = findViewById(R.id.create_plan_add_workout_button);
        mDeleteWorkoutFromPlanButton = findViewById(R.id.create_plan_delete_workout_button);
        mAddNumberWeeksButton = findViewById(R.id.create_plan_add_weeks_button);
        mSubtractNumberWeeksButton = findViewById(R.id.create_plan_subtract_weeks_button);
        mCancelButton = findViewById(R.id.create_plan_cancel_button);
        mBarChart = findViewById(R.id.create_plan_summary_chart);
        mIncludeIntroWeek = findViewById(R.id.create_plan_include_intro_week_switch);
        mIncludeDeloadWeek = findViewById(R.id.create_plan_include_deload_week_switch);
        mCreatePlan = findViewById(R.id.create_plan_create_button);
        auth = FirebaseAuth.getInstance();
        setsPerMuscle = new HashMap();
        barChartBuilder = new BarChartBuilder();
    }

    private void setButtonListeners(){
        mCancelButton.setOnClickListener(v -> finish());
        mAddNumberWeeksButton.setOnClickListener(v -> changeNumberWeeks(1));
        mSubtractNumberWeeksButton.setOnClickListener(v -> changeNumberWeeks(-1));
        mAddWorkoutToPlanButton.setOnClickListener(v -> goToWorkoutsPage());
        mDeleteWorkoutFromPlanButton.setOnClickListener(v -> deleteChosenWorkouts());
        mCreatePlan.setOnClickListener(v -> createPlan());
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEditTextsListeners(){
        mPlanName.setOnTouchListener((v, event) -> {
            mPlanName.setHint("");
            return false;
        });

        mPlanName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                mPlanName.setHint(PLAN_HINT);
                hideKeyboard(v);
            }
        });
    }

    private void changeNumberWeeks(int change){
        int numberWeeks = Integer.parseInt(mNumberWeeks.getText().toString()) + change;

        if(numberWeeks < 1)
            numberWeeks = 1;

        mNumberWeeks.setText(String.valueOf(numberWeeks));
    }

    private void setWorkoutsList(){
        List<Workout> workoutList = new ArrayList();
        LinearLayoutManager workoutsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        workoutsAdapter = new CreatePlanWorkoutsAdapter(this, workoutList);
        mWorkoutsInPlanList.setLayoutManager(workoutsLayoutManager);
        mWorkoutsInPlanList.setAdapter(workoutsAdapter);
    }

    private void setBarChart(){
        barChartBuilder.setupBarChart(getResources(), mBarChart);
    }

    private void goToWorkoutsPage(){
        Intent intent = new Intent(this, WorkoutSelectionActivity.class);
        startActivityForResult(intent, SELECT_WORKOUT_ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_WORKOUT_ACTIVITY_CODE){
            if(resultCode == RESULT_OK){
                String[] addedWorkouts = data.getStringArrayExtra(Constants.WORKOUTS_CHOSEN_FIELD);
                Log.d(TAG, "onActivityResult: selected " + addedWorkouts.length + " new workouts.");

                List<String> addedWorkoutsIds = new ArrayList(Arrays.asList(addedWorkouts));

                String userId = auth.getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.WORKOUTS_PATH);

                //TODO buscar aos premades tmb

                Log.d(TAG, "onDataChange: Before adding: " + workoutsAdapter.getWorkoutsInPlan().size() + " workouts in plan");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DataSnapshot snap;

                            for(String workoutId: addedWorkoutsIds) {
                                snap = snapshot.child(userId);

                                if(!snap.hasChild(workoutId)){
                                    snap = snapshot.child(Constants.PREMADE_PATH);
                                }

                                Workout workout = snap.child(workoutId).getValue(Workout.class);
                                workoutsAdapter.addWorkoutToPlan(workout);
                            }

                            workoutsAdapter.notifyDataSetChanged();
                            updateBarChart(workoutsAdapter.getWorkoutsInPlan());
                            Log.d(TAG, "onDataChange: After adding: " + workoutsAdapter.getWorkoutsInPlan().size() + " workouts in plan");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                });
            }
        }
    }

    private void updateBarChart(List<Workout> workoutsInPlan){
        Map<String, Integer> setsPerMuscle = getSetsPerMuscleGroup(workoutsInPlan);
        barChartBuilder.updateBarChart(getResources(), setsPerMuscle, mBarChart);
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

    private void deleteChosenWorkouts(){
        List<String> workoutsToDelete = workoutsAdapter.getChosenWorkoutsList();
        Collections.sort(workoutsToDelete, Collections.reverseOrder());

        for(String workoutPosition: workoutsToDelete)
            workoutsAdapter.deleteChosenWorkout(Integer.parseInt(workoutPosition));

        workoutsAdapter.clearChosenList();
        workoutsAdapter.notifyDataSetChanged();

        updateBarChart(workoutsAdapter.getWorkoutsInPlan());
    }

    private void createPlan(){
        List<Workout> workoutsInPlan = workoutsAdapter.getWorkoutsInPlan();
        List<String> workoutsInPlanIds = new ArrayList();
        String planName = mPlanName.getText().toString();

        if(workoutsInPlan.size() == 0){
            Toast.makeText(CreatePlanActivity.this, NO_WORKOUTS_WARNING, Toast.LENGTH_SHORT).show();

        } else if(planName.isEmpty()){
            Toast.makeText(CreatePlanActivity.this, NO_PLAN_NAME_WARNING, Toast.LENGTH_SHORT).show();

        } else{

            String userId = auth.getUid();
            String planId = UUID.randomUUID().toString();
            int numberWeeks = Integer.parseInt(mNumberWeeks.getText().toString());
            boolean includeIntroWeek = mIncludeIntroWeek.isChecked();
            boolean includeDeloadWeek = mIncludeDeloadWeek.isChecked();

            for(Workout workout: workoutsInPlan)
                workoutsInPlanIds.add(workout.getWorkoutId());

            WorkoutPlan newPlan = new WorkoutPlan(planId,
                    planName,
                    numberWeeks,
                    includeIntroWeek,
                    includeDeloadWeek,
                    workoutsInPlanIds);

            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.PLANS_PATH)
                    .child(userId)
                    .child(planId);

            reference.setValue(newPlan);
            finish();
        }
    }
}