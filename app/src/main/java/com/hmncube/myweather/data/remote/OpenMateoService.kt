package com.hmncube.myweather.data.remote

import com.hmncube.myweather.data.remote.models.ApiWeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMateoService {
    @GET("forecast")
    suspend fun GetWeather(
        @Query("latitude") latitude : Double,
        @Query("longitude") longitude : Double,
        @Query("models") models : String,
        @Query("hourly", encoded = true) hourly : String,
        @Query("daily", encoded = true) daily : String,
        @Query("current_weather") current : Boolean,
        @Query("timezone") timezone : String,
    ) : ApiWeatherData
}