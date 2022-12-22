package com.hmncube.myweather.data.remote

import com.hmncube.myweather.data.remote.models.ApiWeatherData
import com.hmncube.myweather.data.remote.models.GeoCodeResponse
import com.hmncube.myweather.data.remote.models.Geocode
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {
    @GET("data/2.5/forecast")
    suspend fun GetWeather(
        @Query("lat") latitude : Double,
        @Query("lon") longitude : Double,
        @Query("appid") appId : String,
        @Query("units") units : String,
    ) : ApiWeatherData

    @GET("geo/1.0/direct")
    suspend fun GetPlaceCoordinates(
        @Query("q") placeName : String,
        @Query("limit") limit : Int,
        @Query("appid") appId : String,
    ) : List<Geocode>
}