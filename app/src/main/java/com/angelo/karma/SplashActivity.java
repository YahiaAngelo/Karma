package com.angelo.karma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    private LottieAnimationView animationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mAuth = FirebaseAuth.getInstance();
        animationView = findViewById(R.id.splash_anim);
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mAuth.getCurrentUser() != null){
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    View sharedView = animationView;
                    String transName = "splash_anim";
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, sharedView, transName);
                    startActivity(intent, transitionActivityOptions.toBundle());
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });







    }
}
