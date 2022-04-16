package com.example.gymcompanion.components;

import java.util.List;

public class ExerciseLog {

    private String exerciseId;
    private List<SetLog> setsList;
    private String exerciseName;

    public ExerciseLog() {
    }

    public ExerciseLog(String exerciseId, List<SetLog> setsList, String exerciseName) {
        this.exerciseId = exerciseId;
        this.setsList = setsList;
        this.exerciseName = exerciseName;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public List<SetLog> getSetsList() {
        return setsList;
    }

    public void setSetsList(List<SetLog> setsList) {
        this.setsList = setsList;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public void setWeightForSet(int numberSet, float weightUsed){
        setsList.get(numberSet).setWeightUsed(weightUsed);
    }

    public void setRepsForSet(int numberSet, int numberReps){
        setsList.get(numberSet).setNumberReps(numberReps);
    }
}
