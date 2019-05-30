package com.angelo.karma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.angelo.karma.activity.AboutActivity;
import com.angelo.karma.activity.ProfileActivity;
import com.angelo.karma.activity.SettingsActivity;
import com.angelo.karma.classes.User;
import com.angelo.karma.fragment.MainFragment;
import com.angelo.karma.interfaces.OnFetchUserListener;
import com.angelo.karma.query.QueryUtils;


import com.bumptech.glide.Glide;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;


import java.util.List;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String username;
    private String profilePic;
    private int following;
    private int followers;
    private List<String> followingList;
    private AccountHeader headerResult;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkTheme);
            getWindow().setNavigationBarColor(getColor(R.color.darkColorPrimaryDark));
            toolbar.setBackgroundColor(getColor(R.color.darkColorPrimary));
        }else {
            setTheme(R.style.AppTheme);
        }



        mAuth = FirebaseAuth.getInstance();

        checkStoragePermission();

        BubbleNavigationLinearView bubbleNavigationLinearView = findViewById(R.id.bottom_navigation_view_linear);
        bubbleNavigationLinearView.setCurrentActiveItem(0);

        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("profilePic", profilePic);
                        startActivity(intent);
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, "Coming soon!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.VISIBLE);






        QueryUtils.fetchUserInfo(mAuth.getCurrentUser().getDisplayName(), new OnFetchUserListener() {
            @Override
            public void onSuccess(User user) {
                username = user.username;
                profilePic = user.profilePic;
                followingList = user.following_list;
                following = user.getFollowing();
                followers = user.getFollowers();
                showProfile();
                for (String following: followingList){
                    QueryUtils.fetchPosts(MainActivity.this, following);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure() {

            }
        });








        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MainFragment())
                .commit();





    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) mSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("username", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        searchView.setQueryHint("Search");
        return true;
    }



    private void showProfile(){


        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {

                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.with(imageView.getContext()).clear(imageView);            }
        });

        String userInfoText = following + " Following    " + followers + " Followers";

       headerResult = new AccountHeaderBuilder()
                .withActivity(this)
               .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                   @Override
                   public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                       Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                       intent.putExtra("username", username);
                       startActivity(intent);
                       return false;
                   }

                   @Override
                   public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                       return false;
                   }
               })
               .addProfiles(new ProfileDrawerItem().withName(username).withEmail(userInfoText).withIcon(profilePic))
                .build();



        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Main").withIcon(R.drawable.md_paper);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Settings").withIcon(R.drawable.md_settings).withSelectable(false);

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("About").withSelectable(false).withIcon(R.drawable.md_information_circle_outline),
                        new SecondaryDrawerItem().withName("Sign Out").withSelectable(false).withIcon(R.drawable.md_log_out)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 3:
                               startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            break;
                            case 4:
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                                break;
                            case 5:
                                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                                break;
                            case 6:
                                signOut();
                                break;
                        }
                        return true;
                    }
                })
                .build();


    }

    private void checkStoragePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

        } else {
            // Permission has already been granted
        }
    }

    private void signOut(){
         mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
             @Override
             public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 FirebaseUser user = firebaseAuth.getCurrentUser();
                 if (user == null){
                     Intent mStartActivity = new Intent(MainActivity.this, LoginActivity.class);
                     int mPendingIntentId = 696969;
                     PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId, mStartActivity,
                             PendingIntent.FLAG_CANCEL_CURRENT);
                     AlarmManager mgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                     mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                     System.exit(0);
                 }
             }
         });

        mAuth.signOut();
    }


}
