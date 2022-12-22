package com.hmncube.myweather.ui.current_weather

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED
import com.google.android.gms.common.ConnectionResult.SUCCESS
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.hmncube.myweather.R
import com.hmncube.myweather.data.WeatherRepository
import com.hmncube.myweather.data.database.WeatherData
import com.hmncube.myweather.data.remote.models.Geocode
import com.hmncube.myweather.databinding.FragmentCurrentWeatherBinding
import com.hmncube.myweather.ui.MyLocationManager
import com.hmncube.myweather.ui.dialogs.search.SearchDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class CurrentWeatherFragment : Fragment(), SearchListener {

    companion object {
        fun newInstance() = CurrentWeatherFragment()
    }

    private val TAG = "CurrentWeather";
    lateinit var viewModel: CurrentWeatherViewModel
    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!
    lateinit var locationPermissionRequest : ActivityResultLauncher<String>
    lateinit var fusedLocationClient : FusedLocationProviderClient
    @Inject lateinit var repository: WeatherRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {  permission ->
            if (permission) {
                getUserLocation()
            } else {
                Snackbar.make(
                    binding.root,
                    "No permissions granted",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrentWeatherViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val locationPermissionsGranted = MyLocationManager.checkPermission(requireContext())
        val googleService = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext())
        if (googleService == SUCCESS) {
            if (locationPermissionsGranted) {
                val isLocationEnabled = MyLocationManager.isLocationEnabled(requireContext())
                if (isLocationEnabled) {
                    getUserLocation()
                } else {
                    Snackbar.make(
                        binding.root,
                        "Please enable the location to continue",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                locationPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        } else if (googleService == SERVICE_VERSION_UPDATE_REQUIRED) {
            //todo design notifications
            //update
        } else {
            // install
        }
        val lat = -17.83
        val long = 31.05
        viewModel.setCoordinates(lat, long)
        viewModel.refresh()

        binding.refreshLayout.setOnRefreshListener {
            viewModel.getFreshWeatherData()
        }

        viewModel.weatherData.observe(requireActivity()) { weatherData ->
            Log.d("CurrentFrag", "onActivityCreated: $weatherData")
            if (weatherData == null || weatherData.forecasts.isEmpty()) {
                viewModel.getFreshWeatherData()
            } else {
                updateScreen(weatherData)
            }
        }

        viewModel.chartData.observe(requireActivity()) {chartData ->
            generateChart(chartData)
        }

        val adapter = ForecastAdapter()
        binding.forecastRv.adapter = adapter
        
        viewModel.forecastData.observe(requireActivity()) { data ->
            Log.d(TAG, "onActivityCreated: ${data.size}")
            adapter.setData(data.toMutableList())
        }

        viewModel.finishedLoading.observe(requireActivity()) { loading ->
            if (loading && binding.currentCardAnimationView.visibility == View.VISIBLE) {
                binding.currentCardAnimationView.visibility = View.GONE
                binding.forecastAnimationView.visibility = View.GONE
                binding.chartAnimationView.visibility = View.GONE
                binding.cardAnimationView.visibility = View.GONE
                binding.detailsLayout.visibility = View.VISIBLE
                binding.forecastRv.visibility = View.VISIBLE
                binding.chartContainer.visibility = View.VISIBLE
                binding.cardContainer.visibility = View.VISIBLE
                binding.refreshLayout.isRefreshing = false
            } else if (!loading && binding.currentCardAnimationView.visibility == View.GONE) {
                Log.d(TAG, "onActivityCreated: pundez ")
                binding.currentCardAnimationView.visibility = View.VISIBLE
                binding.forecastAnimationView.visibility = View.VISIBLE
                binding.chartAnimationView.visibility = View.VISIBLE
                binding.cardAnimationView.visibility = View.VISIBLE
                binding.detailsLayout.visibility = View.GONE
                binding.forecastRv.visibility = View.GONE
                binding.chartContainer.visibility = View.GONE
                binding.cardContainer.visibility = View.GONE
                binding.refreshLayout.isRefreshing = true
            }
        }
        
        viewModel.snackBar.observe(requireActivity()){
            Log.d(TAG, "onActivityCreated: pundez Error $it")
            it?.let { it1 -> Snackbar.make(binding.root, it1, Snackbar.LENGTH_SHORT).show() }
        }

        binding.searchBtn.setOnClickListener {
            showSearchView()
        }

        binding.forecast.setOnClickListener {
            findNavController().navigate(R.id.action_currentWeatherFragment_to_forecastWeatherFragment)
        }
    }

    private fun showSearchView() {
        val searchDialog = SearchDialogFragment(this)
        searchDialog.show(childFragmentManager, searchDialog.tag)
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { 
            val location = it.result
        }
    }

    //todo update split to two functions generate and display
    private fun generateChart(chartData: List<ChartData>) {
        //chart
        binding.chart.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.primary)
        )
        val maxTempDataEntries = mutableListOf<Entry>()
        val dates = mutableListOf<String>()

        for (x in chartData.indices) {
            maxTempDataEntries.add(Entry(x.toFloat(), chartData[x].max.toFloat()))
            dates.add(chartData[x].date)
        }
        val maxTempLineDataSet = LineDataSet(maxTempDataEntries, "Maximum Temperature")
        val valueFormatter: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val temp = value.toInt()
                return String.format(
                    resources.getString(R.string.metric_temperature_units),
                    temp
                )
            }
        }
        maxTempLineDataSet.valueFormatter = valueFormatter
        maxTempLineDataSet.circleHoleColor = ContextCompat.getColor(requireContext(), R.color.on_primary)
        maxTempLineDataSet.highLightColor = ContextCompat.getColor(requireContext(), R.color.on_primary)
        maxTempLineDataSet.circleColors = listOf(
            ContextCompat.getColor(requireContext(), R.color.on_primary)
        )
        maxTempLineDataSet.fillColor = ContextCompat.getColor(requireContext(), R.color.on_primary)
        maxTempLineDataSet.color = ContextCompat.getColor(requireContext(), R.color.on_primary)

        maxTempLineDataSet.axisDependency = YAxis.AxisDependency.LEFT

        val formatter: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return dates[value.toInt()]
            }
        }
        val xAxis = binding.chart.xAxis
        xAxis.granularity = 1f // minimum axis-step (interval) is 1

        xAxis.valueFormatter = formatter
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.on_primary)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        val lineData = LineData(maxTempLineDataSet)
        lineData.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.on_primary))
        binding.chart.animateX(3000, Easing.EaseInSine)
        binding.chart.description.text = "Temperature changes"
        binding.chart.description.textColor =
            ContextCompat.getColor(requireContext(), R.color.on_primary)

        binding.chart.setNoDataText("")
        //binding.chart .textColor = resources.getColor(R.color.on_secondary)
        binding.chart.axisRight.isEnabled = false
        binding.chart.axisLeft.isEnabled = false
        binding.chart.setPinchZoom(true)

        binding.chart.data = lineData
        binding.chart.invalidate()

        binding.chart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        binding.chart.legend.form = Legend.LegendForm.CIRCLE
        binding.chart.legend.textColor = ContextCompat.getColor(requireContext(), R.color.on_primary)
    }

    private fun updateScreen(weatherData: WeatherData) {
        binding.cardCityName.text = weatherData.city.name?.uppercase()
        binding.temp.text = String.format(resources.getString(R.string.metric_temperature_units),
            weatherData.forecasts[0].main?.temp?.roundToInt())
        //"${weatherData.forecasts[0].main?.temp?.roundToInt()}°C"
        binding.weatherDescription.text = weatherData.forecasts[0].weather[0].description?.uppercase()
        binding.date.text = weatherData.forecasts[0].dtTxt.toString()
        binding.weatherIcon.setImageDrawable(getIconFromWeatherCode(weatherData.forecasts[0].weather[0].icon!!))

        binding.windValue.text = String.format(
            resources.getString(R.string.metric_wind_speed_units),
            weatherData.forecasts[0].wind?.speed
        )
        binding.pressureValue.text = String.format(
            resources.getString(R.string.pressure_units),
            weatherData.forecasts[0].main?.pressure)
        binding.humidityValue.text = String.format(
            resources.getString(R.string.humidity_units),
            weatherData.forecasts[0].main?.humidity
        )
    }

    private fun getIconFromWeatherCode(weatherCode: String) : Drawable? {
        return when (weatherCode) {
            "01d" -> ContextCompat.getDrawable(requireContext(), R.drawable.day_clear)
            "02d" -> ContextCompat.getDrawable(requireContext(), R.drawable.day_partial_cloud)
            "03d" -> ContextCompat.getDrawable(requireContext(), R.drawable.cloudy)
            "04d" -> ContextCompat.getDrawable(requireContext(), R.drawable.overcast)
            "09d" -> ContextCompat.getDrawable(requireContext(), R.drawable.rain)
            "10d" -> ContextCompat.getDrawable(requireContext(), R.drawable.day_rain)
            "11d" -> ContextCompat.getDrawable(requireContext(), R.drawable.day_rain_thunder)
            "13d" -> ContextCompat.getDrawable(requireContext(), R.drawable.day_snow)
            "50d" -> ContextCompat.getDrawable(requireContext(), R.drawable.mist)
            "01n" -> ContextCompat.getDrawable(requireContext(), R.drawable.night_full_moon_clear)
            "02n" -> ContextCompat.getDrawable(requireContext(), R.drawable.night_full_moon_partial_cloud)
            "03n" -> ContextCompat.getDrawable(requireContext(), R.drawable.cloudy)
            "04n" -> ContextCompat.getDrawable(requireContext(), R.drawable.overcast)
            "09n" -> ContextCompat.getDrawable(requireContext(), R.drawable.rain)
            "10n" -> ContextCompat.getDrawable(requireContext(), R.drawable.night_full_moon_rain)
            "11n" -> ContextCompat.getDrawable(requireContext(), R.drawable.night_full_moon_rain_thunder)
            "13n" -> ContextCompat.getDrawable(requireContext(), R.drawable.night_full_moon_snow)
            "50n" -> ContextCompat.getDrawable(requireContext(), R.drawable.mist)
            else -> ContextCompat.getDrawable(requireContext(), R.drawable.day_clear)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPlaceClick(place: Geocode) {
        viewModel.setCoordinates(place.lat, place.lon)
        viewModel.getFreshWeatherData()
    }
}

interface SearchListener {
    fun onPlaceClick(place: Geocode)
}