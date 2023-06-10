package com.sacoding.weatherapp.ui.viewModel

import android.app.Application
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.ContactsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacoding.weatherapp.adapter.WeatherCity
import com.sacoding.weatherapp.api.models.Weather
import com.sacoding.weatherapp.location.LocationTracker
import com.sacoding.weatherapp.repository.WeatherRepository
import com.sacoding.weatherapp.util.Constants
import com.sacoding.weatherapp.util.Resource
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.internal.Intrinsics.Kotlin

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val application:Application,
    private val locationTracker: LocationTracker
) :ViewModel(){

    val weatherState:MutableLiveData<Resource<Weather>> = MutableLiveData()
    val weatherStateCity:MutableLiveData<Resource<List<WeatherCity>>> = MutableLiveData()

    fun getWeatherOfCities(cityName:String){
        viewModelScope.launch {
            if(hasInternetConnection()){
                weatherStateCity.postValue(Resource.Loading())
                val cities = listOf("new york", "singapore", "mumbai", "delhi", "sydney", "melbourne")
                val weatherList = mutableListOf<WeatherCity>()
                cities.forEach {city->
                    val response = repository.getWeatherOfCities(city)
                    response.collectLatest {
                        when(it){
                            is Resource.Error ->{
                                weatherStateCity.postValue(Resource.Error(it.message!!))
                            }
                            is Resource.Loading -> {
                                 weatherStateCity.postValue(Resource.Loading())
                            }
                            is Resource.Success -> {
                               it.data?.let {
                                   val weather = WeatherCity(cityName = city,it.main.humidity,it.main.pressure,
                                   it.main.temp,it.sys.sunrise,
                                   it.sys.sunset,it.sys.country,it.wind.speed,it.dt,it.weather[0].description)
                                   weatherList.add(weather)
                               }

                            }
                        }
                    }
                }

                weatherStateCity.postValue(Resource.Success(weatherList.toList()))



            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getWeather(
    ){
        viewModelScope.launch {

            if (hasInternetConnection()){

            locationTracker.getCurrentLocation()?.let {

               weatherState.postValue(Resource.Loading())

                val response = repository.getWeather(
                    it.latitude, it.longitude
                )
                response.collectLatest { result ->
                    when (result) {
                        is Resource.Error -> {
                            weatherState.postValue(Resource.Error(result.message!!))
                        }

                        is Resource.Loading -> {
                            weatherState.postValue(Resource.Loading())
                        }

                        is Resource.Success -> {
                            weatherState.postValue(Resource.Success(result.data))
                        }
                    }

                }
            }?:kotlin.run {
                weatherState.postValue(Resource.Error("Location retrieve failed"))
            }
        }else{
            weatherState.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        }
    }



    private fun hasInternetConnection(): Boolean {

        val connectivityManager = Contexts.getApplication(application).getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ContactsContract.CommonDataKinds.Email.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}