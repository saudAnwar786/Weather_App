package com.sacoding.weatherapp.ui

import android.Manifest
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sacoding.weatherapp.adapter.WeatherCityAdapter
import com.sacoding.weatherapp.databinding.ActivityMainBinding
import com.sacoding.weatherapp.ui.viewModel.MainViewModel
import com.sacoding.weatherapp.util.Resource

import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding :ActivityMainBinding
    private val viewModel:MainViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var weatherAdapter: WeatherCityAdapter
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            viewModel.getWeather()
            val cities = listOf("Delhi","Melbo","","","","")
            viewModel.getWeatherOfCities("Delhi")

        }
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ))
        getDataFromSharedpref()


        viewModel.weatherState.observe(this, Observer{
            when(it){
                is Resource.Error -> {

                    Snackbar.make(binding.root,it.message.toString(),Snackbar.LENGTH_SHORT).show()
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility= View.VISIBLE
                    //Log.d("MainActivity","Loading....")
                    getDataFromSharedpref()

                }
                is Resource.Success -> {
                    binding.progressBar.visibility= View.GONE
                     it.data?.let {
                         binding.temperature.text = "${it.main.temp } K"
                         binding.windSpeed.text = "Wind speed : ${it.wind.speed} km/h"
                         binding.humidity.text = "Humidity : ${it.main.humidity}%"
                         binding.cityName.text = it.sys.country
                         binding.maxTemp.text= "Max Temp : ${it.main.temp_max } K"
                         binding.minTemp.text = "Min Temp : ${it.main.temp_min } K"
                         binding.sunrise.text = "Sunrise : ${timestampToDateAndTime(it.sys.sunrise.toLong())} "
                         binding.sunset.text = "Sunset : ${timestampToDateAndTime(it.sys.sunset.toLong())}"
                         binding.weatherType.text = "Weather type: ${it.weather[0].description}"
                         binding.date.text = timestampToDateAndTime(it.dt.toLong())
                     }
                    writeDataToSharedPref()

                }

                else -> {}
            }
        })

         setRecyclerView()
        viewModel.weatherStateCity.observe(this, Observer {
            when(it){
                is Resource.Error -> {
                    Snackbar.make(binding.root,it.message.toString(),Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                   Log.d("Main","load")
                }
                is Resource.Success -> {
                      weatherAdapter.differ.submitList(it.data)
                }
            }
        })
    }
    private fun timestampToDateAndTime(ms:Long) : String{
        // Assuming you have a timestamp in milliseconds

        val date = Date(ms*1000L)

        val sdf = SimpleDateFormat("yyyy-MM-dd | HH:mm a", Locale.getDefault())

        return sdf.format(date)
    }

   private fun writeDataToSharedPref(){
       binding.apply {
           sharedPreferences.edit()
               .putString("temp",temperature.text.toString())
               .putString("ws",windSpeed.text.toString())
               .putString("hum",humidity.text.toString())
               .putString("cntry",cityName.text.toString())
               .putString("maxTemp",maxTemp.text.toString())
               .putString("minTemp",minTemp.text.toString())
               .putString("sunrise",sunrise.text.toString())
               .putString("sunset",sunset.text.toString())
               .putString("weather",weatherType.text.toString())
               .putString("dt",date.text.toString())
               .apply()
       }

   }
    private fun getDataFromSharedpref(){
        binding.temperature.text=  sharedPreferences.getString("temp","Temperature")
        binding.windSpeed.text=sharedPreferences.getString("ws","Wind Speed")
        binding.humidity.text = sharedPreferences.getString("hum","Humidity")
        binding.cityName.text = sharedPreferences.getString("cntry","Country")
        binding.maxTemp.text= sharedPreferences.getString("maxTemp","Max Temperature")
        binding.minTemp.text = sharedPreferences.getString("minTemp","Min Temperature")
        binding.sunrise.text = sharedPreferences.getString("sunrise","Sunrise")
        binding.sunset.text = sharedPreferences.getString("sunset","Sunset")
        binding.weatherType.text = sharedPreferences.getString("weather","Weather Condition")
        binding.date.text = sharedPreferences.getString("dt","Date ")
    }
    private fun setRecyclerView() {
        weatherAdapter = WeatherCityAdapter()
        binding.recyclerView.apply {
            adapter = weatherAdapter
            layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
        }
    }

}