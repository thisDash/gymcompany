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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.gymcompanion.MainActivity;
import com.example.gymcompanion.R;
import com.example.gymcompanion.components.Constants;
import com.example.gymcompanion.components.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


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
    private ImageView mFacebookLogin;

    private FirebaseAuth auth;
    private DatabaseReference reference;

    private GoogleSignInOptions googleOptions;
    private GoogleSignInClient googleClient;

    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        setupGoogleLogIn();
        setupFacebookLogIn();

        Log.d("LoginActivity", "user:" + auth.getCurrentUser());
        Log.d("LoginActivity", "google user:" + GoogleSignIn.getLastSignedInAccount(this));
        Log.d("LoginActivity", "facebook user:" + AccessToken.getCurrentAccessToken());

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
        mFacebookLogin = findViewById(R.id.sign_in_facebook);
    }

    private void setButtonListeners(){
        mLogin.setOnClickListener(v -> tryLogin());
        mRegister.setOnClickListener(v -> goToRegisterPage());
        mGoogleLogin.setOnClickListener(v -> tryGoogleLogin());
        mFacebookLogin.setOnClickListener(v -> tryFacebookLogin());
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

    //Email and Password Login

    private void tryLogin(){
        Log.d(TAG, "login: Staring login");

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if(email.isEmpty())
            Toast.makeText(LoginActivity.this, EMAIL_MISSING, Toast.LENGTH_SHORT).show();

        else if(password.isEmpty())
            Toast.makeText(LoginActivity.this, PASSWORD_MISSING, Toast.LENGTH_SHORT).show();

        else
            emailPassLogin(email, password);
    }

    private void emailPassLogin(String email, String password){
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

    //Google Log in

    private void setupGoogleLogIn(){
        googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleClient = GoogleSignIn.getClient(this, googleOptions);
    }

    private void tryGoogleLogin(){
        Intent intent = googleClient.getSignInIntent();
        googleLoginResult.launch(intent);
    }

    ActivityResultLauncher<Intent> googleLoginResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
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
            });

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(this, authResult -> {
                    Toast.makeText(LoginActivity.this, "Authentication success.", Toast.LENGTH_LONG).show();
                    tryCreateNewUser();
                })
                .addOnFailureListener(this, e -> Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show());
    }

    //Facebook Login

    private void setupFacebookLogIn(){
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Login Successful");
                        firebaseAuthWithFacebook(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Login Canceled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "Facebook Login Error");
                    }
                });
    }

    private void tryFacebookLogin(){
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithFacebook(AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "Facebook Authentication success.");
                        tryCreateNewUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d(TAG, "Facebook Authentication failed: " + task.getException());
                    }
                });
    }

    //Navigation methods

    private void goToHomePage() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void goToRegisterPage(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    //Auxiliary methods

    private void tryCreateNewUser() {
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