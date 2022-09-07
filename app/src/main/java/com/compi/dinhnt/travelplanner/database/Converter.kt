package com.compi.dinhnt.travelplanner.database

import androidx.room.TypeConverter
import com.compi.dinhnt.travelplanner.model.Weather
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.util.*

class ActivityDateConverter {
    @TypeConverter
    fun toDate(date: Long?): Date? {
        return date?.let { Date(date) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}

class MapStringStringConverter {
    @TypeConverter
    fun toMap(json: String?): Map<String, String>? {
        return json?.let {
            Gson().fromJson(it, Map::class.java) as Map<String, String>
        }
    }

    @TypeConverter
    fun fromMap(map: Map<String, String>?): String? {
        return map?.let {
            Gson().toJson(it)
        }
    }
}

class LatLngConverter {
    @TypeConverter
    fun toMap(json: String?): LatLng? {
        return json?.let {
            Gson().fromJson(it, LatLng::class.java)
        }
    }

    @TypeConverter
    fun fromMap(latLng: LatLng?): String? {
        return latLng?.let {
            Gson().toJson(latLng)
        }
    }
}

class ListWeatherConverter {
    @TypeConverter
    fun toMap(json: String?): List<Weather>? {
        return json?.let {
            Gson().fromJson(it, List::class.java) as List<Weather>
        }
    }

    @TypeConverter
    fun fromMap(map: List<Weather>?): String? {
        return map?.let {
            Gson().toJson(it)
        }
    }
}