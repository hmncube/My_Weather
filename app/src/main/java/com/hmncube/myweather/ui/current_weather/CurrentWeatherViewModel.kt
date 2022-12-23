package com.hmncube.myweather.ui.current_weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hmncube.myweather.R
import com.hmncube.myweather.data.WeatherRefreshError
import com.hmncube.myweather.data.WeatherRepository
import com.hmncube.myweather.data.database.WeatherData
import com.hmncube.myweather.data.remote.models.Geocode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(private val repository: WeatherRepository)
    : ViewModel() {
    private val _finishedLoading = MutableLiveData(false)
    val finishedLoading : LiveData<Boolean>
        get() = _finishedLoading

    private val _searchLoading = MutableLiveData(false)
    val searchLoading : LiveData<Boolean>
        get() = _searchLoading

    private val _snackBar = MutableLiveData<String?>()
    val snackBar : LiveData<String?>
        get() = _snackBar

    private val _weatherData = MutableLiveData<WeatherData>()
    val weatherData : LiveData<WeatherData>
        get() = _weatherData

    private val _chartData = MutableLiveData<List<ChartData>>()
    val chartData : LiveData<List<ChartData>>
        get() = _chartData


    private val _forecastData = MutableLiveData<List<ForecastData>>()
    val forecastData : LiveData<List<ForecastData>>
        get() = _forecastData

    private val _geoCodeResults = MutableLiveData<List<Geocode>>()
    val geoCodeResults get() = _geoCodeResults


    private var latitude : Double? = null
    private var longitude : Double? = null

    fun refresh() {
        viewModelScope.launch {
            try {
                _finishedLoading.value = false
                repository.refreshData(latitude!!, longitude!!)
                _weatherData.value = repository.getWeatherFromLocalSource(
                    latitude!!, longitude!!)
                _chartData.value = generateChartData(weatherData.value!!)
                _forecastData.value = generateForecastData(weatherData.value!!)
            } catch (error : WeatherRefreshError) {
                _snackBar.value = error.message
            } finally {
                _finishedLoading.value = true
            }
        }
    }

    //todo rework
    private fun generateForecastData(data: WeatherData): List<ForecastData> {
        val forecasts = mutableListOf<ForecastData>()
        var lastDate = Calendar.getInstance().time
        lastDate.hours = 0
        lastDate.minutes = 0
        lastDate.seconds = 0
        for (forecast in data.forecasts) {
            val date = Date(forecast.dt?.toLong()!! * 1000)
            if (date.date != lastDate.date) {
                lastDate = date
                lastDate.hours = 0
                lastDate.minutes = 0
                lastDate.seconds = 0
                forecasts.add(
                    ForecastData(
                        getIconFromWeatherCode(forecast.weather[0].icon!!),
                        forecast.dtTxt?.split(" ")?.get(0) ?: "",
                        forecast.main?.tempMax?.roundToInt().toString()
                    )
                )
            }
        }
        return forecasts
    }

    //todo rework and use from ImageUtile and Animation
    private fun getIconFromWeatherCode(weatherCode: String) : Int {
        return when (weatherCode) {
            "01d" -> R.drawable.day_clear
            "02d" -> R.drawable.day_partial_cloud
            "03d" -> R.drawable.cloudy
            "04d" -> R.drawable.overcast
            "09d" -> R.drawable.rain
            "10d" -> R.drawable.day_rain
            "11d" -> R.drawable.day_rain_thunder
            "13d" -> R.drawable.day_snow
            "50d" -> R.drawable.mist
            "01n" -> R.drawable.night_full_moon_clear
            "02n" -> R.drawable.night_full_moon_partial_cloud
            "03n" -> R.drawable.cloudy
            "04n" -> R.drawable.overcast
            "09n" -> R.drawable.rain
            "10n" -> R.drawable.night_full_moon_rain
            "11n" -> R.drawable.night_full_moon_rain_thunder
            "13n" -> R.drawable.night_full_moon_snow
            "50n" -> R.drawable.mist
            else -> R.drawable.day_clear
        }
    }

    private fun generateChartData(data: WeatherData): List<ChartData> {
        val chartData = mutableListOf<ChartData>()

        val forecasts = data.forecasts.take(5)
        for (forecast in forecasts) {
            chartData.add(
                ChartData(
                    getTime(forecast.dtTxt!!),
                    forecast.main?.tempMax!!)
            )
        }
        return chartData
    }

    private fun getTime(date : String) : String {
        val time = date.split(" ")[1].split(":")[0]
        return "${time}:00"
    }

    fun setCoordinates(lat : Double, long : Double) {
        latitude = lat
        longitude = long
    }

    fun getFreshWeatherData() {
        _finishedLoading.value = false
        viewModelScope.launch {
            repository.getWeatherFromRemoteSource(latitude!!, longitude!!)
            _weatherData.value = repository.getWeatherFromLocalSource(
                latitude!!, longitude!!)
            _finishedLoading.value = true
        }
    }

    fun getCoodForPlace(place: String) {
        _searchLoading.value = true
        viewModelScope.launch {
            try {
                val results = repository.searchPlace(place)
                geoCodeResults.value = results
            } catch (ex : WeatherRefreshError) {
                _snackBar.value = ex.message
            } finally {
                _searchLoading.value = false
            }
        }
    }
}