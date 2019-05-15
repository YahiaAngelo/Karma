package com.angelo.karma.viewmodel;

import android.app.Application;

import com.angelo.karma.classes.Post;
import com.angelo.karma.repo.PostRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class PostViewModel extends AndroidViewModel {
    private PostRepository repository;

    private LiveData<List<Post>> allPosts;

    public PostViewModel(@NonNull Application application){
        super(application);

        repository = new PostRepository(application);

        allPosts = repository.getAllPosts();
    }

    public LiveData<List<Post>> getAllPosts(){
        return allPosts;
    }

    public void savePost(final Post post){
        repository.insert(post);
    }
}
