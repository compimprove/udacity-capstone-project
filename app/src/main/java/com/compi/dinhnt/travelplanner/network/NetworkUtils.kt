package com.compi.dinhnt.travelplanner.network

import com.compi.dinhnt.travelplanner.model.WeatherDetail
import org.json.JSONObject
import java.util.*

private data class WeatherResponse(val timeStamp: Long, val iconCode: String, val timeStr: String)


fun JSONObject.getWeather(date: Date): WeatherDetail? {
    val dateTp = date.time
    val list = this.getJSONArray("list")
    val listWeatherResponse = mutableListOf<WeatherResponse>()
    for (index in 0 until list.length()) {
        val weather = list.getJSONObject(index)
        val timeStamp = weather.getInt("dt")
        val icon =
            weather.getJSONArray("weather").getJSONObject(0).getString("icon").substring(0, 2)
        val timeStr = weather.getString("dt_txt")
        listWeatherResponse.add(WeatherResponse(timeStamp.toLong() * 1000, icon, timeStr))
    }
    listWeatherResponse.sortWith { o1, o2 ->
        if (o1 != null && o2 != null) {
            kotlin.math.abs(o1.timeStamp - dateTp).compareTo(kotlin.math.abs(o2.timeStamp - dateTp))
        } else {
            0
        }
    }
    val delta = listWeatherResponse[0].timeStamp - dateTp
    return if (kotlin.math.abs(delta / 1000 / 3600) <= 4) {
        WeatherDetail.getWeatherFromCode(listWeatherResponse[0].iconCode)
    } else {
        null
    }
}

