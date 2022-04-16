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

public class CreatePlanWorkoutsAdapter extends RecyclerView.Adapter<CreatePlanWorkoutsAdapter.ViewHolder> {

    private static final String TAG = "CreatePlanAdapter";

    private Context mContext;
    private List<Workout> workoutsList;
    private List<String> chosenWorkouts;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    public CreatePlanWorkoutsAdapter(Context context, List<Workout> workoutsList){
        this.mContext = context;
        this.workoutsList = workoutsList;
        this.chosenWorkouts = new ArrayList();
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

        Log.d(TAG, "onBindViewHolder: Workout: " + workout.getWorkoutName());

        holder.name.setText(workout.getWorkoutName());
        holder.numberExercises.setText(workout.getNumberExercises());
        holder.numberSets.setText(workout.getNumberSets());
        holder.workoutChecked.setVisibility(View.GONE);

        holder.layout.setOnClickListener(view -> {

            if(chosenWorkouts.contains(String.valueOf(position))){
                holder.workoutChecked.setVisibility(View.GONE);
                chosenWorkouts.remove(String.valueOf(position));
                Log.d(TAG, "onBindViewHolder: not chosen");

            } else {
                holder.workoutChecked.setVisibility(View.VISIBLE);
                chosenWorkouts.add(String.valueOf(position));
                Log.d(TAG, "onBindViewHolder: chosen");
            }

            Log.d(TAG, "onBindViewHolder: have " + chosenWorkouts.size() + " selected");
        });

        holder.layout.setOnLongClickListener(view -> {
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
        LinearLayoutManager exercisesLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        WorkoutOverviewExercisesListAdapter exercisesAdapter = new WorkoutOverviewExercisesListAdapter(exerciseList, mContext);
        mExercisesList.setLayoutManager(exercisesLayoutManager);
        mExercisesList.setAdapter(exercisesAdapter);
    }

    @Override
    public int getItemCount() {
        return workoutsList.size();
    }

    public List<String> getChosenWorkoutsList(){
        return chosenWorkouts;
    }

    public List<Workout> getWorkoutsInPlan(){
        return workoutsList;
    }

    public void addWorkoutToPlan(Workout workout){
        workoutsList.add(workout);
    }

    public void deleteChosenWorkout(int position){
        workoutsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, workoutsList.size());
    }

    public void clearChosenList(){
        chosenWorkouts = new ArrayList();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView numberExercises;
        TextView numberSets;
        ConstraintLayout layout;
        ImageView workoutChecked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.workout_name);
            numberExercises = itemView.findViewById(R.id.workout_number_exercises);
            numberSets = itemView.findViewById(R.id.workout_number_sets);
            layout = itemView.findViewById(R.id.workout_layout);
            workoutChecked = itemView.findViewById(R.id.workout_checked);
        }
    }
}
