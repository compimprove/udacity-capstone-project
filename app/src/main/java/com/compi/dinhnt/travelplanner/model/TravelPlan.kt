package com.compi.dinhnt.travelplanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.compi.dinhnt.travelplanner.R
import java.io.Serializable
import java.util.*


data class TravelPlan(
    val id: String,
    val name: String,
    val activityNumber: Int,
    val startDate: Date?,
    val endDate: Date?,
    val weathers: List<Weather>? = null
)


@Entity(tableName = "travelplan")
data class TravelPlanCTO(
    val name: String,
    val weathers: List<Weather>? = null,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
) {
}



