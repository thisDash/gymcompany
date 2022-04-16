package com.example.gymcompanion.barChart;

import android.content.res.Resources;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.SetsPerMuscleGroup;
import com.example.gymcompanion.components.SetsPerMuscleGroupComparator;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BarChartBuilder {

    private static final Float GRAPH_DATA_SIZE = 12f;

    public BarChartBuilder(){}

    public void setupBarChart(Resources resources, BarChart mBarChart){
        List<String> muscleGroups = new ArrayList();
        Collections.addAll(muscleGroups, resources.getStringArray(R.array.muscle_groups));

        List<BarEntry> entries = new ArrayList();
        List<String> xValues = new ArrayList();
        int i = 0;

        for(String muscleGroup: muscleGroups){
            DataObject object = new DataObject(muscleGroup, 0);
            entries.add(new BarEntry(i++, object.getYValue()));
            xValues.add(object.getXValue());
        }

        BarDataSet dataSet = new BarDataSet(entries,"");
        dataSet.setColor(resources.getColor(R.color.white));
        BarData data = new BarData(dataSet);
        data.setValueTextSize(GRAPH_DATA_SIZE);
        data.setValueTextColor(resources.getColor(R.color.white));
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) Math.floor(value) + " Sets";
            }
        });

        mBarChart.setData(data);
        mBarChart.setHighlightPerTapEnabled(false);

        mBarChart.setTouchEnabled(true);
        mBarChart.setDragEnabled(true);
        mBarChart.setVisibleXRangeMaximum(4);
        mBarChart.setPinchZoom(false);
        mBarChart.setScaleEnabled(false);
        mBarChart.setDrawGridBackground(false);
        mBarChart.setExtraBottomOffset(15);
        mBarChart.setExtraTopOffset(15);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.setBackgroundColor(resources.getColor(R.color.dark_gray));

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setTextSize(GRAPH_DATA_SIZE);
        xAxis.setTextColor(resources.getColor(R.color.white));
        xAxis.setLabelCount(entries.size(),false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setSpaceMin(0.5f);

        YAxis yAxis = mBarChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawLabels(false);
        yAxis.setTextColor(resources.getColor(R.color.white));
        yAxis.setAxisMinimum(0f);
        yAxis.setGranularity(1f);

        yAxis = mBarChart.getAxisRight();
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawLabels(false);
        mBarChart.animateY(2000);
    }

    public void updateBarChart(Resources resources, Map<String, Integer> setsPerMuscle, BarChart mBarChart){
        List<String> muscleGroups = new ArrayList();
        Collections.addAll(muscleGroups, resources.getStringArray(R.array.muscle_groups));

        List<BarEntry> entries = new ArrayList();
        int i = 0;

        List<SetsPerMuscleGroup> setsPerMuscleGroupList = new ArrayList();

        for(String muscleGroup: muscleGroups){
            int numberSets = setsPerMuscle.get(muscleGroup);
            setsPerMuscleGroupList.add(new SetsPerMuscleGroup(numberSets, muscleGroup));
        }

        Collections.sort(setsPerMuscleGroupList, new SetsPerMuscleGroupComparator());
        List<String> xValues = new ArrayList();

        for(SetsPerMuscleGroup setsPerMuscleGroup: setsPerMuscleGroupList){
            entries.add(new BarEntry(i++, setsPerMuscleGroup.getNumberSets()));
            xValues.add(setsPerMuscleGroup.getMuscleGroup());
        }

        BarDataSet dataSet = new BarDataSet(entries,"");
        dataSet.setColor(resources.getColor(R.color.white));

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.2f);
        data.setValueTextColor(resources.getColor(R.color.white));
        data.setValueTextSize(GRAPH_DATA_SIZE);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) Math.floor(value) + " Sets";
            }
        });

        mBarChart.setData(data);
        mBarChart.notifyDataSetChanged();
        mBarChart.invalidate();
        mBarChart.animateY(2000);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
    }

    public void updateBarChart(Resources resources, List<Integer> setsPerMuscle, BarChart mBarChart){
        List<BarEntry> entries = new ArrayList();
        int i = 0;

        List<SetsPerMuscleGroup> setsPerMuscleGroupList = new ArrayList();
        List<String> muscleGroups = new ArrayList();
        Collections.addAll(muscleGroups, resources.getStringArray(R.array.muscle_groups));

        for(int sets: setsPerMuscle){
            setsPerMuscleGroupList.add(new SetsPerMuscleGroup(sets, muscleGroups.get(i++)));
        }

        Collections.sort(setsPerMuscleGroupList, new SetsPerMuscleGroupComparator());
        List<String> xValues = new ArrayList();

        i = 0;

        for(SetsPerMuscleGroup setsPerMuscleGroup: setsPerMuscleGroupList){
            entries.add(new BarEntry(i++, setsPerMuscleGroup.getNumberSets()));
            xValues.add(setsPerMuscleGroup.getMuscleGroup());
        }

        BarDataSet dataSet = new BarDataSet(entries,"");
        dataSet.setColor(resources.getColor(R.color.white));

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.2f);
        data.setValueTextColor(resources.getColor(R.color.white));
        data.setValueTextSize(GRAPH_DATA_SIZE);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) Math.floor(value) + " Sets";
            }
        });

        mBarChart.setData(data);
        mBarChart.notifyDataSetChanged();
        mBarChart.invalidate();

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
    }
}
