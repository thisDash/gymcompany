package com.example.gymcompanion.plan;

import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.components.WorkoutPlan;

import java.util.Comparator;

public class PlanNameComparator implements Comparator<WorkoutPlan> {

    @Override
    public int compare(WorkoutPlan p1, WorkoutPlan p2) {
        return p1.getPlanName().compareToIgnoreCase(p2.getPlanName());
    }
}
