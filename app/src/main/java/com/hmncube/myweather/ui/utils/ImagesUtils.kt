package com.hmncube.myweather.ui.utils

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.hmncube.myweather.R

class ImagesUtils {
    companion object {
        fun getAnimationFromWeatherCode(weatherCode: String) : Int {
            return when (weatherCode) {
                "01d" -> R.raw.sun
                "02d" -> R.raw.day_partial_cloud
                "03d" -> R.raw.cloudy_day
                "04d" -> R.raw.overcast
                "09d" -> R.raw.raining
                "10d" -> R.raw.day_rain
                "11d" -> R.raw.day_rain_thunder
                "13d" -> R.raw.snow_day
                "50d" -> R.raw.mist
                "01n" -> R.raw.moon
                "02n" -> R.raw.night_full_moon_partial_cloud
                "03n" -> R.raw.cloudy_night
                "04n" -> R.raw.overcast
                "09n" -> R.raw.raining
                "10n" -> R.raw.night_rain
                "11n" -> R.raw.night_thunderstorm
                "13n" -> R.raw.snow_night
                "50n" -> R.raw.mist
                else -> R.raw.sun
            }
        }

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

    }
}