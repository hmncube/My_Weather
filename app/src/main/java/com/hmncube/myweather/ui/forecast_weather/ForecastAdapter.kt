package com.hmncube.myweather.ui.forecast_weather

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.card.MaterialCardView
import com.hmncube.myweather.R
import javax.inject.Inject

class ForecastAdapter @Inject constructor(private val listener: ForecastClickListener) :
    RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    private var dataSet = mutableListOf<DailyForecastData>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout : MaterialCardView
        val day : TextView
        val icon : LottieAnimationView
        val desc : TextView
        //val maxTemp : TextView
        val maxTemp : TextView

        init {
            layout = view.findViewById(R.id.forecastLayout)
            day = view.findViewById(R.id.forecastDay)
            icon = view.findViewById(R.id.forecastIcon)
            desc = view.findViewById(R.id.forecastDesc)
            maxTemp = view.findViewById(R.id.forecastMaxTemp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.forecast_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.day.text = DateFormat.format("E", dataSet[position].date)
        holder.icon.setAnimation(dataSet[position].weatherIcon)
        holder.desc.text = dataSet[position].weatherDescription
        holder.maxTemp.text = String.format(
            holder.itemView.context.resources.getString(R.string.metric_temperature_units),
            dataSet[position].maxTemperature
        )
        holder.layout.setOnClickListener {
            listener.onClick(dataSet[position])
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setData(data : MutableList<DailyForecastData>) {
        dataSet = data
        notifyDataSetChanged()
    }
}
