package com.hmncube.myweather.data.database

import com.hmncube.myweather.data.remote.models.City

data class WeatherData(
    var forecasts : List<com.hmncube.myweather.data.remote.models.List>,
    var city: City
)
