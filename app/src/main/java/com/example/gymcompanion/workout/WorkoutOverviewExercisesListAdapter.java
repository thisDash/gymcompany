package com.example.gymcompanion.workout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.ExerciseDetailed;

import java.util.List;

public class WorkoutOverviewExercisesListAdapter extends RecyclerView.Adapter<WorkoutOverviewExercisesListAdapter.ViewHolder> {

    private static final String TAG = "WorkoutOverviewExercisesListAdapter";

    private List<ExerciseDetailed> exercisesList;
    private Context mContext;

    public WorkoutOverviewExercisesListAdapter(List<ExerciseDetailed> exercisesList, Context context){
        this.exercisesList = exercisesList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseDetailed exercise = exercisesList.get(position);

        holder.name.setText(exercise.getName());
        holder.numberSets.setText(exercise.getNumberSets());
        holder.lowRepRange.setText(exercise.getLowRepRange());
        holder.highRepRange.setText(exercise.getHighRepRange());
        holder.targetRPE.setText(exercise.getTargetRPE());
        holder.exerciseChecked.setVisibility(View.GONE);
        holder.exerciseCard.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));
    }

    @Override
    public int getItemCount() {
        return exercisesList.size();
    }

    public List<ExerciseDetailed> getExercisesInWorkout(){
        return exercisesList;
    }

    public void addExerciseToWorkout(ExerciseDetailed exercise){
        exercisesList.add(exercise);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView numberSets;
        TextView lowRepRange;
        TextView highRepRange;
        TextView targetRPE;
        ImageView exerciseChecked;
        CardView exerciseCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.exercise_name);
            numberSets = itemView.findViewById(R.id.number_sets);
            lowRepRange = itemView.findViewById(R.id.low_limit_rep_range);
            highRepRange = itemView.findViewById(R.id.high_limit_rep_range);
            targetRPE = itemView.findViewById(R.id.target_rpe);
            exerciseChecked = itemView.findViewById(R.id.exercise_checked);
            exerciseCard = itemView.findViewById(R.id.exercise_card);
        }
    }
}
