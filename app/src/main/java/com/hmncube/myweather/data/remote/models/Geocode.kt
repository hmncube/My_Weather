package com.hmncube.myweather.data.remote.models

data class GeoCodeResponse(
    var cod : String?,
    var message : Int?,
    var geocodes: ArrayList<Geocode>
)
data class Geocode(
    val name : String,
    val lat : Double,
    val lon : Double,
    val country : String,
    val state : String
)
