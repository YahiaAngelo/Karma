package com.angelo.karma.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.angelo.karma.MainActivity;
import com.angelo.karma.R;
import com.angelo.karma.SignupDetailsActivity;
import com.angelo.karma.databinding.ActivityProfileEditBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfileEditActivity extends AppCompatActivity {

    private ActivityProfileEditBinding binding;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String username;
    private String profilePic;
    private String firstName;
    private String lastName;
    private String bio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SignupActivity);
        getWindow().setStatusBarColor(getColor(R.color.primary));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        username = getIntent().getStringExtra("username");
        profilePic = getIntent().getStringExtra("profilePic");
        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        bio = getIntent().getStringExtra("bio");

        Glide.with(this)
                .load(profilePic)
                .into(binding.profileEditPic);

        binding.closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.profileEditFirstName.setText(firstName);
        binding.profileEditLastName.setText(lastName);
        binding.profileEditBio.setText(bio);
        binding.profileEditPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(0);
            }
        });

        binding.profileEditFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(binding.profileEditFirstName.getText().toString(), binding.profileEditLastName.getText().toString(), binding.profileEditBio.getText().toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {


                try {

                    StorageReference userRef = storageReference.child(mAuth.getCurrentUser().getDisplayName() + "/" + imageFiles.get(0).getName());

                    UploadTask uploadTask;

                    uploadTask = userRef.putFile(Uri.fromFile(new Compressor(ProfileEditActivity.this).compressToFile(imageFiles.get(0))));

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
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(downloadUri)
                                        .build();

                                mAuth.getCurrentUser().updateProfile(profileUpdates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Glide.with(getApplicationContext())
                                                        .load(downloadUri.toString())
                                                        .into(binding.profileEditPic);
                                                Map<String, Object> userDoc = new HashMap<>();
                                                userDoc.put("profilepic", downloadUri.toString());

                                                db.collection("users").document(mAuth.getCurrentUser().getDisplayName())
                                                        .update(userDoc)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
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

    private void updateProfile(String firstName, String lastName, String bio){
        Map<String, Object> userDoc = new HashMap<>();
        userDoc.put("first_name", firstName);
        userDoc.put("last_name", lastName);
        userDoc.put("bio", bio);
        db.collection("users").document(mAuth.getCurrentUser().getDisplayName())
                .update(userDoc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(ProfileEditActivity.this, ProfileActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                });
    }

    private void openGallery(int type) {

        EasyImage.openGallery(this, type);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
