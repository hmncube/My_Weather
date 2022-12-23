package com.hmncube.myweather.ui.forecast_weather

import java.util.Date

data class DailyForecastData(
    val temperature : Int,
    val date : Date,
    val weatherDescription : String,
    val windSpeed : Double,
    val humidity : Int,
    val visibility : Int,
    val weatherIcon : Int,
)
