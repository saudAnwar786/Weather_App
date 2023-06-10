package com.sacoding.weatherapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sacoding.weatherapp.databinding.ItemWeathercityBinding

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherCityAdapter:RecyclerView.Adapter<WeatherCityAdapter.WeatherCityViewHolder>() {

    inner class WeatherCityViewHolder(val binding:ItemWeathercityBinding):RecyclerView.ViewHolder(binding.root)


    val differCallBack = object :DiffUtil.ItemCallback<WeatherCity>(){
        override fun areItemsTheSame(oldItem: WeatherCity, newItem: WeatherCity): Boolean {
            return oldItem.cityName == newItem.cityName
        }

        override fun areContentsTheSame(oldItem: WeatherCity, newItem: WeatherCity): Boolean {
            return newItem.cityName == oldItem.cityName
        }
    }
    val differ = AsyncListDiffer(this,differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherCityViewHolder {
        return WeatherCityViewHolder(
            ItemWeathercityBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: WeatherCityViewHolder, position: Int) {
        val curItem = differ.currentList[position]
        holder.binding.apply {
            temperature.text = "${curItem.temp } K"
            humidity.text = "Humidity : ${curItem.humidity} %"
            windSpeed.text  = "Wind Speed : ${curItem.speed} km/h"
            weatherType.text = "Weather Type : ${curItem.weatherType}"
            date.text = "Date : ${timestampToDateAndTime(curItem.dt.toLong())}"
            sunrise.text = "Sunrise : ${timestampToDateAndTime(curItem.sunrise.toLong())}"
            sunset.text = "Sunset : ${timestampToDateAndTime(curItem.sunset.toLong())}"
            cityName.text = curItem.cityName.capitalize()

        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    private fun timestampToDateAndTime(ms:Long) : String{
        // Assuming you have a timestamp in milliseconds

        val date = Date(ms*1000L)

        val sdf = SimpleDateFormat("yyyy-MM-dd | HH:mm a", Locale.getDefault())

        return sdf.format(date)
    }
}