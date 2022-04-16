package com.example.gymcompanion.plan;

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
import com.example.gymcompanion.components.WorkoutPlan;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private static final String TAG = "PlanAdapter";

    private List<WorkoutPlan> plansList;
    private Context mContext;

    public PlanAdapter(Context context, List<WorkoutPlan> plansList){
        this.mContext = context;
        this.plansList = plansList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutPlan plan = plansList.get(position);

        holder.planName.setText(plan.getPlanName());
        holder.numberWorkouts.setText(String.valueOf(plan.getNumberWorkouts()));
        holder.numberWeeks.setText(String.valueOf(plan.getNumberWeeks()));

        if(plan.getNumberWorkouts() == 1)
            holder.numberWorkoutsText.setText(" Workout per Week | ");

        holder.planLayout.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PlanOverviewActivity.class);
            intent.putExtra(Constants.PLAN_ID_FIELD, plan.getPlanId());
            mContext.startActivity(intent);
        });

        holder.planCard.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));
    }

    @Override
    public int getItemCount() {
        return plansList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView planName;
        TextView numberWorkouts;
        TextView numberWeeks;
        TextView numberWorkoutsText;
        ConstraintLayout planLayout;
        CardView planCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            planName = itemView.findViewById(R.id.plan_name);
            numberWorkouts = itemView.findViewById(R.id.plan_number_workouts_per_week);
            numberWeeks = itemView.findViewById(R.id.plan_number_weeks);
            numberWorkoutsText = itemView.findViewById(R.id.plan_number_workouts_per_week_text);
            planLayout = itemView.findViewById(R.id.plan_layout);
            planCard = itemView.findViewById(R.id.plan_card);
        }
    }
}
