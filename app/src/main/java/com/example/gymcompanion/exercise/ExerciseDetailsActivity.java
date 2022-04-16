package com.example.gymcompanion.exercise;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.ExerciseDetailed;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class ExerciseDetailsActivity extends AppCompatActivity {

    private static final String INSERT_NUMBER_SETS             = "Please insert the number of sets.";
    private static final String INSERT_LOW_REP_RANGE           = "Please insert the lower limit of rep range.";
    private static final String INSERT_HIGH_REP_RANGE          = "Please insert the higher limit of rep range.";
    private static final String INSERT_RPE                     = "Please insert the target RPE.";
    private static final String LOWER_VALUE_BIGGER_THAN_HIGHER = "The lower limit of rep range can't be higher than the higher limit one.";

    private TextView mExerciseName;
    private TextView mNumberSets;
    private TextView mTargetRPE;
    private EditText mLowRepRange;
    private EditText mHighRepRange;
    private ImageView mCancelButton;
    private ImageView mSubtractSetsButton;
    private ImageView mAddSetsButton;
    private ImageView mAddRPEButton;
    private ImageView mSubstractRPEButton;
    private Button mAddExerciseButton;

    private FirebaseAuth auth;
    private String exerciseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_details);

        Intent intent = getIntent();

        exerciseId = intent.getStringExtra(Constants.EXERCISE_ID_FIELD);
        String exerciseName = intent.getStringExtra(Constants.EXERCISE_NAME_FIELD);

        getComponents();
        setButtonListeners();
        setEditTextsListeners();
        setExerciseName(exerciseName);
    }

    private void getComponents(){
        auth = FirebaseAuth.getInstance();
        mExerciseName = findViewById(R.id.exercise_details_exercise_name);
        mNumberSets = findViewById(R.id.exercise_details_sets_number);
        mLowRepRange = findViewById(R.id.exercise_details_low_rep_range);
        mHighRepRange = findViewById(R.id.exercise_details_high_rep_range);
        mTargetRPE = findViewById(R.id.exercise_details_target_rpe);
        mSubtractSetsButton = findViewById(R.id.exercise_details_subtract_set_button);
        mAddSetsButton = findViewById(R.id.exercise_details_add_set_button);
        mAddRPEButton = findViewById(R.id.exercise_details_add_rpe_button);
        mSubstractRPEButton = findViewById(R.id.exercise_details_subtract_rpe_button);
        mCancelButton = findViewById(R.id.exercise_details_cancel_button);
        mAddExerciseButton = findViewById(R.id.exercise_details_add_exercise_button);
    }

    private void setButtonListeners(){
        mSubtractSetsButton.setOnClickListener(v -> changeNumberSets(-1));
        mAddSetsButton.setOnClickListener(v -> changeNumberSets(1));
        mSubstractRPEButton.setOnClickListener(v -> changeTargetRPE(-1));
        mAddRPEButton.setOnClickListener(v -> changeTargetRPE(1));
        mAddExerciseButton.setOnClickListener(v -> addExercise());
        mCancelButton.setOnClickListener(v -> finish());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEditTextsListeners(){
        mLowRepRange.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                hideKeyboard(v);
            }
        });

        mHighRepRange.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                hideKeyboard(v);
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setExerciseName(String exerciseName){
        mExerciseName.setText(exerciseName);
    }

    private void changeNumberSets(int change){
        int numberSets = Integer.parseInt(mNumberSets.getText().toString()) + change;

        if(numberSets < 1)
            numberSets = 1;

        mNumberSets.setText(String.valueOf(numberSets));
    }

    private void changeTargetRPE(int change){
        int targetRPE = Integer.parseInt(mTargetRPE.getText().toString()) + change;

        if(targetRPE < 1)
            targetRPE = 1;

        else if(targetRPE > 10)
            targetRPE = 10;

        mTargetRPE.setText(String.valueOf(targetRPE));
    }

    private void addExercise(){
        String name = mExerciseName.getText().toString();
        String numberSets = mNumberSets.getText().toString();
        String lowRepRange = mLowRepRange.getText().toString();
        String highRepRange = mHighRepRange.getText().toString();
        String targetRPE = mTargetRPE.getText().toString();

        if(verifyParameters(numberSets, lowRepRange, highRepRange, targetRPE)){

            String exerciseDetailedId = UUID.randomUUID().toString();

            ExerciseDetailed exerciseDetailed = new ExerciseDetailed(exerciseDetailedId,
                    exerciseId,
                    name,
                    numberSets,
                    lowRepRange,
                    highRepRange,
                    targetRPE);

            String userId = auth.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.EXERCISES_WORKOUT_PATH)
                    .child(userId)
                    .child(exerciseDetailedId);

            reference.setValue(exerciseDetailed);

            Intent resultIntent = new Intent();
            resultIntent.putExtra(Constants.EXERCISE_DETAILED_ID_FIELD, exerciseDetailedId);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private boolean verifyParameters(String numberSets, String lowRepRange, String highRepRange,
                                     String targetRPE){

        if(numberSets.isEmpty()){
            Toast.makeText(ExerciseDetailsActivity.this, INSERT_NUMBER_SETS, Toast.LENGTH_LONG).show();
            return false;
        }

        if(lowRepRange.isEmpty()){
            Toast.makeText(ExerciseDetailsActivity.this, INSERT_LOW_REP_RANGE, Toast.LENGTH_LONG).show();
            return false;
        }

        if(highRepRange.isEmpty()){
            Toast.makeText(ExerciseDetailsActivity.this, INSERT_HIGH_REP_RANGE, Toast.LENGTH_LONG).show();
            return false;
        }

        if(targetRPE.isEmpty()){
            Toast.makeText(ExerciseDetailsActivity.this, INSERT_RPE, Toast.LENGTH_LONG).show();
            return false;
        }

        if(Integer.parseInt(lowRepRange) > Integer.parseInt(highRepRange)){
            Toast.makeText(ExerciseDetailsActivity.this, LOWER_VALUE_BIGGER_THAN_HIGHER, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}