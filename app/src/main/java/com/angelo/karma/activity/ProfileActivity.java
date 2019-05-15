package com.angelo.karma.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.angelo.karma.R;
import com.angelo.karma.adapter.PostAdapter;
import com.angelo.karma.classes.Post;
import com.angelo.karma.classes.User;
import com.angelo.karma.databinding.ActivityProfileBinding;
import com.angelo.karma.interfaces.OnFetchUserListener;
import com.angelo.karma.interfaces.OnFetchUserPostsListener;
import com.angelo.karma.query.QueryUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;


public class ProfileActivity extends AppCompatActivity {
    private  ActivityProfileBinding binding;
    private PostAdapter postAdapter;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();


        QueryUtils.fetchUserInfo(getIntent().getStringExtra("username"), new OnFetchUserListener() {
            @Override
            public void onSuccess(User user) {
                if (user.getUsername().equals(mAuth.getCurrentUser().getDisplayName())){
                    binding.followButton.setText(getString(R.string.edit_profile));
                    binding.followButton.setBackgroundColor(getColor(R.color.colorAccentBlueDark_50));
                    binding.followButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                            intent.putExtra("username", user.getUsername());
                            intent.putExtra("profilePic", user.getProfilePic());
                            intent.putExtra("firstName", user.getFirstName());
                            intent.putExtra("lastName", user.getLastName());
                            intent.putExtra("bio", user.getBio());
                            startActivity(intent);
                        }
                    });
                }else {
                    if (user.followers_list.contains(mAuth.getCurrentUser().getDisplayName())){
                        binding.followButton.setText(getString(R.string.unfollow));
                        binding.followButton.setBackgroundColor(getColor(R.color.colorAccentBlueDark_50));
                        binding.followButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                binding.followButton.setText(getString(R.string.follow));
                                binding.followButton.setBackgroundColor(getColor(R.color.colorAccentBlueDark));
                                QueryUtils.submitFollow(false, user.getUsername(), user.getFollowers_list(), mAuth.getCurrentUser().getDisplayName());
                                ProfileActivity.this.recreate();
                            }
                        });

                    }else {
                        binding.followButton.setText(getString(R.string.follow));
                        binding.followButton.setBackgroundColor(getColor(R.color.colorAccentBlueDark));
                        binding.followButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                binding.followButton.setText(getString(R.string.unfollow));
                                binding.followButton.setBackgroundColor(getColor(R.color.colorAccentBlueDark_50));
                                QueryUtils.submitFollow(true, user.getUsername(), user.getFollowers_list(), mAuth.getCurrentUser().getDisplayName());
                                ProfileActivity.this.recreate();
                            }
                        });
                    }

                }
                String name = user.getFirstName() + " " + user.getLastName();
                String username = "@" + user.getUsername();
                Glide.with(ProfileActivity.this)
                        .load(user.getProfilePic())
                        .circleCrop()
                        .fitCenter()
                        .override(Target.SIZE_ORIGINAL)
                        .into(binding.profileAuthorPic);

                binding.profileAuthorPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] resources = new String[]{user.getProfilePic()};

                        new StfalconImageViewer.Builder<>(ProfileActivity.this, resources, new ImageLoader<String>() {
                            @Override
                            public void loadImage(ImageView imageView, String image) {
                                Glide
                                        .with(ProfileActivity.this)
                                        .load(user.getProfilePic())
                                        .into(imageView);
                            }
                        }).withTransitionFrom(binding.profileAuthorPic).show();
                    }
                });
                binding.profileAuthorName.setText(name);

                binding.profileAuthorUsername.setText(username);
                binding.profileAuthorBio.setText(user.getBio());
                binding.profileFollowersNum.setText(String.valueOf(user.getFollowers()));
                binding.profileFollowingNum.setText(String.valueOf(user.getFollowing()));


                postAdapter = new PostAdapter(new ArrayList<>());

                recyclerView = binding.profilePostsRecycler;
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileActivity.this);
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                //Reverse the layout to get latest post first
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);

                QueryUtils.fetchUserPosts(getIntent().getStringExtra("username"), new OnFetchUserPostsListener() {
                    @Override
                    public void onSuccess(List<Post> post) {
                        binding.profilePostsNum.setText(String.valueOf(post.size()));
                        postAdapter.setData(post);
                        recyclerView.setAdapter(postAdapter);

                    }
                });

            }

            @Override
            public void onFailure() {

                binding.profileAuthorName.setText(getString(R.string.user_not_found));
            }
        });



    }
}
