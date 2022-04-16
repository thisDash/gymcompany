package com.example.gymcompanion.workout;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.barChart.BarChartBuilder;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.Exercise;
import com.example.gymcompanion.components.ExerciseDetailed;
import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.exercise.ExercisesActivity;
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

public class CreateWorkoutActivity extends AppCompatActivity {

    private static final String TAG = "CreateWorkoutActivity";

    private static final int EXERCISE_ACTIVITY_CODE = 1;

    private static final String NO_WORKOUT_NAME_WARNING = "Please insert a workout name.";
    private static final String NO_EXERCISES_WARNING = "A workout needs to have at least one exercise.";
    private static final String WORKOUT_HINT = "Workout Name";

    private ImageView mCancelButton;
    private ImageView mAddExerciseButton;
    private ImageView mDeleteExerciseButton;
    private Button mCreateButton;
    private EditText mWorkoutName;
    private RecyclerView mExercisesListView;
    private BarChart mBarChart;

    private CreateWorkoutExercisesListAdapter exercisesAdapter;
    private BarChartBuilder barChartBuilder;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);

        getComponents();
        setButtonListeners();
        setExercisesList();
        setBarChart();
    }

    private void getComponents(){
        mCancelButton = findViewById(R.id.create_workout_cancel_button);
        mCreateButton = findViewById(R.id.create_workout_create_button);
        mAddExerciseButton = findViewById(R.id.create_workout_add_exercise_button);
        mDeleteExerciseButton = findViewById(R.id.create_workout_delete_exercise_button);
        mExercisesListView = findViewById(R.id.create_workout_exercises_list);
        mBarChart = findViewById(R.id.create_workout_summary_chart);
        mWorkoutName = findViewById(R.id.create_workout_workout_name);
        auth = FirebaseAuth.getInstance();
        barChartBuilder = new BarChartBuilder();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setButtonListeners(){
        mCancelButton.setOnClickListener(v -> goToPreviousPage());
        mAddExerciseButton.setOnClickListener(v -> goToExercisesPage());
        mDeleteExerciseButton.setOnClickListener(v -> deleteChosenExercises());
        mCreateButton.setOnClickListener(v -> createWorkout());

        mWorkoutName.setOnTouchListener((v, event) -> {
            mWorkoutName.setHint("");
            return false;
        });

        mWorkoutName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                mWorkoutName.setHint(WORKOUT_HINT);
                hideKeyboard(v);
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setExercisesList(){
        List<ExerciseDetailed> exerciseList = new ArrayList();
        LinearLayoutManager exercisesLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        exercisesAdapter = new CreateWorkoutExercisesListAdapter(exerciseList, this);
        mExercisesListView.setLayoutManager(exercisesLayoutManager);
        mExercisesListView.setAdapter(exercisesAdapter);
    }

    private void setBarChart(){
        barChartBuilder.setupBarChart(getResources(), mBarChart);
    }

    private void goToExercisesPage(){
        Intent intent = new Intent(this, ExercisesActivity.class);
        startActivityForResult(intent, EXERCISE_ACTIVITY_CODE);
    }

    private void deleteChosenExercises(){
        List<String> exercisesToDelete = exercisesAdapter.getChosenExercisesList();

        Log.d(TAG, "deleteChosenExercises: Before deleting: " +
                exercisesAdapter.getExercisesInWorkout() + " exercises in workout");

        Collections.sort(exercisesToDelete, Collections.reverseOrder());

        for(String exercisePosition: exercisesToDelete)
            exercisesAdapter.deleteChosenExercise(Integer.parseInt(exercisePosition));

        exercisesAdapter.clearChosenList();
        exercisesAdapter.notifyDataSetChanged();

        updateBarChart(exercisesAdapter.getExercisesInWorkout());

        Log.d(TAG, "deleteChosenExercises: After deleting: " +
                exercisesAdapter.getExercisesInWorkout() + " exercises in workout");
    }

    private void createWorkout(){
        List<ExerciseDetailed> exercisesInWorkout = exercisesAdapter.getExercisesInWorkout();
        String workoutName = mWorkoutName.getText().toString();

        if(exercisesInWorkout.size() == 0)
            Toast.makeText(CreateWorkoutActivity.this, NO_EXERCISES_WARNING, Toast.LENGTH_SHORT).show();

        else if(workoutName.isEmpty())
            Toast.makeText(CreateWorkoutActivity.this, NO_WORKOUT_NAME_WARNING, Toast.LENGTH_SHORT).show();

        else{
            int numberExercises = exercisesInWorkout.size();
            int numberSets = 0;

            for(ExerciseDetailed exercise: exercisesInWorkout)
                numberSets += Integer.parseInt(exercise.getNumberSets());

            String workoutId = UUID.randomUUID().toString();
            String userId = auth.getUid();

            List<String> muscleGroups = new ArrayList();
            Collections.addAll(muscleGroups, getResources().getStringArray(R.array.muscle_groups));

            Map<String, Integer> setsPerMuscleGroupMap = new HashMap();

            for(String muscleGroup: muscleGroups) {
                setsPerMuscleGroupMap.put(muscleGroup, 0);
            }

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.EXERCISES_PATH)
                    .child(userId);

            int finalNumberSets = numberSets;
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(ExerciseDetailed exerciseDetailed: exercisesInWorkout){
                        Exercise exercise = snapshot.child(exerciseDetailed.getExerciseId())
                                .getValue(Exercise.class);

                        for(String muscleGroupWorked: exercise.getMuscleGroupsWorked()){
                            int newNumberSets = setsPerMuscleGroupMap.get(muscleGroupWorked) +
                                    Integer.parseInt(exerciseDetailed.getNumberSets());
                            setsPerMuscleGroupMap.put(muscleGroupWorked,newNumberSets);
                        }
                    }

                    Integer[] setsPerMuscleGroupArray = new Integer[muscleGroups.size()];

                    setsPerMuscleGroupArray[Constants.CHEST_INDEX] = setsPerMuscleGroupMap.get(Constants.CHEST);
                    setsPerMuscleGroupArray[Constants.BACK_INDEX] = setsPerMuscleGroupMap.get(Constants.BACK);
                    setsPerMuscleGroupArray[Constants.SHOULDERS_INDEX] = setsPerMuscleGroupMap.get(Constants.SHOULDERS);
                    setsPerMuscleGroupArray[Constants.QUADS_INDEX] = setsPerMuscleGroupMap.get(Constants.QUADS);
                    setsPerMuscleGroupArray[Constants.HAMSTRINGS_INDEX] = setsPerMuscleGroupMap.get(Constants.HAMSTRINGS);
                    setsPerMuscleGroupArray[Constants.TRICEPS_INDEX] = setsPerMuscleGroupMap.get(Constants.TRICEPS);
                    setsPerMuscleGroupArray[Constants.BICEPS_INDEX] = setsPerMuscleGroupMap.get(Constants.BICEPS);
                    setsPerMuscleGroupArray[Constants.CALVES_INDEX] = setsPerMuscleGroupMap.get(Constants.CALVES);
                    setsPerMuscleGroupArray[Constants.GLUTES_INDEX]= setsPerMuscleGroupMap.get(Constants.GLUTES);
                    setsPerMuscleGroupArray[Constants.TRAPS_INDEX] = setsPerMuscleGroupMap.get(Constants.TRAPS);

                    List<Integer> setsPerMuscleGroup = new ArrayList(Arrays.asList(setsPerMuscleGroupArray));

                    Workout workout = new Workout(workoutId,
                            workoutName,
                            String.valueOf(numberExercises),
                            String.valueOf(finalNumberSets),
                            exercisesAdapter.getExercisesInWorkout(),
                            setsPerMuscleGroup);

                    DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.WORKOUTS_PATH)
                            .child(userId)
                            .child(workoutId);

                    reference.setValue(workout);
                    deleteTemporaryData();
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EXERCISE_ACTIVITY_CODE){
            if(resultCode == RESULT_OK){
                String addedExerciseId = data.getStringExtra(Constants.EXERCISE_DETAILED_ID_FIELD);

                String userId = auth.getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.EXERCISES_WORKOUT_PATH)
                        .child(userId)
                        .child(addedExerciseId);

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Log.d(TAG, "onDataChange: Before adding: " + exercisesAdapter.getExercisesInWorkout().size() + " exercises in workout");

                        ExerciseDetailed exercise = snapshot.getValue(ExerciseDetailed.class);
                        exercisesAdapter.addExerciseToWorkout(exercise);
                        exercisesAdapter.notifyDataSetChanged();
                        updateBarChart(exercisesAdapter.getExercisesInWorkout());
                        Log.d(TAG, "onDataChange: After adding: " + exercisesAdapter.getExercisesInWorkout().size() + " workouts in plan");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void updateBarChart(List<ExerciseDetailed> exercisesInWorkout){
        List<String> muscleGroups = new ArrayList();
        Collections.addAll(muscleGroups, getResources().getStringArray(R.array.muscle_groups));

        Map<String, Integer> setsPerMuscle = new HashMap();

        for(String muscleGroup: muscleGroups){
            setsPerMuscle.put(muscleGroup,0);
        }

        String userId = auth.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.EXERCISES_PATH)
                .child(userId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(ExerciseDetailed exerciseDetailed: exercisesInWorkout){

                    Exercise exercise = snapshot.child(exerciseDetailed.getExerciseId())
                            .getValue(Exercise.class);

                    for(String muscleGroupWorked: exercise.getMuscleGroupsWorked()){
                        int newNumberSets = setsPerMuscle.get(muscleGroupWorked) +
                                Integer.parseInt(exerciseDetailed.getNumberSets());
                        setsPerMuscle.put(muscleGroupWorked,newNumberSets);
                    }
                }

                barChartBuilder.updateBarChart(getResources(), setsPerMuscle, mBarChart);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void goToPreviousPage(){
        deleteTemporaryData();
        finish();
    }

    private void deleteTemporaryData(){
        String userId = auth.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.EXERCISES_WORKOUT_PATH)
                .child(userId);

        reference.removeValue();
    }
}