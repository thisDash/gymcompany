package com.example.gymcompanion.exercise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.Exercise;
import com.example.gymcompanion.components.MuscleGroupFilter;
import com.example.gymcompanion.muscleGroupsFilter.MuscleGroupFiltersAdapter;
import com.example.gymcompanion.workout.FilterNameComparator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExercisesActivity extends AppCompatActivity {

    private static final String TAG = "ExercisesActivity";

    private static final int EXERCISE_DETAILS_ACTIVITY_CODE = 1;

    private ImageView mCancelButton;
    private Button mCreateExerciseButton;
    private RecyclerView mExercisesListView;
    private RecyclerView mExercisesFilters;

    private ExercisesAdapter exercisesAdapter;
    private MuscleGroupFiltersAdapter filtersAdapter;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        getComponents();
        setButtonListeners();
        setExercisesList();
        setExercisesFilters();
    }

    private void getComponents(){
        auth = FirebaseAuth.getInstance();
        mCancelButton = findViewById(R.id.exercises_cancel_button);
        mCreateExerciseButton = findViewById(R.id.exercises_create_exercise_button);
        mExercisesListView = findViewById(R.id.exercises_exercises_list);
        mExercisesFilters = findViewById(R.id.exercises_exercises_filters);
    }

    private void setButtonListeners(){
        mCancelButton.setOnClickListener(v -> finish());
        mCreateExerciseButton.setOnClickListener(v -> goToCreateExercisePage());
    }

    private void goToCreateExercisePage(){
        Intent intent = new Intent(this, CreateExerciseActivity.class);
        startActivity(intent);
    }

    private void setExercisesList(){
        String userId = auth.getUid();
        Context context = this;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.EXERCISES_PATH).child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Exercise> exerciseList = new ArrayList<>();

                for(DataSnapshot snap: snapshot.getChildren()){
                    Exercise exercise = snap.getValue(Exercise.class);
                    exerciseList.add(exercise);
                }

                Collections.sort(exerciseList, new ExerciseNameComparator());

                LinearLayoutManager muscleGroupsLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                exercisesAdapter = new ExercisesAdapter(exerciseList, context);
                mExercisesListView.setLayoutManager(muscleGroupsLayoutManager);
                mExercisesListView.setAdapter(exercisesAdapter);

                exercisesAdapter.setOnItemClickListener((exerciseId, exerciseName) ->
                        goToExerciseDetailsPage(exerciseId, exerciseName, context));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setExercisesFilters(){
        List<String> muscleGroups = new ArrayList();
        Collections.addAll(muscleGroups, getResources().getStringArray(R.array.muscle_groups));
        List<MuscleGroupFilter> filters = new ArrayList();

        for(String muscleGroup: muscleGroups)
            filters.add(new MuscleGroupFilter(muscleGroup, false));

        Collections.sort(filters, new FilterNameComparator());

        LinearLayoutManager filtersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        filtersAdapter = new MuscleGroupFiltersAdapter(filters, this);
        mExercisesFilters.setLayoutManager(filtersLayoutManager);
        mExercisesFilters.setAdapter(filtersAdapter);

        filtersAdapter.setOnItemClickListener(muscleGroupsSelected -> {

            List<MuscleGroupFilter> newFiltersOrder = new ArrayList();

            for(String muscleGroup: muscleGroupsSelected){
                newFiltersOrder.add(new MuscleGroupFilter(muscleGroup, true));
            }

            Collections.sort(newFiltersOrder, new FilterNameComparator());

            List<MuscleGroupFilter> nonSelectedFilters = new ArrayList();

            for(String muscleGroup: muscleGroups)
                if(!muscleGroupsSelected.contains(muscleGroup))
                    nonSelectedFilters.add(new MuscleGroupFilter(muscleGroup, false));

            Collections.sort(nonSelectedFilters, new FilterNameComparator());

            for(MuscleGroupFilter filterNotSelected: nonSelectedFilters)
                newFiltersOrder.add(filterNotSelected);

            filtersAdapter.setMuscleGroupFiltersList(newFiltersOrder);
            filtersAdapter.notifyDataSetChanged();

            String userId = auth.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.EXERCISES_PATH).child(userId);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Exercise> exerciseList = new ArrayList<>();

                    for(DataSnapshot snap: snapshot.getChildren()){
                        Exercise exercise = snap.getValue(Exercise.class);
                        boolean canBeAdded = true;

                        for(String muscleGroup: muscleGroupsSelected){
                            if(!exercise.getMuscleGroupsWorked().contains(muscleGroup)) {
                                canBeAdded = false;
                                break;
                            }
                        }

                        if(canBeAdded)
                            exerciseList.add(exercise);
                    }

                    Collections.sort(exerciseList, new ExerciseNameComparator());
                    exercisesAdapter.setExercisesList(exerciseList);
                    exercisesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void goToExerciseDetailsPage(String exerciseId, String exerciseName, Context context){
        Intent intent = new Intent(context, ExerciseDetailsActivity.class);
        intent.putExtra(Constants.EXERCISE_ID_FIELD, exerciseId);
        intent.putExtra(Constants.EXERCISE_NAME_FIELD, exerciseName);
        startActivityForResult(intent, EXERCISE_DETAILS_ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EXERCISE_DETAILS_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                String exerciseDetailedId = data.getStringExtra(Constants.EXERCISE_DETAILED_ID_FIELD);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.EXERCISE_DETAILED_ID_FIELD, exerciseDetailedId);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }
}