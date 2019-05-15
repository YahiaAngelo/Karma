package com.angelo.karma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelo.karma.BuildConfig;
import com.angelo.karma.R;
import com.angelo.karma.interfaces.OnLatestUpdateCheckListener;
import com.angelo.karma.query.QueryUtils;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        LinearLayout changelogButton = findViewById(R.id.changelogButton);
        LinearLayout sourceCodeButton = findViewById(R.id.source_code_button);
        LinearLayout checkUpdatesButton = findViewById(R.id.updates_check_button);
        LinearLayout aboutUsButton = findViewById(R.id.aboutus_button);
        TextView checkUpdatesText = findViewById(R.id.updates_check_text);


        changelogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new BlurPopupWindow.Builder<>(AboutActivity.this)
                        .setContentView(R.layout.activity_changelog)
                        .setGravity(Gravity.CENTER)
                        .setScaleRatio(0.2f)
                        .setBlurRadius(10)
                        .setTintColor(0x30000000)
                        .build()
                        .show();



            }
        });

        sourceCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AboutActivity.this, AboutLinksActivity.class));

            }
        });

        checkUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdatesText.setText(getString(R.string.updates_check));
                QueryUtils.getLatestUpdate(new OnLatestUpdateCheckListener() {
                    @Override
                    public void onSuccess(String latestUpdate) {
                        Double versionCode = Double.valueOf(BuildConfig.VERSION_NAME);
                        Double latestVersion = Double.valueOf(latestUpdate);
                        if (versionCode.equals(latestVersion)){
                            checkUpdatesText.setText(getString(R.string.uptodate));
                        }else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
                            builder.setMessage("Karma v" + latestUpdate + " is available!" );
                            builder.setTitle("New Update!");
                            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    openLink("https://github.com/YahiaAngelo/Karma/releases");
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }

                    }

                    @Override
                    public void onFailure() {
                        checkUpdatesText.setText(getString(R.string.updates_check_failed));
                    }
                });
            }
        });
        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BlurPopupWindow.Builder<>(AboutActivity.this)
                        .setContentView(R.layout.activity_about_us)
                        .setGravity(Gravity.CENTER)
                        .setScaleRatio(0.2f)
                        .setBlurRadius(10)
                        .setTintColor(0x30000000)
                        .build()
                        .show();


            }
        });
    }
    private void openLink(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
