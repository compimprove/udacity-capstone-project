package com.compi.dinhnt.travelplanner.utils

import com.compi.dinhnt.travelplanner.network.Network
import com.compi.dinhnt.travelplanner.network.getWeather
import org.json.JSONObject
import retrofit2.await

fun JSONObject.getFirstMediumImageFromPexel(ratioMax: Double, ratioMin: Double): String? {
    val list = this.getJSONArray("photos")
    for (index in 0 until list.length()) {
        val photo = list.getJSONObject(index)
        val ratio = photo.getInt("width").toDouble() / photo.getInt("height").toDouble()
        if (ratio in ratioMin..ratioMax) {
            return photo.getJSONObject("src").getString("medium")
        }
    }
    return null
}

object TravelPlanUtils {
    private const val maxPageSearched = 10

    suspend fun searchImage(name: String): String? {
        for (page in 1..maxPageSearched) {
            val data = Network.pexelApiService.searchImage(name, page).await()
            val imageUrl = JSONObject(data).getFirstMediumImageFromPexel(1.65, 1.35)
            if (imageUrl != null) return imageUrl
        }
        return null
    }
}