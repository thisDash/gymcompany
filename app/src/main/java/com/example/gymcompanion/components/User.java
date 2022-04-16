package com.example.gymcompanion.components;

public class User {

    private String userName;
    private String currentPlanId;
    private String currentPlanLogId;

    public User(){
    }

    public User(String userName){
        this.userName = userName;
        this.currentPlanId = "";
        this.currentPlanLogId = "";
    }

    public User(String userName, String currentPlanId, String currentPlanLogId) {
        this.userName = userName;
        this.currentPlanId = currentPlanId;
        this.currentPlanLogId = currentPlanLogId;
    }

    public String getUserName() {
        return userName;
    }

    public String getCurrentPlanId() {
        return currentPlanId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCurrentPlanId(String currentPlanId) {
        this.currentPlanId = currentPlanId;
    }

    public String getCurrentPlanLogId() {
        return currentPlanLogId;
    }

    public void setCurrentPlanLogId(String currentPlanLogId) {
        this.currentPlanLogId = currentPlanLogId;
    }
}
