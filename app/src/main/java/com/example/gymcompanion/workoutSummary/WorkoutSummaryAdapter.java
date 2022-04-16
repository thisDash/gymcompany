package com.example.gymcompanion.workoutSummary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.ExerciseDetailed;
import com.example.gymcompanion.components.ExerciseLog;
import com.example.gymcompanion.workoutLog.SetAdapter;

import java.util.ArrayList;
import java.util.List;

public class WorkoutSummaryAdapter extends RecyclerView.Adapter<WorkoutSummaryAdapter.ViewHolder>{

    private static final String TAG = "WorkoutsAdapter";

    private List<ExerciseDetailed> exercisesList;
    private List<ExerciseLog> exerciseLogs;
    private List<SetSummaryAdapter> setAdapterList;
    private Context mContext;

    public WorkoutSummaryAdapter(Context context, List<ExerciseDetailed> exercisesList, List<ExerciseLog> exerciseLogs){
        this.exercisesList = exercisesList;
        this.exerciseLogs = exerciseLogs;
        this.mContext = context;
        this.setAdapterList = new ArrayList();
    }

    @NonNull
    @Override
    public WorkoutSummaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_exercise_log, parent, false);
        return new WorkoutSummaryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutSummaryAdapter.ViewHolder holder, int position) {
        ExerciseDetailed exercise = exercisesList.get(holder.getAdapterPosition());

        holder.exerciseName.setText(exercise.getName());
        holder.numberSets.setText(exercise.getNumberSets());
        holder.lowRepRange.setText(exercise.getLowRepRange());
        holder.highRepRange.setText(exercise.getHighRepRange());
        holder.targetRPE.setText(exercise.getTargetRPE());

        boolean isExpandable = exercise.isExpandable();

        LinearLayoutManager exercisesLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        SetSummaryAdapter setAdapter = new SetSummaryAdapter(Integer.parseInt(exercise.getNumberSets()), exerciseLogs.get(position), mContext);
        holder.setsList.setLayoutManager(exercisesLayoutManager);
        holder.setsList.setAdapter(setAdapter);

        setAdapterList.add(position, setAdapter);

        holder.expandButton.setRotation(isExpandable ? 90: 0);
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE: View.GONE);
    }

    @Override
    public int getItemCount() {
        return exercisesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView exerciseName;
        TextView numberSets;
        TextView lowRepRange;
        TextView highRepRange;
        TextView targetRPE;
        ImageView expandButton;

        RelativeLayout expandableLayout;
        RecyclerView setsList;
        ConstraintLayout exerciseInfoLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_log_exercise_name);
            numberSets = itemView.findViewById(R.id.exercise_log_number_sets);
            lowRepRange = itemView.findViewById(R.id.exercise_log_low_limit_rep_range);
            highRepRange = itemView.findViewById(R.id.exercise_log_high_limit_rep_range);
            targetRPE = itemView.findViewById(R.id.exercise_log_target_rpe);
            setsList = itemView.findViewById(R.id.exercise_log_reps_per_set_list);
            expandButton = itemView.findViewById(R.id.exercise_expand_button);
            expandableLayout = itemView.findViewById(R.id.exercise_log_expandable_section);
            exerciseInfoLayout = itemView.findViewById(R.id.exercise_log_info_layout);

            exerciseInfoLayout.setOnClickListener(view -> {
                ExerciseDetailed exercise = exercisesList.get(getAdapterPosition());
                exercise.setExpandable(!exercise.isExpandable());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}