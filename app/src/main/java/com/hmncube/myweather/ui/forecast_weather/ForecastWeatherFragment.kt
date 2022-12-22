package com.hmncube.myweather.ui.forecast_weather

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hmncube.myweather.R
import com.hmncube.myweather.databinding.FragmentForecastWeatherBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = ForecastWeatherFragment()
    }

    private lateinit var viewModel: ForecastWeatherViewModel
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
        //todo get adapter data from view model
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}