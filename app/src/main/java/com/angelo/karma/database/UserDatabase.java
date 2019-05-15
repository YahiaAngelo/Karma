package com.angelo.karma.database;

import android.content.Context;

import com.angelo.karma.classes.Converters;
import com.angelo.karma.classes.User;
import com.angelo.karma.dao.UserDao;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(entities = {User.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class UserDatabase extends RoomDatabase {
    private static UserDatabase INSTANCE;

    public abstract UserDao userDao();

    private static final Object sLock = new Object();

    public static UserDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        UserDatabase.class, "users.db")
                        .allowMainThreadQueries()
                        .build();
            }
            return INSTANCE;
        }
    }
}
