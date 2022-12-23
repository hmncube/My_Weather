package com.hmncube.myweather.ui.dialogs.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hmncube.myweather.R
import com.hmncube.myweather.data.remote.models.Geocode
import com.hmncube.myweather.databinding.FragmenSearchDialogBinding
import com.hmncube.myweather.ui.current_weather.CurrentWeatherViewModel
import com.hmncube.myweather.ui.current_weather.SearchListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchDialogFragment(private val listener : SearchListener) : BottomSheetDialogFragment(), View.OnClickListener, AdapterClickListener {

    private lateinit var _binding : FragmenSearchDialogBinding
    private val binding  get() = _binding
    lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmenSearchDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrentWeatherViewModel::class.java)
        showEmptyView(true, "Search for the place!")

        binding.searchTerm.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //ignore
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkEnablingSearchButton()
            }

            override fun afterTextChanged(p0: Editable?) {
                //
            }
        })

        binding.dialogSearchBtn.setOnClickListener(this)
        binding.resultsRv.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SearchAdapter(this)
        binding.resultsRv.adapter = adapter
        viewModel.geoCodeResults.observe(requireActivity()) { geoCodes ->
            adapter.setData(geoCodes.toMutableList())
            showEmptyView(adapter.itemCount == 0, "No results found!")
        }

        viewModel.searchLoading.observe(requireActivity()) { loading ->
            if (loading) {
                showEmptyView(false)
                binding.containerAnimationView.visibility = View.VISIBLE
                binding.resultsRv.visibility = View.GONE
            } else {
                binding.containerAnimationView.visibility = View.GONE
                binding.resultsRv.visibility = View.VISIBLE
            }
        }

        viewModel.snackBar.observe(requireActivity()) {msg ->
            msg?.let { showEmptyView(true, it) }
        }
    }

    private fun showEmptyView(isEmpty: Boolean, msg: String = "") {
        if (isEmpty) {
            binding.resultsRv.visibility = View.GONE
            binding.emptyAnimationView.visibility = View.VISIBLE
            binding.emptyMessage.text = msg
        } else {
            binding.resultsRv.visibility = View.VISIBLE
            binding.emptyAnimationView.visibility = View.GONE
        }
    }

    private fun checkEnablingSearchButton() {
        binding.dialogSearchBtn.isEnabled = binding.searchTerm.text?.trim()?.isNotEmpty()!!
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.dialogSearchBtn) {
            hideKeyBoard()
            viewModel.getCoodForPlace(binding.searchTerm.text.toString())
        }
    }

    private fun hideKeyBoard() {
        if (view != null) {
            val manager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    override fun onClick(selected: Geocode) {
        listener.onPlaceClick(selected)
        dismiss()
    }
}

interface AdapterClickListener {
    fun onClick(selected : Geocode)
}