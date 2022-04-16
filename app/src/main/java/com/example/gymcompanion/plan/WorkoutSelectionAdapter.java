package com.example.gymcompanion.plan;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.ExerciseDetailed;
import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.workout.WorkoutOverviewExercisesListAdapter;

import java.util.ArrayList;
import java.util.List;

public class WorkoutSelectionAdapter extends RecyclerView.Adapter<WorkoutSelectionAdapter.ViewHolder> {

    private static final String TAG = "WorkoutSelectionAdapter";

    private Context mContext;
    private List<Workout> workoutsList;
    private List<String> chosenWorkouts;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    public WorkoutSelectionAdapter(Context context, List<Workout> workoutsList){
        this.workoutsList = workoutsList;
        this.chosenWorkouts = new ArrayList();
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_workout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workout workout = workoutsList.get(position);

        holder.workoutName.setText(workout.getWorkoutName());
        holder.numberExercises.setText(workout.getNumberExercises());
        holder.numberSets.setText(workout.getNumberSets());

        holder.workoutLayout.setOnClickListener(view -> {

            String workoutId = workout.getWorkoutId();

            if(chosenWorkouts.contains(workoutId)){
                holder.workoutChecked.setVisibility(View.GONE);
                chosenWorkouts.remove(workoutId);

            } else {
                holder.workoutChecked.setVisibility(View.VISIBLE);
                chosenWorkouts.add(workoutId);
            }

            Log.d(TAG, "onBindViewHolder: have " + chosenWorkouts.size() + " selected");

        });

        holder.workoutLayout.setOnLongClickListener(view -> {
            dialogBuilder = new AlertDialog.Builder(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            final View workoutPopupView = inflater.inflate(R.layout.popup_workout, null);

            TextView mWorkoutName = workoutPopupView.findViewById(R.id.workout_popup_workout_name);

            getWorkoutInformation(workoutPopupView, workout);
            mWorkoutName.setText(workout.getWorkoutName());

            dialogBuilder.setView(workoutPopupView);
            dialog = dialogBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            return false;
        });
    }

    private void getWorkoutInformation(View workoutPopupView, Workout workout){
        RecyclerView mExercisesList = workoutPopupView.findViewById(R.id.workout_popup_exercises_list);

        List<ExerciseDetailed> exerciseList = workout.getExerciseDetailedList();
        LinearLayoutManager exercisesLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL,
                false);

        WorkoutOverviewExercisesListAdapter exercisesAdapter = new WorkoutOverviewExercisesListAdapter(exerciseList, mContext);
        mExercisesList.setLayoutManager(exercisesLayoutManager);
        mExercisesList.setAdapter(exercisesAdapter);
    }

    @Override
    public int getItemCount() {
        return workoutsList.size();
    }

    public List<String> getChosenWorkouts(){
        return chosenWorkouts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView workoutName;
        TextView numberSets;
        TextView numberExercises;
        ConstraintLayout workoutLayout;
        ImageView workoutChecked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workout_name);
            numberSets = itemView.findViewById(R.id.workout_number_sets);
            numberExercises = itemView.findViewById(R.id.workout_number_exercises);
            workoutLayout = itemView.findViewById(R.id.workout_layout);
            workoutChecked = itemView.findViewById(R.id.workout_checked);
        }
    }
}
