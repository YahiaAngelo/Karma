package com.angelo.karma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.emoji.widget.EmojiEditText;

import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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

public class SignupDetailsActivity extends AppCompatActivity {

    ConstraintLayout container;
    private ImageView profilePic;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EmojiEditText bioEditText;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private MaterialButton finishButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SignupActivity);
        setContentView(R.layout.activity_signup_details);

        container = findViewById(R.id.signup_extra_container);
        profilePic = findViewById(R.id.signup_details_pic);
        firstNameEditText = findViewById(R.id.signup_first_name);
        lastNameEditText = findViewById(R.id.signup_last_name);
        bioEditText = findViewById(R.id.signup_bio);
        finishButton = findViewById(R.id.signup_finish_button);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(0);
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

                    uploadTask = userRef.putFile(Uri.fromFile(new Compressor(SignupDetailsActivity.this).compressToFile(imageFiles.get(0))));

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
                                                        .into(profilePic);
                                                finishButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (firstNameEditText.getText().length() != 0 || lastNameEditText.getText().length() !=0 || bioEditText.getText().length() != 0){
                                                            Map<String, Object> userDoc = new HashMap<>();
                                                            userDoc.put("first_name", firstNameEditText.getText().toString());
                                                            userDoc.put("last_name", lastNameEditText.getText().toString());
                                                            userDoc.put("bio", bioEditText.getText().toString());
                                                            userDoc.put("profilepic", downloadUri.toString());

                                                            db.collection("users").document(mAuth.getCurrentUser().getDisplayName())
                                                                    .update(userDoc)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                                            finish();
                                                                        }
                                                                    });

                                                        }
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

    private void openGallery(int type) {

        EasyImage.openGallery(this, type);
    }
}
