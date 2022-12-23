package com.hmncube.myweather.data.remote.models

data class Geocode(
    val name : String,
    val lat : Double,
    val lon : Double,
    val country : String,
    val state : String
)
