package com.example.gymcompanion.components;

public class SetsPerMuscleGroup {

    private int numberSets;
    private String muscleGroup;

    public SetsPerMuscleGroup(int numberSets, String muscleGroup) {
        this.numberSets = numberSets;
        this.muscleGroup = muscleGroup;
    }

    public int getNumberSets() {
        return numberSets;
    }

    public void setNumberSets(int numberSets) {
        this.numberSets = numberSets;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }
}
