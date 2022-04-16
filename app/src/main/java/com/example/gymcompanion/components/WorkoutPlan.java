package com.example.gymcompanion.components;

import java.util.List;

public class WorkoutPlan {

    private String planId;
    private String planName;
    private int numberWeeks;
    private boolean includeIntroWeek;
    private boolean includeDeloadWeek;
    private List<String> workoutsIds;

    public WorkoutPlan(){}

    public WorkoutPlan(String planId, String planName, int numberWeeks, boolean includeIntroWeek, boolean includeDeloadWeek, List<String> workoutsIds) {
        this.planId = planId;
        this.planName = planName;
        this.numberWeeks = numberWeeks;
        this.includeIntroWeek = includeIntroWeek;
        this.includeDeloadWeek = includeDeloadWeek;
        this.workoutsIds = workoutsIds;
    }

    public String getPlanId() {
        return planId;
    }

    public String getPlanName() {
        return planName;
    }

    public int getNumberWeeks() {
        return numberWeeks;
    }

    public boolean isIncludeIntroWeek() {
        return includeIntroWeek;
    }

    public boolean isIncludeDeloadWeek() {
        return includeDeloadWeek;
    }

    public List<String> getWorkoutsIds() {
        return workoutsIds;
    }

    public int getNumberWorkouts(){
        return workoutsIds.size();
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public void setNumberWeeks(int numberWeeks) {
        this.numberWeeks = numberWeeks;
    }

    public void setIncludeIntroWeek(boolean includeIntroWeek) {
        this.includeIntroWeek = includeIntroWeek;
    }

    public void setIncludeDeloadWeek(boolean includeDeloadWeek) {
        this.includeDeloadWeek = includeDeloadWeek;
    }

    public void setWorkoutsIds(List<String> workoutsIds) {
        this.workoutsIds = workoutsIds;
    }
}
