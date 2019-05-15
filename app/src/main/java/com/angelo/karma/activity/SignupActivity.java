package com.angelo.karma.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.angelo.karma.LoginActivity;
import com.angelo.karma.R;
import com.angelo.karma.SignupDetailsActivity;
import com.angelo.karma.interfaces.OnEmailCheckListener;
import com.angelo.karma.interfaces.OnUsernameCheckListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private TextView karmaLogo;
    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button signupButton;
    private TextView signinTextView;
    private LottieAnimationView emailCheck;
    private LottieAnimationView usernameCheck;
    private LottieAnimationView passwordCheck;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SignupActivity);
        setContentView(R.layout.activity_signup);
        Window w = getWindow();
        w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //initializing views
        karmaLogo = findViewById(R.id.karma_logo);
        emailEditText = findViewById(R.id.signup_email_editText);
        usernameEditText = findViewById(R.id.signup_username_editText);
        passwordEditText = findViewById(R.id.signup_password_editText);
        signupButton = findViewById(R.id.signup_button);
        signinTextView = findViewById(R.id.signin_textview);
        emailCheck = findViewById(R.id.signup_email_check);
        usernameCheck = findViewById(R.id.signup_username_check);
        passwordCheck = findViewById(R.id.signup_password_check);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    emailCheck.setAnimation(R.raw.loading);
                    emailCheck.playAnimation();
                    if (isEmailValid(emailEditText.getText().toString())){
                        isCheckEmail(emailEditText.getText().toString(), new OnEmailCheckListener() {
                            @Override
                            public void onSuccess(boolean isRegistered) {
                                if (isRegistered){
                                    emailCheck.setAnimation(R.raw.error);
                                    emailCheck.playAnimation();
                                }else {
                                    emailCheck.setAnimation(R.raw.success_checkmark);
                                    emailCheck.playAnimation();
                                }
                            }
                        });
                    } else {
                        emailCheck.setAnimation(R.raw.error);
                        emailCheck.playAnimation();
                    }
                }
                return false;
            }
        });

        usernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    usernameCheck.setAnimation(R.raw.loading);
                    usernameCheck.playAnimation();
                    if (usernameEditText.getText().length() > 0){
                        isCheckUsername(usernameEditText.getText().toString(), new OnUsernameCheckListener() {
                            @Override
                            public void onSuccess(boolean isRegistered) {
                                if (isRegistered){
                                    usernameCheck.setAnimation(R.raw.error);
                                    usernameCheck.playAnimation();
                                }else {
                                    usernameCheck.setAnimation(R.raw.success_checkmark);
                                    usernameCheck.playAnimation();
                                }
                            }
                        });
                    }

                }
                return false;
            }
        });

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    if (passwordEditText.getText().length() < 4){
                        passwordCheck.setAnimation(R.raw.error);
                        passwordCheck.playAnimation();
                    }else {
                        passwordCheck.setAnimation(R.raw.success_checkmark);
                        passwordCheck.playAnimation();
                    }
                }
                return false;
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (emailEditText.getText().length() != 0
                && usernameEditText.getText().length() != 0
                && passwordEditText.getText().length() != 0){
                    attemptSignup(emailEditText.getText().toString(), usernameEditText.getText().toString(), passwordEditText.getText().toString());
                }
            }
        });

        signinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                View sharedView = findViewById(R.id.karma_logo);
                String transName = "splash_anim";
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(SignupActivity.this, sharedView, transName);
                startActivity(intent, transitionActivityOptions.toBundle());
            }
        });




    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void isCheckEmail(final String email,final OnEmailCheckListener listener){
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task)
            {
                boolean check = !task.getResult().getSignInMethods().isEmpty();

                listener.onSuccess(check);

            }
        });

    }


    private void isCheckUsername(final String username, final OnUsernameCheckListener listener){
        if (!username.contains(" ")){
            db.collection("users").document(username)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                listener.onSuccess(task.getResult().exists());
                            }
                        }
                    });
        }else {
            listener.onSuccess(true);
        }

    }

    private void attemptSignup(String email, String username, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            addUserNameToUser(task.getResult().getUser(), username);


                        } else {
                            emailEditText.setError(getString(R.string.exist_email_error));
                        }
                    }
                });
    }



    private void addUserNameToUser(final FirebaseUser user, String username) {
        Map<String, Object> userDoc = new HashMap<>();
        userDoc.put("username", username);
        userDoc.put("uid", user.getUid());
        userDoc.put("email", user.getEmail());
        userDoc.put("following", 0);
        userDoc.put("followers", 0);
        userDoc.put("following_array", Arrays.asList(username));
        userDoc.put("followers_array", Arrays.asList());


        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        db.collection("users").document(username)
                .set(userDoc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mAuth.getCurrentUser().sendEmailVerification();
                                            startActivity(new Intent(SignupActivity.this, SignupDetailsActivity.class));

                                        } else {

                                        }
                                    }
                                });
                    }
                });


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        View sharedView = findViewById(R.id.karma_logo);
        String transName = "splash_anim";
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(SignupActivity.this, sharedView, transName);
        startActivity(intent, transitionActivityOptions.toBundle());
        super.onBackPressed();
    }


}
