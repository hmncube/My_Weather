package com.hmncube.myweather.ui.forecast_weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hmncube.myweather.data.WeatherRepository
import com.hmncube.myweather.data.database.WeatherData
import com.hmncube.myweather.ui.utils.ImagesUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ForecastWeatherViewModel @Inject constructor(private val repository: WeatherRepository) : ViewModel() {

    private var _loading = MutableLiveData<Boolean>(true)
    val loading : LiveData<Boolean>
        get() = _loading

    private var _forecastData = MutableLiveData<List<DailyForecastData>>()
    val forecastData : LiveData<List<DailyForecastData>>
        get() = _forecastData

    fun getForecastData(){
        _loading.value = true
        //todo
        val lat = -17.83
        val long = 31.05
        val weatherData = repository.getWeatherFromLocalSource(lat, long)
        _forecastData.value = processData(weatherData!!)
        _loading.value = false
    }

    //todo rework
    private fun processData(weatherData: WeatherData): List<DailyForecastData> {
        val forecasts = mutableListOf<DailyForecastData>()
        var lastDate = Calendar.getInstance().time
        lastDate.hours = 0
        lastDate.minutes = 0
        lastDate.seconds = 0
        for (forecast in weatherData.forecasts) {
            val date = Date(forecast.dt?.toLong()!! * 1000)
            if (date.date != lastDate.date) {
                lastDate = date
                lastDate.hours = 0
                lastDate.minutes = 0
                lastDate.seconds = 0
                forecasts.add(
                    DailyForecastData(
                        forecast.main?.tempMax?.roundToInt()!!,
                        date,
                        forecast.weather[0].description!!,
                        forecast.wind?.speed!!,
                        forecast.main?.humidity!!,
                        forecast.visibility!! / 1000,
                        ImagesUtils.getAnimationFromWeatherCode(forecast.weather[0].icon!!)
                    )
                )
            }
        }
        return forecasts
    }
}
