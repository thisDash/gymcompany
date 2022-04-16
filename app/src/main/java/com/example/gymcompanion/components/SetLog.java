package com.example.gymcompanion.components;

public class SetLog {

    private int numberSet;
    private float weightUsed;
    private int numberReps;
    private int setRpe;

    public SetLog() {
    }

    public SetLog(int numberSet){
        this.numberSet = numberSet;
        this.setRpe = Constants.RPE_PRESET;
    }

    public SetLog(int numberSet, float weightUsed, int numberReps, int setRpe) {
        this.numberSet = numberSet;
        this.weightUsed = weightUsed;
        this.numberReps = numberReps;
        this.setRpe = setRpe;
    }

    public float getWeightUsed() {
        return weightUsed;
    }

    public void setWeightUsed(float weightUsed) {
        this.weightUsed = weightUsed;
    }

    public int getNumberReps() {
        return numberReps;
    }

    public void setNumberReps(int numberReps) {
        this.numberReps = numberReps;
    }

    public int getNumberSet() {
        return numberSet;
    }

    public void setNumberSet(int numberSet) {
        this.numberSet = numberSet;
    }

    public int getSetRpe() {
        return setRpe;
    }

    public void setSetRpe(int setRpe) {
        this.setRpe = setRpe;
    }
}
