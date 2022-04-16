package com.example.gymcompanion.components;

import java.util.List;

public class Workout {

    private String workoutId;
    private String workoutName;
    private String numberExercises;
    private String numberSets;
    private List<ExerciseDetailed> exerciseDetailedList;
    private List<Integer> setsPerMuscleGroup;

    public Workout(){}

    public Workout(String workoutId, String workoutName, String numberExercises, String numberSets, List<ExerciseDetailed> exerciseDetailedList, List<Integer> setsPerMuscleGroup) {
        this.workoutId = workoutId;
        this.workoutName = workoutName;
        this.numberExercises = numberExercises;
        this.numberSets = numberSets;
        this.exerciseDetailedList = exerciseDetailedList;
        this.setsPerMuscleGroup = setsPerMuscleGroup;
    }

    public String getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getNumberExercises() {
        return numberExercises;
    }

    public void setNumberExercises(String numberExercises) {
        this.numberExercises = numberExercises;
    }

    public String getNumberSets() {
        return numberSets;
    }

    public void setNumberSets(String numberSets) {
        this.numberSets = numberSets;
    }

    public List<ExerciseDetailed> getExerciseDetailedList() {
        return exerciseDetailedList;
    }

    public void setExerciseDetailedList(List<ExerciseDetailed> exerciseDetailedList) {
        this.exerciseDetailedList = exerciseDetailedList;
    }

    public List<Integer> getSetsPerMuscleGroup() {
        return setsPerMuscleGroup;
    }

    public void setSetsPerMuscleGroup(List<Integer> setsPerMuscleGroup) {
        this.setsPerMuscleGroup = setsPerMuscleGroup;
    }
}
