package com.hmncube.myweather.ui.forecast_weather

import java.util.Date

data class DailyForecastData(
    val maxTemperature : Int,
    val minTemperature : Int,
    val date : Date,
    val weatherDescription : String,
    val windSpeed : Double,
    val humidity : Int,
    val visibility : Int,
    val weatherIcon : Int,
)
