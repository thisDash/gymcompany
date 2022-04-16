package com.example.gymcompanion.components;

import java.util.List;

public class Exercise {

    private String exerciseId;
    private String name;
    private List<String> muscleGroupsWorked;

    public Exercise(){}

    public Exercise(String exerciseId, String name, List<String> muscleGroupsWorked) {
        this.exerciseId = exerciseId;
        this.name = name;
        this.muscleGroupsWorked = muscleGroupsWorked;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public String getName() {
        return name;
    }

    public List<String> getMuscleGroupsWorked() {
        return muscleGroupsWorked;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMuscleGroupsWorked(List<String> muscleGroupsWorked) {
        this.muscleGroupsWorked = muscleGroupsWorked;
    }
}
