package com.compi.dinhnt.travelplanner.network

import com.compi.dinhnt.travelplanner.Constant
import com.compi.dinhnt.travelplanner.Constant.OPEN_WEATHER_URL
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/forecast")
    fun getWeatherByLatLong(
        @Query("lat") startDate: Double,
        @Query("lon") endDate: Double,
        @Query("appid") key: String = Constant.WEATHER_API_KEY,
        ): Call<String>
}

object Network {
    val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(OPEN_WEATHER_URL)
            .build()
            .create(WeatherApiService::class.java)
    }
}