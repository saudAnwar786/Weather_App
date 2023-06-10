package com.sacoding.weatherapp.repository

import android.util.Log
import com.sacoding.weatherapp.api.WeatherApi
import com.sacoding.weatherapp.api.models.Weather
import com.sacoding.weatherapp.util.Resource
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api:WeatherApi
        ){

    suspend fun getWeather(
        lat:Double,long:Double
        ) = flow{

        emit(Resource.Loading())
        val response =
        try {
            api.getWeather(
                lat,long
            )
        }catch (e:IOException){
            Log.d("Repository","IO exception")
            emit(Resource.Error(e.message!!))
            return@flow
        }catch (e:HttpException){
            Log.d("Repository","Http exception")
            emit(Resource.Error(e.message()))
            return@flow
        }

        emit(Resource.Success(response))
    }

    suspend fun getWeatherOfCities(
        cityName:String
    ) = flow{

        emit(Resource.Loading())
        val response =
            try {
                api.getWeatherOfCities(
                    cityName
                )
            }catch (e:IOException){
                Log.d("Repository","IO exception")
                emit(Resource.Error(e.message!!))
                return@flow
            }catch (e:HttpException){
                Log.d("Repository","Http exception")
                emit(Resource.Error(e.message()))
                return@flow
            }

        emit(Resource.Success(response))
    }

}