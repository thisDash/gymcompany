package com.example.gymcompanion.exercise;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.Exercise;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateExerciseActivity extends AppCompatActivity {

    private static final String TAG = "CreateExerciseActivity";

    private static final String INSERT_NAME                    = "Please insert the exercise name.";
    private static final String INSERT_NUMBER_SETS             = "Please insert the number of sets.";
    private static final String INSERT_LOW_REP_RANGE           = "Please insert the lower limit of rep range.";
    private static final String INSERT_HIGH_REP_RANGE          = "Please insert the higher limit of rep range.";
    private static final String INSERT_RPE                     = "Please insert the target RPE.";
    private static final String LOWER_VALUE_BIGGER_THAN_HIGHER = "The lower limit of rep range can't be higher than the higher limit one.";
    private static final String EXERCISE_HINT                  = "Exercise Name";

    private Spinner spinner;
    private EditText mExerciseName;
    private ImageView mCancelButton;
    private ImageView mAddMuscleGroupButton;
    private RecyclerView mMuscleGroupsListView;
    private Button mCreateExerciseButton;

    private List<String> muscleGroupsSelected;
    private MuscleGroupsAdapter muscleGroupsAdapter;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exercise);
        muscleGroupsSelected = new ArrayList();

        getComponents();
        setButtonListeners();
        setEditTextsListeners();
        setupSpinner();
        setupMuscleGroupsListView();
    }

    private void getComponents(){
        auth = FirebaseAuth.getInstance();
        spinner = findViewById(R.id.create_exercise_spinner);
        mExerciseName = findViewById(R.id.create_exercise_exercise_name);
        mAddMuscleGroupButton = findViewById(R.id.create_exercise_add_muscle_group_button);
        mMuscleGroupsListView = findViewById(R.id.create_exercise_muscle_groups_list);
        mCreateExerciseButton = findViewById(R.id.create_exercise_create_button);
        mCancelButton = findViewById(R.id.create_exercise_cancel_button);
    }

    private void setButtonListeners(){
        mAddMuscleGroupButton.setOnClickListener(v -> addNewMuscleGroup());
        mCreateExerciseButton.setOnClickListener(v -> createExercise());
        mCancelButton.setOnClickListener(v -> goToCreateWorkoutPage());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEditTextsListeners(){
        mExerciseName.setOnTouchListener((v, event) -> {
            mExerciseName.setHint("");
            return false;
        });

        mExerciseName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                mExerciseName.setHint(EXERCISE_HINT);
                hideKeyboard(v);
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setupMuscleGroupsListView(){
        LinearLayoutManager muscleGroupsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        muscleGroupsAdapter = new MuscleGroupsAdapter(muscleGroupsSelected);
        mMuscleGroupsListView.setLayoutManager(muscleGroupsLayoutManager);
        mMuscleGroupsListView.setAdapter(muscleGroupsAdapter);

        muscleGroupsAdapter.setOnItemClickListener(position -> {
            removeMuscleGroup(position);
        });
    }

    private void removeMuscleGroup(int position){
        muscleGroupsSelected.remove(position);
        muscleGroupsAdapter.notifyItemRemoved(position);
        fillSpinner();

        if(mAddMuscleGroupButton.getVisibility() == View.GONE)
            mAddMuscleGroupButton.setVisibility(View.VISIBLE);
    }

    private void addNewMuscleGroup(){
        String addedMuscleGroup = spinner.getSelectedItem().toString();
        muscleGroupsSelected.add(addedMuscleGroup);
        muscleGroupsAdapter.notifyDataSetChanged();
        fillSpinner();

        if(muscleGroupsSelected.size() == getResources().getStringArray(R.array.muscle_groups).length)
            mAddMuscleGroupButton.setVisibility(View.GONE);
    }

    private void fillSpinner(){
        List<String> muscleGroupsAvailable = new ArrayList();

        for(String group: getResources().getStringArray(R.array.muscle_groups)){
            if(!muscleGroupsSelected.contains(group))
                muscleGroupsAvailable.add(group);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(
                this,
                R.layout.custom_spinner,
                muscleGroupsAvailable.toArray()
        );
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);
    }

    private void setupSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter(
                this,
                R.layout.custom_spinner,
                getResources().getStringArray(R.array.muscle_groups)
        );

        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);
    }

    private void goToCreateWorkoutPage(){
        finish();
    }

    private void createExercise(){
        String name = mExerciseName.getText().toString();
        String currentlySelected;

        if(spinner.getSelectedItem() != null)
            currentlySelected = spinner.getSelectedItem().toString();

        else
            currentlySelected = null;

        if(verifyParameters(name)){

            if(currentlySelected != null)
                muscleGroupsSelected.add(currentlySelected);

            String exerciseId = UUID.randomUUID().toString();

            Exercise exercise = new Exercise(exerciseId,
                    name,
                    muscleGroupsSelected);

            String userId = auth.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.EXERCISES_PATH)
                    .child(userId)
                    .child(exerciseId);

            Log.d(TAG, "createExercise: Created one exercise");

            reference.setValue(exercise);
            finish();
        }
    }

    private boolean verifyParameters(String name){
        if(name.isEmpty()){
            Toast.makeText(CreateExerciseActivity.this, INSERT_NAME, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}