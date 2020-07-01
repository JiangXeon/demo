package com.xeon.baseDemo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xeon.baseDemo.data.LocationStorage
import io.reactivex.Completable

@Dao
interface LocationDao {

    @Insert
    fun insert(stories: List<LocationStorage>): Completable

    @Query("SELECT * FROM location Limit :count")
    fun getTagStorage(count: Int): List<LocationStorage>

    @Query("DELETE  FROM location")
    fun deleteTable():Completable

    @Query("DELETE  FROM  location WHERE id in (:ids)")
    fun deleteData(ids: List<Int>):Completable

}