package com.example.gymcompanion.components;

import java.util.Comparator;

public class SetsPerMuscleGroupComparator implements Comparator<SetsPerMuscleGroup> {

    @Override
    public int compare(SetsPerMuscleGroup s1, SetsPerMuscleGroup s2) {
        return Integer.compare(s2.getNumberSets(), s1.getNumberSets());
    }
}
