package com.example.gymcompanion.barChart;

public class DataObject {

    private String xValue;
    private float yValue;

    public DataObject(String xValue, float yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public String getXValue() {
        return xValue;
    }

    public void setXValue(String xValue) {
        this.xValue = xValue;
    }

    public float getYValue() {
        return yValue;
    }

    public void setYValue(float yValue) {
        this.yValue = yValue;
    }
}
