package com.xeon.baseDemo.db

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.xeon.baseDemo.data.LocationStorage
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class Storage constructor(context: Context) {
    private val appDataBase = Room.databaseBuilder(
        context, AppDataBase::class.java,
        "location"
    ).build()


    private val sqlScheduler =
        Schedulers.from(Executors.newSingleThreadExecutor { Thread(it, "SqlThread") })

    fun savePendingLocations(locations: Collection<LocationStorage>): Completable {
        Log.i("savePendingLocations", locations.toString())
        return appDataBase.locationDao().insert(locations.toList()).observeOn(sqlScheduler)
    }

    fun getPendingLocations(count: Int): List<LocationStorage> {
        return appDataBase.locationDao().getTagStorage(count)
    }

    fun removeAllPendingLocations(): Completable {
        return appDataBase.locationDao().deleteTable().observeOn(sqlScheduler)
    }

    fun removePendingLocations(locations: List<Int>): Completable {
        Log.i("removePendingLocations", locations.toString())
        return appDataBase.locationDao().deleteData(locations).observeOn(sqlScheduler)
    }

}