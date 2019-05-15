package com.angelo.karma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.angelo.karma.activity.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    LottieAnimationView remembermeAnim;
    private EditText emailEditText;
    private EditText passwordEditText;
    private MaterialButton signinButton;
    private TextView signupText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        //Check for if user exists

        setTheme(R.style.SignupActivity);
        setContentView(R.layout.activity_login);
            Window w = getWindow();
            w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
            //initializing vars
            emailEditText = findViewById(R.id.signin_email_editText);
            passwordEditText = findViewById(R.id.signin_password_editText);
            signinButton = findViewById(R.id.signin_button);
            signupText = findViewById(R.id.signup_textview);
            remembermeAnim = findViewById(R.id.signin_checkbox);


            signinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEmailValid(emailEditText.getText().toString())){
                        if (passwordEditText.getText().length() > 6){
                            attemptLogin(emailEditText.getText().toString(), passwordEditText.getText().toString());
                        }else {
                            emailEditText.setError("Enter valid password");
                        }
                    }else {
                        emailEditText.setError("Enter valid email address");
                    }
                }
            });


            remembermeAnim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        remembermeAnim.playAnimation();
                    remembermeAnim.setRepeatMode(LottieDrawable.REVERSE);


                }
            });


            signupText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    View sharedView = findViewById(R.id.signin_karma_logo);
                    String transName = "splash_anim";
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, sharedView, transName);
                    startActivity(intent, transitionActivityOptions.toBundle());                }
            });


    }


    private void attemptLogin(String email, String password){



        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();
                        }else {
                            emailEditText.setError(getString(R.string.login_error));
                        }

                    }
                });


    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


}
