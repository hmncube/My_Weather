package com.hmncube.myweather.data

import android.util.Log
import com.google.gson.Gson
import com.hmncube.myweather.BuildConfig
import com.hmncube.myweather.data.database.AppDatabase
import com.hmncube.myweather.data.database.WeatherData
import com.hmncube.myweather.data.remote.OpenWeatherService
import com.hmncube.myweather.data.remote.WeatherRemoteDataSource
import com.hmncube.myweather.data.remote.models.Geocode
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val database: AppDatabase,
    private val api : OpenWeatherService,
) {
    private val apiKey = BuildConfig.OpenWeatherAPi

    suspend fun refreshData(lat: Double, long: Double) {
        val key = generateKey(lat, long)
        val data = database.readData(key)
        Log.d("WeatherRepo", "refreshData: $data")
        if (data.isNullOrBlank()) {
            //no data in db
            getWeatherFromRemoteSource(lat, long)
        } else {

            val weatherData = Gson().fromJson(data, WeatherData::class.java)
            if (weatherData.forecasts.isEmpty()) {
                getWeatherFromRemoteSource(lat, long)
                return
                //throw WeatherRefreshError("Could not get the weather", null)
            }
            val lastWeatherData = weatherData.forecasts[weatherData.forecasts.size - 1]
            val date = lastWeatherData.dt!!
            val dateText = lastWeatherData.dtTxt!!
            val dateObj = Date(date.toLong() * 1000)
            val today = Calendar.getInstance().time
            Log.d("ppppp", "refreshData: $dateObj")
            Log.d("ppppp", "refreshData: $today")

            //todo rework develop algorithm to dertemine if date is tommorrow
            if (today.after(dateObj)) {
                Log.d("WeatherRepo", "refreshData: old")
                // weather is old
                database.deleteData(key)
                getWeatherFromRemoteSource(lat, long)
            } else {
                //remove old weather conditions
                val updatedForecast = weatherData.forecasts.filter {
                    Date(it.dt?.toLong()!! * 1000).after(today)
                }
                val updatedWeather = WeatherData(updatedForecast, weatherData.city)
                database.deleteData(key)
                database.saveData(updatedWeather, key)
            }
        }
    }

    suspend fun getWeatherFromRemoteSource(lat: Double, long: Double) {
        val result = api.GetWeather(
            lat,
            long,
            apiKey,
            "metric"
        )

        if (result.cod == "200") {
            val data = WeatherData(result.list.toList(), result.city!!)
            saveDataToDb(data, generateKey(lat, long))
        } else {
            //error
            throw WeatherRefreshError("Failed to fetch weather",
                Throwable(result.cod))
        }
    }

    private fun generateKey(lat: Double, long: Double): String {
        return "$lat - $long"
    }


    private fun saveDataToDb(result: WeatherData, key : String) {
        Log.d("pundez", "saveDataToDb: SAVINF $result")
        database.saveData(result, key)
    }

    fun getWeatherFromLocalSource(lat: Double, long: Double): WeatherData? {
        val data = database.readData(generateKey(lat, long))
        return Gson().fromJson(data, WeatherData::class.java)
    }

    suspend fun searchPlace(place: String): List<Geocode> {
        try {
            return api.GetPlaceCoordinates(place, 10, apiKey)
        } catch (ex: java.lang.Exception){
            throw WeatherRefreshError("Could not search for the mentioned place", null)
        }

    }
}

class WeatherRefreshError(message: String, cause: Throwable?) : Throwable(message, cause)