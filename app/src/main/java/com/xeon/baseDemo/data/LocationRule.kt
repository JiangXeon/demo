package com.xeon.baseDemo.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.threeten.bp.DayOfWeek
import java.lang.Exception
import java.util.*
import java.io.Serializable


data class Dto<out T>(
    @JsonProperty("success") val success: Boolean = false,
    @JsonProperty("data") val data: T? = null,
    @JsonProperty("errMsg") val errMsg: String? = null
) {

    init {
        if (!success) {
            throw Exception(errMsg)
        }
    }
}


data class LocationRule(
    @get:JsonProperty("weekDayList") val weekDayList: List<Int>,
    @get:JsonProperty("locationInterval") val locationInterval: Long,
    @get:JsonProperty("startTime") val startTime: Long,
    @get:JsonProperty("duration") val duration: Long,
    @get:JsonProperty("enable") val enable: Boolean,
    @get:JsonProperty("upLoadInterval") val upLoadInterval: Long,
    @get:JsonProperty("desc") val desc: String?
) : Serializable {

    constructor() : this(
        listOf(),
        0,
        0,
        0,
        true,
        0,
        ""
    )

    @get:JsonIgnore
    val locationReportWeekDays: SortedSet<DayOfWeek> by lazy {
        if (weekDayList.isEmpty()) {
            return@lazy ALL_WEEK_DAYS
        }

        val dayOfWeeks = TreeSet<DayOfWeek>()
        weekDayList.forEachIndexed { index, i ->
            if (i != 0) {
                dayOfWeeks.add(DayOfWeek.of(index + 1))
            }
        }

        dayOfWeeks
    }

    companion object {
        private val ALL_WEEK_DAYS = DayOfWeek.values().toSortedSet()
    }
}