package com.example.gymcompanion.plan;

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
import com.example.gymcompanion.components.WorkoutPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlansFragment extends Fragment {

    private View mView;

    private RecyclerView mPlansListView;
    private Button mCreatePlanButton;

    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_plans, container, false);

        getComponents();
        setButtonListeners();
        setPlansList();

        return mView;
    }

    private void getComponents(){
        mPlansListView = mView.findViewById(R.id.plans_list_view);
        mCreatePlanButton = mView.findViewById(R.id.plans_create_button);
        auth = FirebaseAuth.getInstance();
    }

    private void setButtonListeners(){
        mCreatePlanButton.setOnClickListener(v -> goToCreatePlanPage());
    }

    private void setPlansList(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mPlansListView.setLayoutManager(layoutManager);
        List<WorkoutPlan> plans = new ArrayList();

        final PlanAdapter workoutPlanAdapter= new PlanAdapter(getContext(), plans);
        mPlansListView.setAdapter(workoutPlanAdapter);

        String userId = auth.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.PLANS_PATH);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                plans.clear();

                DataSnapshot snap = dataSnapshot.child(Constants.PREMADE_PATH);

                for(DataSnapshot snapshot: snap.getChildren()){
                    WorkoutPlan plan = snapshot.getValue(WorkoutPlan.class);
                    plans.add(plan);
                }

                snap = dataSnapshot.child(userId);

                for(DataSnapshot snapshot: snap.getChildren()){
                    WorkoutPlan plan = snapshot.getValue(WorkoutPlan.class);
                    plans.add(plan);
                }

                Collections.sort(plans, new PlanNameComparator());
                workoutPlanAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void goToCreatePlanPage(){
        Intent intent = new Intent(getActivity(), CreatePlanActivity.class);
        startActivity(intent);
    }
}
