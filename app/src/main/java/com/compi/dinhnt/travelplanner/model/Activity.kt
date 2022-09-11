package com.compi.dinhnt.travelplanner.model

import android.content.res.Resources
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.compi.dinhnt.travelplanner.R
import com.google.android.gms.maps.model.LatLng
import java.util.*

@Entity
data class Activity(
    val name: String,
    val type: ActivityType,
    val date: Date,
    val time: Long, // in minutes
    val travelPlanId: Long,
    @Embedded
    val location: Location? = null,
    val note: String?,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
) : Comparable<Activity> {
    override fun compareTo(other: Activity): Int {
        return time.compareTo(other.time)
    }

}

enum class ActivityType {
    HOTEL, FLIGHT, EATING, PLAYING, TAXI;

    fun getIcon() = when (this) {
        HOTEL -> R.drawable.ic_hotel
        FLIGHT -> R.drawable.ic_flight
        EATING -> R.drawable.ic_eating
        PLAYING -> R.drawable.ic_playing
        TAXI -> R.drawable.ic_taxi
    }

    fun toString(resources: Resources) = when (this) {
        HOTEL -> resources.getString(R.string.HOTEL)
        FLIGHT -> resources.getString(R.string.FLIGHT)
        EATING -> resources.getString(R.string.EATING)
        PLAYING -> resources.getString(R.string.PLAYING)
        TAXI -> resources.getString(R.string.TAXI)
    }
}

data class TravelPlanWithActivity(
    @Embedded val travelPlanCTO: TravelPlanCTO,
    @Relation(
        parentColumn = "id",
        entityColumn = "travelPlanId"
    )
    val activities: MutableList<Activity>
)

data class Location(
    val latLong: LatLng,
    val locationName: String
)