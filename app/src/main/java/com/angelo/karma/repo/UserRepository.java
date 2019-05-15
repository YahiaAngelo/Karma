package com.angelo.karma.repo;

import android.app.Application;
import android.os.AsyncTask;

import com.angelo.karma.classes.User;
import com.angelo.karma.dao.UserDao;
import com.angelo.karma.database.UserDatabase;

import java.util.List;

import androidx.lifecycle.LiveData;

public class UserRepository {

    private UserDao userDao;
    private LiveData<List<User>> allUsers;
    private User byUserName;


   public UserRepository(Application application){

        UserDatabase db = UserDatabase.getInstance(application);
        userDao = db.userDao();
        allUsers = userDao.findLiveData();
    }

    public UserRepository(Application application, String username){

        UserDatabase db = UserDatabase.getInstance(application);
        userDao = db.userDao();
        byUserName = userDao.findAllByUsername(username);

    }

   public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public User getByUserName(){
       return byUserName;
    }

    public void insert(User user){
        new insertAsyncTask(userDao).doInBackground(user);
    }


    private static class insertAsyncTask extends AsyncTask<User, Void, Void>{

        private UserDao mAsyncTaskDao;

        insertAsyncTask(UserDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... users) {
            mAsyncTaskDao.save(users[0]);
            return null;
        }
    }

}
