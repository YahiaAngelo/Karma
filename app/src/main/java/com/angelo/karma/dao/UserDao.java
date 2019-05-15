package com.angelo.karma.dao;

import com.angelo.karma.classes.User;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<User> users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM User WHERE uid IN (:userIds)")
    List<User> findAllByIds(int[] userIds);

    @Query("SELECT * FROM User WHERE username IN (:userNames)")
    User findAllByUsername(String userNames);

    @Query("SELECT * FROM User")
    List<User> findAll();
    @Query("SELECT * FROM User ORDER BY username ASC")
    LiveData<List<User>> findLiveData();

    @Query("Delete FROM User")
    void deleteAll();
}
