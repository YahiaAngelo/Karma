package com.angelo.karma.repo;

import android.app.Application;
import android.os.AsyncTask;

import com.angelo.karma.classes.Post;
import com.angelo.karma.dao.PostDao;
import com.angelo.karma.database.PostDatabase;

import java.util.List;

import androidx.lifecycle.LiveData;

public class PostRepository {

    private PostDao postDao;
    private LiveData<List<Post>> allPosts;


    public PostRepository(Application application){

        PostDatabase db = PostDatabase.getInstance(application);

        postDao = db.postDao();

        allPosts = postDao.findLiveData();
    }

    public LiveData<List<Post>> getAllPosts() {
        return allPosts;
    }

    public void insert(Post post){
        new insertAsyncTask(postDao).doInBackground(post);
    }


    private static class insertAsyncTask extends AsyncTask<Post, Void, Void> {

        private PostDao mAsyncTaskDao;

        insertAsyncTask(PostDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Post... posts) {
            mAsyncTaskDao.save(posts[0]);
            return null;
        }
    }

}
