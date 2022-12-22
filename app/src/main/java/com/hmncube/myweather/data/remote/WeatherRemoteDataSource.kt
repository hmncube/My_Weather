package com.hmncube.myweather.data.remote

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRemoteDataSource @Inject constructor(//private val ioDispatcher: CoroutineDispatcher,
                              private val api: OpenMateoService) {

    //todo Make the inputs options for the user to select V2 ie hourly, daily
    fun getWeatherData(latitude: Double, longitude: Double) = flow{
        //withContext(Dispatchers.IO) {
        emit(api.GetWeather(
                latitude,
                longitude,
                "best_match",
                "temperature_2m,relativehumidity_2m,weathercode,windspeed_10m",
                "weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset,precipitation_sum",
                true,
                "auto"
            ))
        }
}