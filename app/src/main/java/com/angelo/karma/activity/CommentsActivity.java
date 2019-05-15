package com.angelo.karma.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.emoji.widget.EmojiEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.angelo.karma.R;
import com.angelo.karma.adapter.CommentAdapter;
import com.angelo.karma.databinding.ActivityCommentsBinding;
import com.angelo.karma.query.QueryUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class CommentsActivity extends AppCompatActivity {

    private CommentAdapter commentAdapter;
    private RecyclerView recyclerView;
    private ActivityCommentsBinding binding;
    private SpinKitView spinKitView;
    private EmojiEditText emojiEditText;
    private MaterialButton sendButton;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
       binding = DataBindingUtil.setContentView(this, R.layout.activity_comments);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
       spinKitView = findViewById(R.id.comment_progress);
        spinKitView.setColor(getColor(R.color.colorAccentDark));
        spinKitView.setVisibility(View.VISIBLE);
        commentAdapter = new CommentAdapter(new ArrayList<>());
        initRecyclerView();
        mAuth = FirebaseAuth.getInstance();

        emojiEditText = binding.commentEditText;
        sendButton = binding.commentSendButton;


        Intent intent = getIntent();
        String postAuthor = intent.getStringExtra("postAuthor");
        int postNum = intent.getIntExtra("postNum", 0);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojiEditText.getText().length() == 0){
                    emojiEditText.setError("Write a comment");
                } else {
                    QueryUtils.addComment(emojiEditText.getText().toString(), "", System.currentTimeMillis(),false, mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getPhotoUrl().toString(), postAuthor, postNum, commentAdapter);
                    emojiEditText.setText("");
                }
            }
        });






    }


    private void initRecyclerView(){
        recyclerView = binding.commentListRecycler;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //Reverse the layout to get latest post first
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        getComments();
        recyclerView.setAdapter(commentAdapter);



    }


    private void getComments(){
        Log.v("karma", "Getting comments");
        Intent intent = getIntent();
        String postAuthor = intent.getStringExtra("postAuthor");
        int postNum = intent.getIntExtra("postNum", 0);
        QueryUtils.fetchComments(postAuthor, postNum, commentAdapter, this);
    }
}
