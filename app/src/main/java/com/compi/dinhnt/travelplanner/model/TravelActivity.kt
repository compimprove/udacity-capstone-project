package com.compi.dinhnt.travelplanner.model

import android.content.res.Resources
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.compi.dinhnt.travelplanner.R
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.util.*

@Entity
data class TravelActivity(
    val name: String,
    val type: ActivityType,
    val date: Date,
    val time: Long, // in minutes
    val travelPlanId: String,
    @Embedded
    val location: Location? = null,
    val note: String?,
    @Embedded
    var weather: Weather? = null,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
) : Comparable<TravelActivity>, Serializable {
    override fun compareTo(other: TravelActivity): Int {
        return time.compareTo(other.time)
    }

}

data class Weather(
    val weatherTimeStamp: Long,
    val weatherDetail: WeatherDetail?
)

enum class WeatherDetail {
    CLEAR_SKY,
    FEW_CLOUDS,
    SCATTERED_CLOUDS,
    BROKEN_CLOUDS,
    SHOWER_RAIN,
    RAIN,
    THUNDERSTORM,
    SNOW,
    MIST;

    fun getIcon() = when (this) {
        CLEAR_SKY -> R.drawable.ic_cloud
        FEW_CLOUDS -> R.drawable.ic_cloud
        SCATTERED_CLOUDS -> R.drawable.ic_cloud
        BROKEN_CLOUDS -> R.drawable.ic_broken_clouds
        SHOWER_RAIN -> R.drawable.ic_shower_rain
        RAIN -> R.drawable.ic_rain
        THUNDERSTORM -> R.drawable.ic_thunderstorm
        SNOW -> R.drawable.ic_snow
        MIST -> R.drawable.ic_mist
    }

    companion object {
        fun getWeather(str: String) = when (str) {
            "clear sky" -> CLEAR_SKY
            "few clouds" -> FEW_CLOUDS
            "scattered clouds" -> SCATTERED_CLOUDS
            "broken clouds" -> BROKEN_CLOUDS
            "shower rain" -> SHOWER_RAIN
            "rain" -> RAIN
            "thunderstorm" -> THUNDERSTORM
            "snow" -> SNOW
            "mist" -> MIST
            else -> CLEAR_SKY
        }

        fun getWeatherFromCode(str: String) = when (str) {
            "01" -> CLEAR_SKY
            "02" -> FEW_CLOUDS
            "03" -> SCATTERED_CLOUDS
            "04" -> BROKEN_CLOUDS
            "09" -> SHOWER_RAIN
            "10" -> RAIN
            "11" -> THUNDERSTORM
            "13" -> SNOW
            "50" -> MIST
            else -> CLEAR_SKY
        }
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
    val activities: MutableList<TravelActivity>
)

data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String
)