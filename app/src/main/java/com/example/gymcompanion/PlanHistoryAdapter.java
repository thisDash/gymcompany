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
import com.example.gymcompanion.components.WorkoutPlanLog;
import com.example.gymcompanion.workoutSummary.WorkoutSummaryActivity;

import java.util.List;

public class PlanHistoryAdapter extends RecyclerView.Adapter<PlanHistoryAdapter.ViewHolder> {

    private static final String TAG = "WorkoutsAdapter";

    private List<WorkoutPlanLog> plansList;
    private Context mContext;

    public PlanHistoryAdapter(Context context, List<WorkoutPlanLog> plansList){
        this.plansList = plansList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_plan_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutPlanLog planLog = plansList.get(position);

        holder.layout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));

        //TODO
    }

    @Override
    public int getItemCount() {
        return plansList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView planName;
        TextView planCompletionDate;
        CardView layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            planName = itemView.findViewById(R.id.plan_log_name);
            planCompletionDate = itemView.findViewById(R.id.plan_log_date);
            layout = itemView.findViewById(R.id.plan_log_home_layout);
        }
    }
}
