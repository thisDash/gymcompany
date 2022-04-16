package com.example.gymcompanion.components;

import java.util.List;

public class WorkoutLog {

    private String workoutLogId;
    private String workoutId;
    private List<ExerciseLog> exercisesLogList;
    private String datePerformed;
    private String workoutName;
    private int totalVolume;
    private String timeElapsed;

    public WorkoutLog() {
    }

    public WorkoutLog(String workoutLogId, String workoutId, List<ExerciseLog> exercisesLogList,
                      String datePerformed, String workoutName, int totalVolume, String timeElapsed) {
        this.workoutLogId = workoutLogId;
        this.workoutId = workoutId;
        this.exercisesLogList = exercisesLogList;
        this.datePerformed = datePerformed;
        this.workoutName = workoutName;
        this.totalVolume = totalVolume;
        this.timeElapsed = timeElapsed;
    }

    public String getWorkoutLogId() {
        return workoutLogId;
    }

    public void setWorkoutLogId(String workoutLogId) {
        this.workoutLogId = workoutLogId;
    }

    public String getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }

    public List<ExerciseLog> getExercisesLogList() {
        return exercisesLogList;
    }

    public void setExercisesLogList(List<ExerciseLog> exercisesLogList) {
        this.exercisesLogList = exercisesLogList;
    }

    public String getDatePerformed() {
        return datePerformed;
    }

    public void setDatePerformed(String datePerformed) {
        this.datePerformed = datePerformed;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public int getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(int totalVolume) {
        this.totalVolume = totalVolume;
    }

    public String getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(String timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
}
