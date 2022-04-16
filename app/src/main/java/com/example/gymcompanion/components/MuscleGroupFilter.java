package com.example.gymcompanion.components;

public class MuscleGroupFilter {

    private String muscleGroup;
    private boolean isSelected;

    public MuscleGroupFilter(String muscleGroup, boolean isSelected) {
        this.muscleGroup = muscleGroup;
        this.isSelected = isSelected;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
