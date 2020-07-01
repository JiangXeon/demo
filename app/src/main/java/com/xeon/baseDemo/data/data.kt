package com.xeon.baseDemo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Optional
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Location @JvmOverloads constructor(
    @get:JsonProperty("latitude") val latitude: Double = 0.0,
    @get:JsonProperty("longitude") val longitude: Double = 0.0,
    @get:JsonProperty("radius") val radius: Int? = null,
    @get:JsonProperty("alt") val altitude: Int? = null,
    @get:JsonProperty("speed") val speed: Int? = null,
    @get:JsonProperty("createTime") val createTime: Long = System.currentTimeMillis(),
    @get:JsonProperty("direction") val direction: Float? = null
) : Serializable

@Entity(tableName = "location")
data class LocationStorage(
    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0,

    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0
) {

    constructor() : this(0.0, 0.0)

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Int = 0

    @ColumnInfo(name = "radius")
    var radius: Int? = 0

    @ColumnInfo(name = "alt")
    var altitude: Int? = 0

    @ColumnInfo(name = "speed")
    var speed: Int? = 0

    @ColumnInfo(name = "createTime")
    var createTime: Long = System.currentTimeMillis()

    @ColumnInfo(name = "direction")
    var direction: Float? = 0.0f

    override fun toString(): String {
        return "[ id:$id,createTime:$createTime]"
    }
}

fun Location.toStorage(): LocationStorage {
    return LocationStorage().also {
        it.altitude = altitude
        it.createTime = createTime
        it.direction = direction
        it.latitude = latitude
        it.longitude = longitude
        it.radius = radius
        it.speed = speed

    }
}

fun <T> T?.toOption(): Optional<T> {
    return Optional.fromNullable(this)
}
