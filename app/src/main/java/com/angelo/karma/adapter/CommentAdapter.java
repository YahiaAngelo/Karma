package com.angelo.karma.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.angelo.karma.R;
import com.angelo.karma.activity.ProfileActivity;
import com.angelo.karma.classes.Comment;
import com.angelo.karma.databinding.CommentListBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private List<Comment> commentList;
    private LayoutInflater layoutInflater;

    public class MyViewHolder extends RecyclerView.ViewHolder {

       private final CommentListBinding binding;


        public MyViewHolder(final CommentListBinding commentListBinding){
            super(commentListBinding.getRoot());

            this.binding = commentListBinding;

        }
    }


    public CommentAdapter(List<Comment> comments){
        this.commentList = comments;
        setHasStableIds(true);
        notifyDataSetChanged();
    }



    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
        notifyDataSetChanged();

    }

    public void addComment(Comment newComment){
        List<Comment> newList = new ArrayList<>();
        newList = this.commentList;
        newList.add(newComment);
        this.commentList = newList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setComment(commentList.get(position));
        setFadeAnimation(holder.itemView);
        final Comment comment = commentList.get(position);

        holder.binding.commentAuthorName.setText(comment.getCommentAuthor());
        holder.binding.commentDescText.setText(comment.getCommentString());
        holder.binding.commentAuthorPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ProfileActivity.class);
                intent.putExtra("username", comment.getCommentAuthor());
                holder.itemView.getContext().startActivity(intent);
            }
        });

       Comment.loadPostAuthorImage(holder.binding.commentAuthorPic, comment.getCommentAuthorPic());


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        CommentListBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.comment_list, parent, false);
        return new CommentAdapter.MyViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public long getItemId(int position) {
        Comment comment = commentList.get(position);
        return comment.getId();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.clearAnimation();
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }
}
