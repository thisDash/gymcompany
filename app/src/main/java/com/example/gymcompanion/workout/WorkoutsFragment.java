package com.example.gymcompanion.workout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.MuscleGroupFilter;
import com.example.gymcompanion.components.Workout;
import com.example.gymcompanion.exercise.ExerciseNameComparator;
import com.example.gymcompanion.muscleGroupsFilter.MuscleGroupFiltersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WorkoutsFragment extends Fragment {

    private static final String TAG = "WorkoutsFragment";

    private View mView;

    private RecyclerView mWorkoutsListView;
    private RecyclerView mWorkoutFilters;
    private Button mCreateWorkoutButton;

    private FirebaseAuth auth;
    private MuscleGroupFiltersAdapter filtersAdapter;
    private WorkoutAdapter workoutAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_workouts, container, false);

        getComponents();
        setButtonListeners();
        setWorkoutsList();
        setWorkoutsFilters();

        return mView;
    }

    private void getComponents(){
        mWorkoutsListView = mView.findViewById(R.id.workouts_list_view);
        mCreateWorkoutButton = mView.findViewById(R.id.workouts_create_button);
        mWorkoutFilters = mView.findViewById(R.id.workouts_workouts_filters);
        auth = FirebaseAuth.getInstance();
    }

    private void setButtonListeners(){
        mCreateWorkoutButton.setOnClickListener(v -> goToCreateWorkoutPage());
    }

    private void setWorkoutsList(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mWorkoutsListView.setLayoutManager(layoutManager);
        List<Workout> workouts = new ArrayList();

        workoutAdapter = new WorkoutAdapter(getContext(), workouts);
        mWorkoutsListView.setAdapter(workoutAdapter);

        String userId = auth.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUTS_PATH);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workouts.clear();

                DataSnapshot snap = dataSnapshot.child(Constants.PREMADE_PATH);

                for(DataSnapshot snapshot: snap.getChildren()){
                    Workout workout = snapshot.getValue(Workout.class);
                    workouts.add(workout);
                }

                snap = dataSnapshot.child(userId);

                for(DataSnapshot snapshot: snap.getChildren()){
                    Workout workout = snapshot.getValue(Workout.class);
                    workouts.add(workout);
                }

                Collections.sort(workouts, new WorkoutNameComparator());
                workoutAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setWorkoutsFilters(){
        List<String> muscleGroups = new ArrayList();
        Collections.addAll(muscleGroups, getResources().getStringArray(R.array.muscle_groups));
        List<MuscleGroupFilter> filters = new ArrayList();

        Context context = getContext();

        for(String muscleGroup: muscleGroups)
            filters.add(new MuscleGroupFilter(muscleGroup, false));

        Collections.sort(filters, new FilterNameComparator());

        LinearLayoutManager filtersLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        filtersAdapter = new MuscleGroupFiltersAdapter(filters, context);
        mWorkoutFilters.setLayoutManager(filtersLayoutManager);
        mWorkoutFilters.setAdapter(filtersAdapter);

        filtersAdapter.setOnItemClickListener(muscleGroupsSelected -> {
            List<MuscleGroupFilter> newFiltersOrder = new ArrayList();

            for(String muscleGroup: muscleGroupsSelected){
                newFiltersOrder.add(new MuscleGroupFilter(muscleGroup, true));
            }

            Collections.sort(newFiltersOrder, new FilterNameComparator());

            List<MuscleGroupFilter> nonSelectedFilters = new ArrayList();

            for(String muscleGroup: muscleGroups)
                if(!muscleGroupsSelected.contains(muscleGroup))
                    nonSelectedFilters.add(new MuscleGroupFilter(muscleGroup, false));

            Collections.sort(nonSelectedFilters, new FilterNameComparator());

            for(MuscleGroupFilter filterNotSelected: nonSelectedFilters)
                newFiltersOrder.add(filterNotSelected);

            filtersAdapter.setMuscleGroupFiltersList(newFiltersOrder);
            filtersAdapter.notifyDataSetChanged();

            String userId = auth.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.WORKOUTS_PATH);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<DataSnapshot> snapshots = new LinkedList();

                    DataSnapshot snap = snapshot.child(userId);

                    for(DataSnapshot s: snap.getChildren())
                        snapshots.add(s);

                    snap = snapshot.child(Constants.PREMADE_PATH);

                    for(DataSnapshot s: snap.getChildren())
                        snapshots.add(s);

                    List<Workout> workoutList = new ArrayList();

                    for(DataSnapshot s: snapshots){
                        Workout workout = s.getValue(Workout.class);
                        int i = 0;
                        boolean canBeAdded = true;

                        for(int setsPerMuscleGroup: workout.getSetsPerMuscleGroup()){
                            String muscleGroup = getMuscleGroup(i);

                            if(muscleGroupsSelected.contains(muscleGroup) && setsPerMuscleGroup == 0){
                                canBeAdded = false;
                                break;
                            }

                            i++;
                        }

                        if(canBeAdded)
                            workoutList.add(workout);
                    }

                    Collections.sort(workoutList, new WorkoutNameComparator());
                    workoutAdapter.setWorkoutsList(workoutList);
                    workoutAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

    }

    private String getMuscleGroup(int muscleGroupIndex){

        String muscleGroup = "";

        switch (muscleGroupIndex){
            case Constants.CHEST_INDEX:
                muscleGroup = Constants.CHEST;
                break;
            case Constants.BACK_INDEX:
                muscleGroup = Constants.BACK;
                break;
            case Constants.SHOULDERS_INDEX:
                muscleGroup = Constants.SHOULDERS;
                break;
            case Constants.QUADS_INDEX:
                muscleGroup = Constants.QUADS;
                break;
            case Constants.HAMSTRINGS_INDEX:
                muscleGroup = Constants.HAMSTRINGS;
                break;
            case Constants.TRICEPS_INDEX:
                muscleGroup = Constants.TRICEPS;
                break;
            case Constants.BICEPS_INDEX:
                muscleGroup = Constants.BICEPS;
                break;
            case Constants.CALVES_INDEX:
                muscleGroup = Constants.CALVES;
                break;
            case Constants.GLUTES_INDEX:
                muscleGroup = Constants.GLUTES;
                break;
            case Constants.TRAPS_INDEX:
                muscleGroup = Constants.TRAPS;
                break;
        }

        return muscleGroup;
    }

    private void goToCreateWorkoutPage(){
        Intent intent = new Intent(getActivity(), CreateWorkoutActivity.class);
        startActivity(intent);
    }
}
