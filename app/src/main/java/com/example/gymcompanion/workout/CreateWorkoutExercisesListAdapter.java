package com.example.gymcompanion.workout;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.ExerciseDetailed;

import java.util.ArrayList;
import java.util.List;

public class CreateWorkoutExercisesListAdapter extends RecyclerView.Adapter<CreateWorkoutExercisesListAdapter.ViewHolder> {

    private static final String TAG = "ExercisesAdapter";

    private List<ExerciseDetailed> exercisesList;
    private List<String> chosenExercises;
    private Context mContext;

    public CreateWorkoutExercisesListAdapter(List<ExerciseDetailed> exercisesList, Context context){
        this.exercisesList = exercisesList;
        this.chosenExercises = new ArrayList();
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

        Log.d(TAG, "onBindViewHolder: Exercise: " + exercise.getName());

        holder.name.setText(exercise.getName());
        holder.numberSets.setText(exercise.getNumberSets());
        holder.lowRepRange.setText(exercise.getLowRepRange());
        holder.highRepRange.setText(exercise.getHighRepRange());
        holder.targetRPE.setText(exercise.getTargetRPE());
        holder.exerciseChecked.setVisibility(View.GONE);

        holder.exerciseCard.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));

        holder.layout.setOnClickListener(view -> {

            if(chosenExercises.contains(String.valueOf(position))){
                holder.exerciseChecked.setVisibility(View.GONE);
                chosenExercises.remove(String.valueOf(position));
                Log.d(TAG, "onBindViewHolder: not chosen");

            } else {
                holder.exerciseChecked.setVisibility(View.VISIBLE);
                chosenExercises.add(String.valueOf(position));
                Log.d(TAG, "onBindViewHolder: chosen");
            }

            Log.d(TAG, "onBindViewHolder: have " + chosenExercises.size() + " selected");
        });
    }

    @Override
    public int getItemCount() {
        return exercisesList.size();
    }

    public List<String> getChosenExercisesList(){
        return chosenExercises;
    }

    public List<ExerciseDetailed> getExercisesInWorkout(){
        return exercisesList;
    }

    public void addExerciseToWorkout(ExerciseDetailed exercise){
        exercisesList.add(exercise);
    }

    public void deleteChosenExercise(int position){
        exercisesList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, exercisesList.size());
    }

    public void clearChosenList(){
        chosenExercises = new ArrayList();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView numberSets;
        TextView lowRepRange;
        TextView highRepRange;
        TextView targetRPE;
        ConstraintLayout layout;
        ImageView exerciseChecked;
        CardView exerciseCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.exercise_name);
            numberSets = itemView.findViewById(R.id.number_sets);
            lowRepRange = itemView.findViewById(R.id.low_limit_rep_range);
            highRepRange = itemView.findViewById(R.id.high_limit_rep_range);
            targetRPE = itemView.findViewById(R.id.target_rpe);
            layout = itemView.findViewById(R.id.element_exercise_layout);
            exerciseChecked = itemView.findViewById(R.id.exercise_checked);
            exerciseCard = itemView.findViewById(R.id.exercise_card);
        }
    }
}
