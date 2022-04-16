package com.example.gymcompanion.workout;

import com.example.gymcompanion.components.MuscleGroupFilter;
import com.example.gymcompanion.components.Workout;

import java.util.Comparator;

public class FilterNameComparator implements Comparator<MuscleGroupFilter> {

    @Override
    public int compare(MuscleGroupFilter f1, MuscleGroupFilter f2) {
        return f1.getMuscleGroup().compareToIgnoreCase(f2.getMuscleGroup());
    }
}
