package com.hmncube.myweather.ui.forecast_weather

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hmncube.myweather.R
import com.hmncube.myweather.databinding.FragmentForecastWeatherBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastWeatherFragment : Fragment(), ForecastClickListener {

    companion object {
        fun newInstance() = ForecastWeatherFragment()
    }

    private lateinit var viewModel: ForecastWeatherViewModel
    private lateinit var adapter : ForecastAdapter

    private var _binding : FragmentForecastWeatherBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForecastWeatherViewModel::class.java)
        binding.forecastRv.layoutManager = LinearLayoutManager(requireContext())
        adapter = ForecastAdapter(this)
        binding.forecastRv.adapter = adapter
        viewModel.getForecastData()
        viewModel.forecastData.observe(requireActivity()) { forecasts ->
            updateUi(forecasts)
        }
    }

    private fun updateUi(forecasts: List<DailyForecastData>) {
        adapter.setData(forecasts.toMutableList())
        if (forecasts.isNotEmpty()) {
            val forecast = forecasts.first()
            updateCard(forecast)
        }
    }

    private fun updateCard(forecast: DailyForecastData) {
        Log.d("pundez", "updateCard: max = ${forecast.maxTemperature} min = ${forecast.minTemperature}")
        binding.temp.text = String.format(
            resources.getString(R.string.metric_temperature_units),
            forecast.maxTemperature
        )

        binding.minTemp.text = String.format(
            resources.getString(R.string.metric_temperature_units),
            forecast.minTemperature
        )

        binding.mainIcon.setAnimation(forecast.weatherIcon)
        binding.description.text = forecast.weatherDescription

        binding.date.text = DateFormat.format("E, d MMM", forecast.date)
        binding.windValue.text = String.format(
            resources.getString(R.string.metric_wind_speed_units),
            forecast.windSpeed
        )
        binding.humidityValue.text = String.format(
            resources.getString(R.string.humidity_units),
            forecast.humidity
        )
        binding.visibilityValue.text = String.format(
            resources.getString(R.string.visibility_units),
            forecast.visibility
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(item: DailyForecastData) {
        updateCard(item)
    }
}

interface ForecastClickListener {
    fun onClick(item : DailyForecastData)
}
