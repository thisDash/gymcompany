package com.example.gymcompanion.workout;

import com.example.gymcompanion.components.Workout;

import java.util.Comparator;

public class WorkoutNameComparator implements Comparator<Workout> {

    @Override
    public int compare(Workout w1, Workout w2) {
        return w1.getWorkoutName().compareToIgnoreCase(w2.getWorkoutName());
    }
}
