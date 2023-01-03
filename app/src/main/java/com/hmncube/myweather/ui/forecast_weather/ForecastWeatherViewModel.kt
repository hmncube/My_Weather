package com.hmncube.myweather.ui.forecast_weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hmncube.myweather.data.WeatherRepository
import com.hmncube.myweather.data.database.WeatherData
import com.hmncube.myweather.data.remote.models.WeatherValues
import com.hmncube.myweather.ui.utils.ImagesUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.RoundingMode
import java.text.DecimalFormat
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

    private fun processData(weatherData: WeatherData): List<DailyForecastData> {
        val forecasts = mutableListOf<DailyForecastData>()

        var max = Double.MIN_VALUE
        var min = Double.MAX_VALUE
        var weatherIcons = mutableListOf<String>()
        var weatherDescriptions = mutableListOf<String>()
        var humidityList = mutableListOf<Int>()
        var windSpeedList = mutableListOf<Double>()
        var visibilityList = mutableListOf<Int>()

        val weatherForecasts = removeCurrentDayWeather(weatherData.forecasts)
        var lastDate = Date(weatherForecasts[0].dt?.toLong()!! * 1000).toCalendar()
        for (forecast in weatherForecasts) {
            val date = Date(forecast.dt?.toLong()!! * 1000).toCalendar()
            if (date.get(Calendar.DAY_OF_MONTH) == lastDate.get(Calendar.DAY_OF_MONTH)) {
                val temp = forecast.main?.temp!!
                if (temp > max) {
                    max = temp
                } else if (temp < min) {
                    min = temp
                }
                weatherIcons.add(forecast.weather[0].icon!!)
                weatherDescriptions.add(forecast.weather[0].description!!)
                humidityList.add(forecast.main?.humidity!!)
                windSpeedList.add(forecast.wind?.speed!!)
                visibilityList.add(forecast.visibility!!)
            } else {
                val weatherIcon = getFrequentElement(weatherIcons)
                weatherIcons = mutableListOf()

                val description = getFrequentElement(weatherDescriptions)
                weatherDescriptions = mutableListOf()

                val averageHumidity = calculateHumidityAverage(humidityList)
                humidityList = mutableListOf()

                val averageWindSpeed = calculateWindSpeedAverage(windSpeedList)
                windSpeedList = mutableListOf()

                val averageVisibility = calculateHumidityAverage(visibilityList) / 1000
                visibilityList = mutableListOf()

                forecasts.add(
                    DailyForecastData(
                        max.roundToInt(),
                        min.roundToInt(),
                        lastDate.time,
                        description,
                        averageWindSpeed,
                        averageHumidity,
                        averageVisibility,
                        ImagesUtils.getAnimationFromWeatherCode(weatherIcon)
                    )
                )
                max = Double.MIN_VALUE
                min = Double.MAX_VALUE
                lastDate = date
            }
        }
        return forecasts
    }

    private fun calculateHumidityAverage(list: MutableList<Int>): Int {
        return list.sum() / list.size
    }

    private fun calculateWindSpeedAverage(windSpeedList: MutableList<Double>): String {
        val averageSpeed = (windSpeedList.sum() / windSpeedList.size) * 3.6
        val df = DecimalFormat("##.##")
        df.roundingMode = RoundingMode.DOWN
        return df.format(averageSpeed)
    }

    private fun removeCurrentDayWeather(forecasts: List<WeatherValues>): List<WeatherValues> {
        val values = mutableListOf<WeatherValues>()
        val todayDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        for (forecast in forecasts) {
            val date = Date(forecast.dt?.toLong()!! * 1000).toCalendar()
            val day = date.get(Calendar.DAY_OF_MONTH)
            if (todayDate != day) {
                values.add(forecast)
            }
        }
        return values
    }

    private fun isNotToday(date: Calendar): Boolean {
        val month = date.get(Calendar.MONTH)
        val day = date.get(Calendar.DAY_OF_MONTH)

        val thisMonth = Calendar.getInstance().get(Calendar.MONTH)
        val thisDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        if (thisMonth == month) {
            return day != thisDate
        }
        return true
    }

    private fun getFrequentElement(data: MutableList<String>): String {
        val descriptionCounts = data.groupingBy { it }.eachCount()
        return descriptionCounts.maxBy { it.value }.key
    }
}
//todo
private fun Date.toCalendar(): Calendar {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}
