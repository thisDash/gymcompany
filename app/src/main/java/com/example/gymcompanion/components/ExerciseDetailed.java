package com.example.gymcompanion.components;

public class ExerciseDetailed {

    private String exerciseDetailedId;
    private String exerciseId;
    private String name;
    private String numberSets;
    private String lowRepRange;
    private String highRepRange;
    private String targetRPE;
    private boolean expandable;

    public ExerciseDetailed(){}

    public ExerciseDetailed(String exerciseDetailedId, String exerciseId, String name,
                            String numberSets, String lowRepRange, String highRepRange,
                            String targetRPE) {
        this.exerciseDetailedId = exerciseDetailedId;
        this.exerciseId = exerciseId;
        this.name = name;
        this.numberSets = numberSets;
        this.lowRepRange = lowRepRange;
        this.highRepRange = highRepRange;
        this.targetRPE = targetRPE;
        expandable = false;
    }

    public String getExerciseDetailedId() {
        return exerciseDetailedId;
    }

    public void setExerciseDetailedId(String exerciseDetailedId) {
        this.exerciseDetailedId = exerciseDetailedId;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberSets() {
        return numberSets;
    }

    public void setNumberSets(String numberSets) {
        this.numberSets = numberSets;
    }

    public String getLowRepRange() {
        return lowRepRange;
    }

    public void setLowRepRange(String lowRepRange) {
        this.lowRepRange = lowRepRange;
    }

    public String getHighRepRange() {
        return highRepRange;
    }

    public void setHighRepRange(String highRepRange) {
        this.highRepRange = highRepRange;
    }

    public String getTargetRPE() {
        return targetRPE;
    }

    public void setTargetRPE(String targetRPE) {
        this.targetRPE = targetRPE;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }
}
