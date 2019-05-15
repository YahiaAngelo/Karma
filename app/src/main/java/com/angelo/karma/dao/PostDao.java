package com.angelo.karma.dao;

import com.angelo.karma.classes.Post;
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
public interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<Post> posts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Post post);

    @Update
    void update(Post post);

    @Delete
    void delete(Post post);

    @Query("SELECT * FROM Post WHERE postId IN (:postIds)")
    List<Post> findAllByIds(long[] postIds);

    @Query("SELECT * FROM Post WHERE postId IN (:postIds)")
    List<Post> findAllByPostId(int postIds);

    @Query("SELECT * FROM Post")
    List<Post> findAll();
    @Query("SELECT * FROM Post ORDER BY postId ASC")
    LiveData<List<Post>> findLiveData();

    @Query("Delete FROM Post")
    void deleteAll();
}
