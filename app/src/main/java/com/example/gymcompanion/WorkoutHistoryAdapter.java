package com.example.gymcompanion;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.ExerciseLog;
import com.example.gymcompanion.components.SetLog;
import com.example.gymcompanion.components.WorkoutLog;
import com.example.gymcompanion.workoutSummary.WorkoutSummaryActivity;

import java.util.List;

public class WorkoutHistoryAdapter extends RecyclerView.Adapter<WorkoutHistoryAdapter.ViewHolder> {

    private static final String TAG = "WorkoutsAdapter";

    private List<WorkoutLog> workoutsList;
    private Context mContext;

    public WorkoutHistoryAdapter(Context context, List<WorkoutLog> workoutsList){
        this.workoutsList = workoutsList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_workout_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutLog workout = workoutsList.get(position);

        int numberSets = 0;
        int numberReps = 0;
        int weightLifted = 0;

        for(ExerciseLog log: workout.getExercisesLogList()) {

            for(SetLog setLog: log.getSetsList()){
                int reps = setLog.getNumberReps();
                int weight = (int)(setLog.getNumberReps() * setLog.getWeightUsed());

                numberReps += reps;
                weightLifted += weight;

                if(reps != 0 && weight != 0)
                    numberSets++;
            }
        }

        holder.workoutName.setText(workout.getWorkoutName());
        holder.numberSets.setText(String.valueOf(numberSets));
        holder.numberReps.setText(String.valueOf(numberReps));
        holder.weightLifted.setText(String.valueOf(weightLifted));
        holder.date.setText(workout.getDatePerformed());
        holder.duration.setText(workout.getTimeElapsed());
        holder.layout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));

        holder.layout.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, WorkoutSummaryActivity.class);
            intent.putExtra(Constants.WORKOUT_LOG_ID_FIELD, workout.getWorkoutLogId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return workoutsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView workoutName;
        TextView numberSets;
        TextView numberReps;
        TextView weightLifted;
        TextView date;
        TextView duration;
        CardView layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workout_log_name);
            numberSets = itemView.findViewById(R.id.workout_log_number_sets);
            numberReps = itemView.findViewById(R.id.workout_log_number_reps);
            weightLifted = itemView.findViewById(R.id.workout_log_weight_lifted);
            date = itemView.findViewById(R.id.workout_log_date);
            duration = itemView.findViewById(R.id.workout_log_duration);
            layout = itemView.findViewById(R.id.workout_log_home_layout);
        }
    }
}
