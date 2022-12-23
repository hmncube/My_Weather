package com.hmncube.myweather.data.remote.models

import com.google.gson.annotations.SerializedName


data class Sys (
  @SerializedName("pod" )
  var pod : String? = null
)