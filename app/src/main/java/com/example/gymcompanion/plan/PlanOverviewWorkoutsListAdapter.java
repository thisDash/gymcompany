package com.example.gymcompanion.plan;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.ExerciseDetailed;
import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.components.WorkoutPlan;
import com.example.gymcompanion.workout.WorkoutOverviewExercisesListAdapter;

import java.util.List;

public class PlanOverviewWorkoutsListAdapter extends RecyclerView.Adapter<PlanOverviewWorkoutsListAdapter.ViewHolder> {

    private static final String TAG = "WorkoutOverviewExercisesListAdapter";

    private List<Workout> workoutsList;
    private Context mContext;
    private WorkoutPlan plan;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    public PlanOverviewWorkoutsListAdapter(Context context, List<Workout> workoutsList, WorkoutPlan plan){
        this.workoutsList = workoutsList;
        this.mContext = context;
        this.plan = plan;
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
        holder.name.setText(workout.getWorkoutName());
        holder.numberExercises.setText(workout.getNumberExercises());
        holder.numberSets.setText(workout.getNumberSets());
        holder.workoutChecked.setVisibility(View.GONE);

        holder.layout.setOnClickListener(view -> {
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
        });

        holder.workoutCard.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));
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

    public List<Workout> getWorkoutsInPlan(){
        return workoutsList;
    }

    public void addWorkoutToPlan(Workout workout){
        workoutsList.add(workout);
    }

    public WorkoutPlan getPlan(){
        return plan;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView numberExercises;
        TextView numberSets;
        ConstraintLayout layout;
        ImageView workoutChecked;
        CardView workoutCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.workout_name);
            numberExercises = itemView.findViewById(R.id.workout_number_exercises);
            numberSets = itemView.findViewById(R.id.workout_number_sets);
            layout = itemView.findViewById(R.id.workout_layout);
            workoutChecked = itemView.findViewById(R.id.workout_checked);
            workoutCard = itemView.findViewById(R.id.workout_card);
        }
    }
}
