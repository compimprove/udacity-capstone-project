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
    val imageUrl: String? = null,
    val startDate: Date?,
    val endDate: Date?
)


@Entity(tableName = "travelplan")
data class TravelPlanCTO(
    val name: String,
    val imageUrl: String? = null,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
)



