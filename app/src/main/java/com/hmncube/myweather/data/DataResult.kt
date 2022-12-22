package com.hmncube.myweather.data

data class DataResult(
    var data : Any,
    val isSuccess : Boolean,
    val error : String
)