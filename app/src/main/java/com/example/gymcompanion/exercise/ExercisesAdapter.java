package com.example.gymcompanion.exercise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.Exercise;

import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ViewHolder> {

    private static final String TAG = "ExercisesAdapter";

    private List<Exercise> exercisesList;
    private OnItemClickListener mListener;
    private Context mContext;

    public interface OnItemClickListener{
        void onSelectClick(String exerciseId, String exerciseName);
    }

    public void setOnItemClickListener(ExercisesAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    public ExercisesAdapter(List<Exercise> exercisesList, Context context){
        this.exercisesList = exercisesList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_exercise, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercisesList.get(position);

        holder.infoLayout.setVisibility(View.INVISIBLE);
        holder.name.setText(exercise.getName());
        holder.exerciseCard.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));
    }

    @Override
    public int getItemCount() {
        return exercisesList.size();
    }

    public void setExercisesList(List<Exercise> exercisesList){
        this.exercisesList = exercisesList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        LinearLayout infoLayout;
        ConstraintLayout layout;
        CardView exerciseCard;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.exercise_name);
            layout = itemView.findViewById(R.id.element_exercise_layout);
            infoLayout = itemView.findViewById(R.id.exercise_info_layout);
            exerciseCard = itemView.findViewById(R.id.exercise_card);

            layout.setOnClickListener(view -> {
                if (listener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Exercise exercise = exercisesList.get(position);
                        String exerciseName = exercise.getName();
                        String exerciseId = exercise.getExerciseId();
                        listener.onSelectClick(exerciseId, exerciseName);
                    }
                }
            });
        }
    }
}
