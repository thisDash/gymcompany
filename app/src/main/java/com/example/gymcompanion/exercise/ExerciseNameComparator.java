package com.example.gymcompanion.exercise;

import com.example.gymcompanion.components.Exercise;

import java.util.Comparator;

public class ExerciseNameComparator implements Comparator<Exercise> {

    @Override
    public int compare(Exercise e1, Exercise e2) {
        return e1.getName().compareToIgnoreCase(e2.getName());
    }
}
