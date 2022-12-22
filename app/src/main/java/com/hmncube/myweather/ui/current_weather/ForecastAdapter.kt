package com.hmncube.myweather.ui.current_weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hmncube.myweather.R
import javax.inject.Inject

class ForecastAdapter @Inject constructor() :
    RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

        private var dataSet = mutableListOf<ForecastData>()

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val weatherIcon : ImageView
            val date : TextView
            val temperature : TextView

            init {
                weatherIcon = view.findViewById(R.id.cardWeatherIcon)
                date = view.findViewById(R.id.cardDate)
                temperature = view.findViewById(R.id.cardTemp)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.forecast_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.weatherIcon.setImageResource(dataSet[position].icon)
        holder.date.text = dataSet[position].date
        holder.temperature.text = String.format(
            holder.itemView.context.resources.getString(R.string.metric_temperature_units),
            dataSet[position].temp
        )
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setData(data : MutableList<ForecastData>) {
        dataSet = data
        notifyDataSetChanged()
    }
}