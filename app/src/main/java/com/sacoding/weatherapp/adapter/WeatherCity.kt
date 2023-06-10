package com.sacoding.weatherapp.adapter

data class WeatherCity(
    val cityName:String,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
//    val temp_max: Double,
//    val temp_min: Double,
    val sunrise: Int,
    val sunset: Int,
    val country: String,
    val speed: Double,
    val dt: Int,
    val weatherType:String
)
