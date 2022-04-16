package com.example.gymcompanion;

import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.components.WorkoutPlanLog;

import java.util.Comparator;

public class PlanLogComparator implements Comparator<WorkoutPlanLog> {

    @Override
    public int compare(WorkoutPlanLog w1, WorkoutPlanLog w2) {
        return w1.getCompletionDateCode().compareToIgnoreCase(w2.getCompletionDateCode());
    }
}
