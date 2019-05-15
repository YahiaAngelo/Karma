package com.angelo.karma.viewmodel;

import android.app.Application;

import com.angelo.karma.classes.User;

import com.angelo.karma.repo.UserRepository;

import java.util.List;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;

    private LiveData<List<User>> allUsers;

    private User byUser;

    public UserViewModel(@NonNull Application application, String username){
        super(application);


        repository = new UserRepository(application, username);

        allUsers = repository.getAllUsers();

        byUser = repository.getByUserName();

    }


   public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public User getByUser(){return byUser;}

   public void saveUser(final User user) {
        repository.insert(user);
    }


}
