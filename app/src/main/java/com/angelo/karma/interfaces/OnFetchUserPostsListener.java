package com.angelo.karma.interfaces;

import com.angelo.karma.classes.Post;

import java.util.List;

public interface OnFetchUserPostsListener {
    public void onSuccess(List<Post> post);
}
