package com.hmncube.myweather.ui.dialogs.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hmncube.myweather.R
import com.hmncube.myweather.data.remote.models.Geocode
import javax.inject.Inject

class SearchAdapter @Inject constructor(private val listener: AdapterClickListener) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

        private var dataSet = mutableListOf<Geocode>()

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val layout : CardView
            val place : TextView
            val state : TextView
            val country : TextView

            init {
                layout = view.findViewById(R.id.searchResultLayout)
                place = view.findViewById(R.id.placeName)
                state = view.findViewById(R.id.stateName)
                country = view.findViewById(R.id.countryName)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("pundez", "onBindViewHolder: $position")
        holder.place.text = dataSet[position].name
        holder.state.text = dataSet[position].state
        holder.country.text = dataSet[position].country
        holder.layout.setOnClickListener {
            listener.onClick(dataSet[position])
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setData(data : MutableList<Geocode>) {
        Log.d("pundez", "setData: ${data.size}")
        dataSet = data
        notifyDataSetChanged()
    }
}