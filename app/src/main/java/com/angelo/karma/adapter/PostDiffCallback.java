package com.angelo.karma.adapter;

import com.angelo.karma.classes.Post;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

public class PostDiffCallback extends DiffUtil.Callback {

    private final List<Post> mOldPostList;
    private final List<Post> mNewPostList;

    public PostDiffCallback(List<Post> oldPostList, List<Post> newPostList){
        this.mOldPostList = oldPostList;
        this.mNewPostList = newPostList;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }

    @Override
    public int getOldListSize() {
        return mOldPostList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewPostList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldPostList.get(oldItemPosition).getPostId() == mNewPostList.get(newItemPosition).getPostId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Post oldPost = mOldPostList.get(oldItemPosition);
        final Post newPost = mNewPostList.get(newItemPosition);

        return oldPost.getPostId() == newPost.getPostId();
    }
}
