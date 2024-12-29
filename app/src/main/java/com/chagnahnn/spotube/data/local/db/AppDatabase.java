package com.chagnahnn.spotube.data.local.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.chagnahnn.spotube.data.local.dao.UserDao;
import com.chagnahnn.spotube.data.local.entities.User;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}