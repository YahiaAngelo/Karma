package com.angelo.karma.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.angelo.karma.R;

public class AboutLinksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_links);
        // Layouts
        LinearLayout karmaTgChannel = findViewById(R.id.karma_channel_tg_layout);
        LinearLayout karmaGithub = findViewById(R.id.karma_github);

        LinearLayout angeloTg = findViewById(R.id.angelo_tg_layout);
        LinearLayout angeloGithub = findViewById(R.id.angelo_github_layout);
        LinearLayout angeloPaypal = findViewById(R.id.angelo_pp_layout);



        karmaTgChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(getString(R.string.karmaTgChannel));
            }
        });

        karmaGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(getString(R.string.karma_github_repo));
            }
        });


        angeloTg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(getString(R.string.angeloTg));
            }
        });

        angeloGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(getString(R.string.angeloGithub));
            }
        });

        angeloPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(getString(R.string.angeloPaypal));
            }
        });


    }

    private void openLink(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}

