package com.example.gymcompanion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.authentication.LoginActivity;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.WorkoutLog;
import com.example.gymcompanion.components.WorkoutPlanLog;
import com.example.gymcompanion.plan.CreatePlanActivity;
import com.example.gymcompanion.plan.CurrentPlanActivity;
import com.example.gymcompanion.workout.CreateWorkoutActivity;
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
import java.util.Stack;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private View mView;

    private TextView mUserName;
    private TextView mPlanName;
    private TextView mPlanCurrentDay;
    private TextView mPlanNumberDays;
    private TextView mPlanCurrentWeek;
    private TextView mPlanNumberWeeks;
    private Button mCreateWorkoutButton;
    private Button mCreatePlanButton;
    private CardView mPlanExistsLayout;
    private CardView mNoPlanExistsLayout;
    private CardView mNoWorkoutHistoryLayout;
    private CardView mNoPlanHistoryLayout;
    private RecyclerView mWorkoutHistory;
    private RecyclerView mPlanHistory;
    private ImageView mHomeMenu;

    private DatabaseReference reference;
    private FirebaseAuth auth;

    private NoPlanListener listener;

    public interface NoPlanListener {
        void onButtonClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof NoPlanListener)
            listener = (NoPlanListener) context;
        else
            throw new RuntimeException(context.toString() +
                    "must implement NoPlanListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(TAG, "onCreateView: started Home Fragment");

        getComponents();
        setButtonListeners();

        getUser();
        getWorkoutHistory();
        getPlanHistory();

        return mView;
    }

    private void getComponents(){
        auth = FirebaseAuth.getInstance();
        mUserName = mView.findViewById(R.id.home_username);
        mCreateWorkoutButton = mView.findViewById(R.id.home_create_workout_button);
        mCreatePlanButton = mView.findViewById(R.id.home_create_plan_button);
        mWorkoutHistory = mView.findViewById(R.id.home_workout_history_list);
        mPlanExistsLayout = mView.findViewById(R.id.home_current_plan_card);
        mNoPlanExistsLayout = mView.findViewById(R.id.home_no_current_plan_card);
        mHomeMenu = mView.findViewById(R.id.home_username_logout);
        mNoWorkoutHistoryLayout = mView.findViewById(R.id.home_no_workout_history_card);
        mPlanHistory = mView.findViewById(R.id.home_plan_history_list);
        mNoPlanHistoryLayout = mView.findViewById(R.id.home_no_plan_history_card);
    }

    private void setButtonListeners(){
        mCreateWorkoutButton.setOnClickListener(v -> goToCreateWorkoutPage());
        mCreatePlanButton.setOnClickListener(v -> goToCreatePlanPage());
        mHomeMenu.setOnClickListener(v -> openLogoutPopup());
    }

    private void openLogoutPopup(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        View popupView = getLayoutInflater().inflate(R.layout.popup_logout, null);
        Button yesButton = popupView.findViewById(R.id.popup_logout_yes_button);
        Button noButton = popupView.findViewById(R.id.popup_logout_no_button);

        dialogBuilder.setView(popupView);
        Dialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        yesButton.setOnClickListener(view -> {
            dialog.dismiss();
            auth.signOut();
            getContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(Constants.PREF_EMAIL, "")
                    .putString(Constants.PREF_PASSWORD, "")
                    .apply();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        noButton.setOnClickListener(view -> dialog.dismiss());
    }

    private void getPlanComponents(){
        mPlanName = mView.findViewById(R.id.current_plan_name);
        mPlanCurrentDay = mView.findViewById(R.id.current_plan_current_day);
        mPlanNumberDays = mView.findViewById(R.id.current_plan_number_workouts);
        mPlanCurrentWeek = mView.findViewById(R.id.current_plan_current_week);
        mPlanNumberWeeks = mView.findViewById(R.id.current_plan_number_weeks);
    }

    private void getUser(){
        String userId = auth.getUid();
        Context context = getContext();

        reference = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_PATH).child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child(Constants.USERNAME_FIELD).getValue(String.class);
                String currentPlanLogId = snapshot
                        .child(Constants.CURRENT_PLAN_LOG_ID_FIELD)
                        .getValue(String.class);

                mUserName.setText(userName);

                if(currentPlanLogId.isEmpty()) {
                    mPlanExistsLayout.setVisibility(View.GONE);
                    mNoPlanExistsLayout.setVisibility(View.VISIBLE);
                    mNoPlanExistsLayout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));
                    mNoPlanExistsLayout.setOnClickListener(v -> goToSelectPlanPage());
                }

                else {
                    mPlanExistsLayout.setVisibility(View.VISIBLE);
                    mPlanExistsLayout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));
                    mPlanExistsLayout.setOnClickListener(v -> goToCurrentPlanPage());
                    mNoPlanExistsLayout.setVisibility(View.GONE);
                    getPlanComponents();
                    getPlanInfo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPlanInfo(){
        String userId = auth.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS_PATH)
                .child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String planLogId = snapshot
                        .child(Constants.CURRENT_PLAN_LOG_ID_FIELD)
                        .getValue(String.class);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.PLAN_LOGS_PATH)
                        .child(userId)
                        .child(planLogId);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        WorkoutPlanLog planLog = snapshot.getValue(WorkoutPlanLog.class);
                        int currentDay = planLog.getCurrentDay() + 1;
                        int currentWeek = planLog.getCurrentWeek() + 1;

                        mPlanName.setText(planLog.getPlanName());
                        mPlanCurrentDay.setText(String.valueOf(currentDay));
                        mPlanNumberDays.setText(String.valueOf(planLog.getNumberDays()));
                        mPlanCurrentWeek.setText(String.valueOf(currentWeek));
                        mPlanNumberWeeks.setText(String.valueOf(planLog.getNumberWeeks()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getWorkoutHistory(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                RecyclerView.HORIZONTAL,
                false);

        mWorkoutHistory.setLayoutManager(layoutManager);
        List<WorkoutLog> workouts = new ArrayList();

        final WorkoutHistoryAdapter workoutAdapter = new WorkoutHistoryAdapter(getContext(), workouts);
        mWorkoutHistory.setAdapter(workoutAdapter);

        String userId = auth.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.WORKOUT_LOGS_PATH)
                .child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workouts.clear();

                Stack<WorkoutLog> workoutLogStack = new Stack();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    WorkoutLog log = snapshot.getValue(WorkoutLog.class);
                    workoutLogStack.push(log);
                }

                if(workoutLogStack.isEmpty()){
                    mWorkoutHistory.setLayoutManager(null);
                    mWorkoutHistory.setAdapter(null);
                    mWorkoutHistory.setVisibility(View.GONE);
                    mNoWorkoutHistoryLayout.setVisibility(View.VISIBLE);

                } else {
                    while(!workoutLogStack.isEmpty())
                        workouts.add(workoutLogStack.pop());

                    workoutAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPlanHistory(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                RecyclerView.HORIZONTAL,
                false);

        mPlanHistory.setLayoutManager(layoutManager);
        List<WorkoutPlanLog> workoutPlans = new ArrayList();

        final PlanHistoryAdapter planHistoryAdapter = new PlanHistoryAdapter(getContext(), workoutPlans);
        mPlanHistory.setAdapter(planHistoryAdapter);

        String userId = auth.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.PLAN_LOGS_PATH)
                .child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workoutPlans.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    WorkoutPlanLog log = snapshot.getValue(WorkoutPlanLog.class);

                    if(!log.getCompletionDateCode().isEmpty())
                        workoutPlans.add(log);
                }

                if(workoutPlans.isEmpty()){
                    mPlanHistory.setLayoutManager(null);
                    mPlanHistory.setAdapter(null);
                    mPlanHistory.setVisibility(View.GONE);
                    mNoPlanHistoryLayout.setVisibility(View.VISIBLE);

                } else {
                    Collections.sort(workoutPlans, new PlanLogComparator());
                    planHistoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void goToCreateWorkoutPage(){
        Intent intent = new Intent(getActivity(), CreateWorkoutActivity.class);
        startActivity(intent);
    }

    private void goToCreatePlanPage(){
        Intent intent = new Intent(getActivity(), CreatePlanActivity.class);
        startActivity(intent);
    }

    private void goToSelectPlanPage(){
        listener.onButtonClicked();
    }

    private void goToCurrentPlanPage(){
        Intent intent = new Intent(getActivity(), CurrentPlanActivity.class);
        startActivity(intent);
    }
}