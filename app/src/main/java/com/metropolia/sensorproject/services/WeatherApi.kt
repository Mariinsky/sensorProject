package com.metropolia.sensorproject.services



import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit

import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

import retrofit2.http.Query


class WeatherApi() {
    private val baseUrl= "https://api.openweathermap.org/data/2.5/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    interface Service {
        @GET("onecall?units=metric&exclude=minutely,hourly,daily,alerts")
        fun fetchWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("appid") appid: String
        ): Observable<Weather>
    }

    val service: Service = retrofit.create(Service::class.java)

}

data class Weather(
    val lat: Float,
    val lon: Float,
    val timezone: String,
    val current: Current
) {
    val curentTemp: String
        get() { return "${current.temp} °C"}

    val feelsLike: String
        get() { return "Feels like ${current.feels_like} °C"}

    val windSpeed: String
        get() { return "${current.wind_speed} m/s"}

    val humidity: String
        get() { return "${current.humidity} %"}

    val description: String
        get() { return current.weather[0].description  }

    val icon: String
        get() { return "https://openweathermap.org/img/wn/${current.weather[0].icon}@4x.png"}
}

data class Current (
    val temp: Float,
    val feels_like: Float,
    val pressure: Int,
    val humidity: Int,
    val wind_speed: Float,
    val wind_deg: Int,
    val weather: List<WeatherDescription>
)

data class WeatherDescription (
    val main: String,
    val description: String,
    val icon: String
)