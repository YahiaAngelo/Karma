package com.angelo.karma.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;


import com.angelo.karma.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SwitchPreferenceCompat updatesPref = findPreference("updates_notification");

            updatesPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isUpdatesNotifications = (Boolean) newValue;
                    if (isUpdatesNotifications){
                        FirebaseMessaging.getInstance().subscribeToTopic("updates")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if (!task.isSuccessful()){
                                            Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                                        }
                                        Toast.makeText(getContext(), "You Can Get New Updates Notification!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("updates");

                    }
                    return true;
                }
            });
        }


    }
}