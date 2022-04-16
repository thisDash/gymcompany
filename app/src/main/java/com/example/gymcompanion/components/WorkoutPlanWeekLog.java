package com.example.gymcompanion.components;

import java.util.ArrayList;
import java.util.List;

public class WorkoutPlanWeekLog {

    private String workoutPlanWeekLogId;
    private List<String> workoutLogsIdsList;
    private int weeklyVolume;

    public WorkoutPlanWeekLog() {
    }

    public WorkoutPlanWeekLog(String workoutPlanWeekLogId) {
        this.workoutPlanWeekLogId = workoutPlanWeekLogId;
        this.workoutLogsIdsList = new ArrayList();
        this.weeklyVolume = 0;
    }

    public WorkoutPlanWeekLog(String workoutPlanWeekLogId, List<String> workoutLogsIdsList, int weeklyVolume) {
        this.workoutPlanWeekLogId = workoutPlanWeekLogId;
        this.workoutLogsIdsList = workoutLogsIdsList;
        this.weeklyVolume = weeklyVolume;
    }

    public String getWorkoutPlanWeekLogId() {
        return workoutPlanWeekLogId;
    }

    public void setWorkoutPlanWeekLogId(String workoutPlanWeekLogId) {
        this.workoutPlanWeekLogId = workoutPlanWeekLogId;
    }

    public List<String> getWorkoutLogsIdsList() {
        return workoutLogsIdsList;
    }

    public void setWorkoutLogsIdsList(List<String> workoutLogsIdsList) {
        this.workoutLogsIdsList = workoutLogsIdsList;
    }

    public int getWeeklyVolume() {
        return weeklyVolume;
    }

    public void setWeeklyVolume(int weeklyVolume) {
        this.weeklyVolume = weeklyVolume;
    }
}
