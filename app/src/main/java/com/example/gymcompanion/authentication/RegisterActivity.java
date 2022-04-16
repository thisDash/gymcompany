package com.example.gymcompanion.authentication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymcompanion.MainActivity;
import com.example.gymcompanion.R;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG                     = "RegisterActivity";
    private static final String INSERT_USERNAME         = "Please insert Username.";
    private static final String INSERT_EMAIL            = "Please insert Email.";
    private static final String INSERT_FIRST_PASSWORD   = "Please insert the first Password.";
    private static final String INSERT_SECOND_PASSWORD  = "Please insert the second Password.";
    private static final String PASSWORDS_DONT_MATCH    = "Passwords don't match.";
    private static final String PASSWORD_SHORT          = "Password must have at least 6 characters.";
    private static final String REGISTERING             = "Registering";
    private static final String EMAIL_HINT              = "Email";
    private static final String USERNAME_HINT           = "Username";
    private static final String PASSWORD_1_HINT         = "Password";
    private static final String PASSWORD_2_HINT         = "Repeat Password";

    private EditText mEmail;
    private EditText mUserName;
    private EditText mPassword1;
    private EditText mPassword2;
    private Button mRegisterButton;
    private Button mAlreadyHaveAccountButton;

    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getComponents();
        setButtonListeners();
        setEditTextsListeners();
    }

    private void getComponents(){
        mEmail = findViewById(R.id.register_page_email);
        mUserName = findViewById(R.id.register_page_username);
        mPassword1 = findViewById(R.id.register_page_password_1);
        mPassword2 = findViewById(R.id.register_page_password_2);
        mRegisterButton = findViewById(R.id.register_page_register_button);
        mAlreadyHaveAccountButton = findViewById(R.id.already_have_account_button);
        auth = FirebaseAuth.getInstance();
    }

    private void setButtonListeners(){
        mRegisterButton.setOnClickListener(v -> register());
        mAlreadyHaveAccountButton.setOnClickListener(v -> goToLoginPage());
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

        mUserName.setOnTouchListener((v, event) -> {
            mUserName.setHint("");
            return false;
        });

        mUserName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                mUserName.setHint(USERNAME_HINT);
                hideKeyboard(v);
            }
        });

        mPassword1.setOnTouchListener((v, event) -> {
            mPassword1.setHint("");
            return false;
        });

        mPassword1.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                mPassword1.setHint(PASSWORD_1_HINT);
                hideKeyboard(v);
            }
        });

        mPassword2.setOnTouchListener((v, event) -> {
            mPassword2.setHint("");
            return false;
        });

        mPassword2.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                mPassword2.setHint(PASSWORD_2_HINT);
                hideKeyboard(v);
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void register(){
        final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
        Log.d(TAG, "register: start register");

        if(verifyParameters()){

            pd.setMessage(REGISTERING);
            pd.show();

            String email = mEmail.getText().toString();
            String password = mPassword1.getText().toString();
            String userName = mUserName.getText().toString();

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {

                    if (task.isSuccessful()){
                        Log.d(TAG,"register: register was successful");
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userID = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_PATH).child(userID);
                        User user = new User(userName);
                        reference.setValue(user);

                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, task1 -> {
                                pd.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.putExtra(Constants.USERNAME_FIELD, userName);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            });

                    } else {
                        FirebaseAuthException e = (FirebaseAuthException )task.getException();
                        Log.d(TAG,"Task was not successfull: " + e);
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, "Register account failed. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
        }
    }

    private boolean verifyParameters(){
        String userName = mUserName.getText().toString();
        String email = mEmail.getText().toString();
        String password1 = mPassword1.getText().toString();
        String password2 = mPassword2.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(RegisterActivity.this, INSERT_EMAIL, Toast.LENGTH_LONG).show();
            return false;
        }

        if(userName.isEmpty()){
            Toast.makeText(RegisterActivity.this, INSERT_USERNAME, Toast.LENGTH_LONG).show();
            return false;
        }

        if(password1.isEmpty()){
            Toast.makeText(RegisterActivity.this, INSERT_FIRST_PASSWORD, Toast.LENGTH_LONG).show();
            return false;
        }

        if(password2.isEmpty()){
            Toast.makeText(RegisterActivity.this, INSERT_SECOND_PASSWORD, Toast.LENGTH_LONG).show();
            return false;
        }

        if(password1.compareTo(password2) != 0){
            Toast.makeText(RegisterActivity.this, PASSWORDS_DONT_MATCH, Toast.LENGTH_LONG).show();
            return false;
        }

        if(password1.length() < 6 || password2.length() < 6){
            Toast.makeText(RegisterActivity.this, PASSWORD_SHORT, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void goToLoginPage(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}