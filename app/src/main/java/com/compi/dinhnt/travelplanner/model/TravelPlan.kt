package com.compi.dinhnt.travelplanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.compi.dinhnt.travelplanner.R
import java.util.*


data class TravelPlan(
    val id: Long,
    val name: String,
    val activityNumber: Int,
    val startDate: Date?,
    val endDate: Date?,
    val weathers: List<Weather>? = null
) {
    fun toListWeathersDetail() = weathers?.map { it.weather }
}


@Entity(tableName = "travelplan")
data class TravelPlanCTO(
    @PrimaryKey
    val id: Long,
    val name: String,
    val weathers: List<Weather>? = null
) {
}

data class Weather(
    val timeStamp: Long,
    val weather: WeatherDetail
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
    }
}

