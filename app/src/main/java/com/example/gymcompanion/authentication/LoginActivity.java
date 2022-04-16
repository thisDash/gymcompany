package com.example.gymcompanion.authentication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.gymcompanion.MainActivity;
import com.example.gymcompanion.R;
import com.example.gymcompanion.components.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG                 = "LoginActivity";
    private static final String EMAIL_MISSING       = "Email is missing...";
    private static final String PASSWORD_MISSING    = "Password is missing...";
    private static final String LOGGING_IN          = "Logging in";
    private static final String LOGIN_FAIL          = "One of the parameters Email/Password is wrong. Please try again.";
    private static final String EMAIL_HINT          = "Email";
    private static final String PASSWORD_HINT       = "Password";

    private EditText mEmail;
    private EditText mPassword;
    private Button mLogin;
    private Button mRegister;

    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getComponents();
        setButtonListeners();
        setEditTextsListeners();
    }

    private void getComponents(){
        mEmail = findViewById(R.id.initial_page_email);
        mPassword = findViewById(R.id.initial_page_password);
        mLogin = findViewById(R.id.initial_page_login_button);
        mRegister = findViewById(R.id.initial_page_no_account_button);
        auth = FirebaseAuth.getInstance();
    }

    private void setButtonListeners(){
        mLogin.setOnClickListener(v -> tryLogin());
        mRegister.setOnClickListener(v -> goToRegisterPage());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEditTextsListeners(){
        mEmail.setOnTouchListener((v, event) -> {
            mEmail.setHint("");
            return false;
        });

        mEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                mEmail.setHint(EMAIL_HINT);
                hideKeyboard(v);
            }
        });

        mPassword.setOnTouchListener((v, event) -> {
            mPassword.setHint("");
            return false;
        });

        mPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                mPassword.setHint(PASSWORD_HINT);
                hideKeyboard(v);
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void tryLogin(){
        Log.d(TAG, "login: Staring login");

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if(email.isEmpty())
            Toast.makeText(LoginActivity.this, EMAIL_MISSING, Toast.LENGTH_SHORT).show();

        else if(password.isEmpty())
            Toast.makeText(LoginActivity.this, PASSWORD_MISSING, Toast.LENGTH_SHORT).show();

        else
            login(email, password);
    }

    private void login(String email, String password){
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage(LOGGING_IN);
        pd.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "login: login was successful");
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userId = firebaseUser.getUid();
                        pd.dismiss();

                        getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE)
                                .edit()
                                .putString(Constants.PREF_EMAIL, email)
                                .putString(Constants.PREF_PASSWORD, password)
                                .apply();

                        reference = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_PATH).child(userId);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String userName = dataSnapshot.child(Constants.USERNAME_FIELD).getValue().toString();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Constants.USERNAME_FIELD, userName);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    } else {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, LOGIN_FAIL, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void goToRegisterPage(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}