package com.sacoding.weatherapp.api

import com.sacoding.weatherapp.api.models.Weather
import com.sacoding.weatherapp.util.Constants.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") latitude:Double ,
        @Query("lon") longitude:Double,
        @Query("APPID") apiKey:String = API_KEY
    ):Weather
    @GET("/data/2.5/weather")
    suspend fun getWeatherOfCities(
        @Query("q") cityName: String ,
        @Query("APPID") apiKey:String = API_KEY
    ):Weather


}