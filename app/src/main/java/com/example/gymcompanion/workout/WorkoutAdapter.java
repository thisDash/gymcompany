package com.example.gymcompanion.workout;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.Workout;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private static final String TAG = "WorkoutsAdapter";

    private List<Workout> workoutsList;
    private Context mContext;

    public WorkoutAdapter(Context context, List<Workout> workoutsList){
        this.workoutsList = workoutsList;
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
            Intent intent = new Intent(mContext, WorkoutOverviewActivity.class);
            intent.putExtra(Constants.WORKOUT_ID_FIELD, workout.getWorkoutId());
            mContext.startActivity(intent);
        });

        holder.workoutCard.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));
    }

    @Override
    public int getItemCount() {
        return workoutsList.size();
    }

    public void setWorkoutsList(List<Workout> workoutsList){
        this.workoutsList = workoutsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView workoutName;
        TextView numberSets;
        TextView numberExercises;
        ConstraintLayout workoutLayout;
        CardView workoutCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workout_name);
            numberSets = itemView.findViewById(R.id.workout_number_sets);
            numberExercises = itemView.findViewById(R.id.workout_number_exercises);
            workoutLayout = itemView.findViewById(R.id.workout_layout);
            workoutCard = itemView.findViewById(R.id.workout_card);
        }
    }
}
