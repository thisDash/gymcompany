package com.example.gymcompanion;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gymcompanion.plan.PlansFragment;
import com.example.gymcompanion.workout.WorkoutsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements HomeFragment.NoPlanListener {

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavBar();
    }

    @Override
    public void onBackPressed() {
        if(bottomNavigationBar.getSelectedItemId() != R.id.nav_home){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_content, new HomeFragment())
                    .commit();
            bottomNavigationBar.setSelectedItemId(R.id.nav_home);
        }

        else
            super.onBackPressed();
    }

    private void setupNavBar(){
        bottomNavigationBar = findViewById(R.id.bottom_nav_bar);
        bottomNavigationBar.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            menuItem -> {
                Fragment selectedFragment = null;

                switch(menuItem.getItemId()){
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;

                    case R.id.nav_workouts:
                        selectedFragment = new WorkoutsFragment();
                        break;

                    case R.id.nav_plans:
                        selectedFragment = new PlansFragment();
                        break;

                    case R.id.nav_max_lifts:
                        selectedFragment = new MaxLiftsFragment();
                        break;
                }

                if(selectedFragment != null)
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, selectedFragment).commit();

                return true;
            };

    @Override
    public void onButtonClicked() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new PlansFragment()).commit();
        bottomNavigationBar.setSelectedItemId(R.id.nav_plans);
    }
}