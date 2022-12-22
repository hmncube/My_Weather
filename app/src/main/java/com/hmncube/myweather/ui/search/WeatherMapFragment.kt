package com.hmncube.myweather.ui.search

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.hmncube.myweather.BuildConfig
import com.hmncube.myweather.databinding.FragmentWeatherMapBinding
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.overlay.TilesOverlay


@AndroidEntryPoint
class WeatherMapFragment : Fragment() {

    companion object {
        fun newInstance() = WeatherMapFragment()
    }

    private lateinit var viewModel: WeatherMapViewModel
    private var _binding : FragmentWeatherMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WeatherMapViewModel::class.java)

        getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        binding.map.setTileSource(TileSourceFactory.MAPNIK)

        val mapController = binding.map.controller
        mapController.setZoom(10.5)
        val lat = -17.83
        val long = 31.05
        val startPoint = GeoPoint(lat, long)
        mapController.setCenter(startPoint)
        val apiKey = BuildConfig.OpenWeatherAPi

        val provider = MapTileProviderBasic(
            activity,
            object : OnlineTileSourceBase("Open Weather Map", 0, 15,
                256, "PNG", arrayOfNulls(0)) {
                override fun getTileURLString(pMapTileIndex: Long): String {
                    val x = MapTileIndex.getX(pMapTileIndex)
                    val y = MapTileIndex.getY(pMapTileIndex)
                    val z = MapTileIndex.getZoom(pMapTileIndex)
                    return "https://tile.openweathermap.org/map/temp_new/${z}/${x}/${y}.png?appid=$apiKey"
                }
            })
        //todo maybe do this after 3 hours or when the weather details are refreshed
        provider.clearTileCache()
        val layer = TilesOverlay(provider, requireContext())
        layer.loadingBackgroundColor = Color.TRANSPARENT
        layer.loadingLineColor = Color.TRANSPARENT

        binding.map.overlays.add(layer)
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}