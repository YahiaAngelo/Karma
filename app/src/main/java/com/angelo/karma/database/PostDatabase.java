package com.angelo.karma.database;


import android.content.Context;

import com.angelo.karma.classes.Converters;
import com.angelo.karma.classes.Post;
import com.angelo.karma.dao.PostDao;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Post.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class PostDatabase extends RoomDatabase {

    private static PostDatabase INSTANCE;

    public abstract PostDao postDao();

    private static final Object sLock = new Object();

    public static PostDatabase getInstance(Context context){

        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        PostDatabase.class, "posts.db")
                        .allowMainThreadQueries()
                        .build();
            }
            return INSTANCE;
        }
    }

}
