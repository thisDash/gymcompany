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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.gymcompanion.MainActivity;
import com.example.gymcompanion.R;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

    private static final int GOOGLE_LOG_IN_CODE     = 100;

    private EditText mEmail;
    private EditText mPassword;
    private Button mLogin;
    private Button mRegister;
    private ImageView mGoogleLogin;

    private FirebaseAuth auth;
    private DatabaseReference reference;

    private GoogleSignInOptions googleOptions;
    private GoogleSignInClient googleClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        setGoogleLogIn();

        Log.d("LoginActivity", "user:" + auth.getCurrentUser());
        Log.d("LoginActivity", "google user:" + GoogleSignIn.getLastSignedInAccount(this));

        getComponents();
        setButtonListeners();
        setEditTextsListeners();
    }

    private void getComponents(){
        mEmail = findViewById(R.id.login_page_email);
        mPassword = findViewById(R.id.login_page_password);
        mLogin = findViewById(R.id.initial_page_login_button);
        mRegister = findViewById(R.id.sign_up_button);
        mGoogleLogin = findViewById(R.id.sign_in_google);
    }

    private void setButtonListeners(){
        mLogin.setOnClickListener(v -> tryLogin());
        mRegister.setOnClickListener(v -> goToRegisterPage());
        mGoogleLogin.setOnClickListener(v -> tryGoogleLogin());
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
            emailPasslogin(email, password);
    }

    private void setGoogleLogIn(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleClient = GoogleSignIn.getClient(this, googleOptions);

        if(account != null) {
            if (auth.getCurrentUser() != null)
                firebaseAuthWithGoogle(account);

            else {
                googleClient.signOut().addOnCompleteListener(task -> {
                    Toast.makeText(LoginActivity.this, "Logged out from previous user, please log in again", Toast.LENGTH_LONG).show();
                });
            }
        }
    }

    private void emailPasslogin(String email, String password){
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage(LOGGING_IN);
        pd.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "login: login was successful");
                        pd.dismiss();
                        goToHomePage();

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

    private void tryGoogleLogin(){
        Intent intent = googleClient.getSignInIntent();
        startActivityForResult(intent, GOOGLE_LOG_IN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("LoginActivity", String.valueOf(requestCode));
        if (requestCode == GOOGLE_LOG_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if (task.isSuccessful()){
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                    Log.d("LoginActivity", "Success with google:" + account.getId());
                } catch (ApiException e) {
                    Log.d("LoginActivity", "errorCode: " + e.getMessage());
                }
            } else {
                Log.d("LoginActivity", "errorCode: " + task.getException().toString());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(this, authResult -> {
                    Toast.makeText(LoginActivity.this, "Authentication success.", Toast.LENGTH_LONG).show();

                    tryCreateNewGoogleUser();
                })
                .addOnFailureListener(this, e -> Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show());
    }

    private void goToHomePage() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void tryCreateNewGoogleUser() {
        FirebaseUser currUser = auth.getCurrentUser();
        String currentUserUID = currUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS_PATH)
                .child(currentUserUID);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    reference = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_PATH).child(currentUserUID);
                    User user = new User(currUser.getDisplayName());
                    reference.setValue(user);
                }

                goToHomePage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}