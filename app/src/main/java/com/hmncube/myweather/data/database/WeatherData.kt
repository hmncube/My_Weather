package com.hmncube.myweather.data.database

import com.hmncube.myweather.data.remote.models.City

data class WeatherData(
    var forecasts : List<com.hmncube.myweather.data.remote.models.WeatherValues>,
    var city: City
)
