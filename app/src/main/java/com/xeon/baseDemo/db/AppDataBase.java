package com.xeon.baseDemo.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.xeon.baseDemo.dao.LocationDao;
import com.xeon.baseDemo.data.LocationStorage;

@Database(entities = {LocationStorage.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract LocationDao locationDao();
}
