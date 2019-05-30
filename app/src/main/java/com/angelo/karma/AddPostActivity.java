package com.angelo.karma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.widget.EmojiEditText;

import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import com.angelo.karma.query.QueryUtils;
import com.bumptech.glide.Glide;
import com.github.florent37.shapeofview.shapes.RoundRectView;
import com.github.ybq.android.spinkit.SpinKitView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;


public class AddPostActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private MaterialButton postButton;
    private ImageView closeImage;
    private ImageView postAuthorPic;
    private EmojiEditText postEditText;
    private RoundRectView postImageLayout;
    private ImageView postImage;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String userName;
    private String profilePic;
    private SpinKitView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_add_post);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        userName = getIntent().getStringExtra("username");
        profilePic = getIntent().getStringExtra("profilePic");

        floatingActionButton = findViewById(R.id.floating_action_button);
        postButton = findViewById(R.id.add_post_button);
        closeImage = findViewById(R.id.close_image);
        postAuthorPic = findViewById(R.id.addpost_author_pic);
        postEditText = findViewById(R.id.post_editText);
        postImageLayout = findViewById(R.id.add_post_imageLayout);
        postImage = findViewById(R.id.add_post_image);
        progressBar = findViewById(R.id.add_post_image_progress);

        progressBar.setColor(getColor(R.color.colorAccentDark));



        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        postButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                QueryUtils.addNewPost(postEditText.getText().toString(), "", profilePic,
                        System.currentTimeMillis(), false, userName);

                finish();


            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery(0);
            }
        });

        Glide.with(this)
                .load(profilePic)
                .into(postAuthorPic);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {

                try {
                    progressBar.setVisibility(View.VISIBLE);

                    StorageReference userRef = storageReference.child(userName + "/" + imageFiles.get(0).getName());

                    UploadTask uploadTask;

                    uploadTask = userRef.putFile(Uri.fromFile(new Compressor(AddPostActivity.this).compressToFile(imageFiles.get(0))));
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return userRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();


                                Glide
                                        .with(getApplicationContext())
                                        .load(downloadUri)
                                        .into(postImage);

                                progressBar.setVisibility(View.INVISIBLE);
                                postImageLayout.setVisibility(View.VISIBLE);
                                postButton.setBackgroundColor(getColor(R.color.colorAccentDark));


                                postButton.setOnClickListener(null);
                                postButton.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        QueryUtils.addNewPost(postEditText.getText().toString(), downloadUri.toString(), profilePic,
                                                System.currentTimeMillis(), true, userName);

                                        finish();


                                    }
                                });

                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });


                }catch (Exception e){

                }







            }


        });

    }


    private void openGallery(int type) {

        EasyImage.openGallery(this, type);
    }
}
