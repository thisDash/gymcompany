package com.example.gymcompanion.components;

import java.util.List;

public class WorkoutPlanLog {

    private String workoutPlanLogId;
    private String planId;
    private String planName;
    private List<String> workoutsIdsList;
    private List<WorkoutPlanWeekLog> weeklyLogsList;
    private int workoutsMissed;
    private int workoutsToGo;
    private int currentDay;
    private int currentWeek;
    private String completionDateCode;
    private boolean includesDeloadWeek;

    public WorkoutPlanLog() {
    }

    public WorkoutPlanLog(String workoutPlanLogId, String planId, String planName,
                          List<String> workoutsIdsList, List<WorkoutPlanWeekLog> weeklyLogsList,
                          boolean includesDeloadWeek) {
        this.workoutPlanLogId = workoutPlanLogId;
        this.planId = planId;
        this.planName = planName;
        this.workoutsIdsList = workoutsIdsList;
        this.weeklyLogsList = weeklyLogsList;
        this.includesDeloadWeek = includesDeloadWeek;
        this.workoutsMissed = 0;
        this.workoutsToGo = getNumberWeeks() * getNumberDays();
        this.currentDay = 0;
        this.currentWeek = 0;
        this.completionDateCode = "";
    }

    public WorkoutPlanLog(String workoutPlanLogId, String planId, String planName,
                          List<String> workoutsIdsList, List<WorkoutPlanWeekLog> weeklyLogsList,
                          int workoutsMissed, int workoutsToGo, int currentDay, int currentWeek,
                          boolean includesDeloadWeek, String completionDateCode) {
        this.workoutPlanLogId = workoutPlanLogId;
        this.planId = planId;
        this.planName = planName;
        this.workoutsIdsList = workoutsIdsList;
        this.weeklyLogsList = weeklyLogsList;
        this.workoutsMissed = workoutsMissed;
        this.workoutsToGo = workoutsToGo;
        this.currentDay = currentDay;
        this.currentWeek = currentWeek;
        this.includesDeloadWeek = includesDeloadWeek;
        this.completionDateCode = completionDateCode;
    }

    public int getNumberWeeks(){
        return weeklyLogsList.size();
    }

    public int getNumberDays(){
        return workoutsIdsList.size();
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getWorkoutPlanLogId() {
        return workoutPlanLogId;
    }

    public void setWorkoutPlanLogId(String workoutPlanLogId) {
        this.workoutPlanLogId = workoutPlanLogId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public List<String> getWorkoutsIdsList() {
        return workoutsIdsList;
    }

    public void setWorkoutsIdsList(List<String> workoutsIdsList) {
        this.workoutsIdsList = workoutsIdsList;
    }

    public List<WorkoutPlanWeekLog> getWeeklyLogsList() {
        return weeklyLogsList;
    }

    public void setWeeklyLogsList(List<WorkoutPlanWeekLog> weeklyLogsList) {
        this.weeklyLogsList = weeklyLogsList;
    }

    public int getWorkoutsMissed() {
        return workoutsMissed;
    }

    public void setWorkoutsMissed(int workoutsMissed) {
        this.workoutsMissed = workoutsMissed;
    }

    public int getWorkoutsToGo() {
        return workoutsToGo;
    }

    public void setWorkoutsToGo(int workoutsToGo) {
        this.workoutsToGo = workoutsToGo;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(int currentWeek) {
        this.currentWeek = currentWeek;
    }

    public boolean isIncludesDeloadWeek() {
        return includesDeloadWeek;
    }

    public void setIncludesDeloadWeek(boolean includesDeloadWeek) {
        this.includesDeloadWeek = includesDeloadWeek;
    }

    public String getCompletionDateCode() {
        return completionDateCode;
    }

    public void setCompletionDateCode(String completionDateCode) {
        this.completionDateCode = completionDateCode;
    }
}
