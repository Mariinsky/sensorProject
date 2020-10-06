package com.metropolia.sensorproject.services

import com.metropolia.sensorproject.WEATHER_API_URL
import com.metropolia.sensorproject.models.Weather
import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *  Weather api for getting weather from https://openweathermap.org/
 *  uses retrofit
 * */
class WeatherApi {

    private val retrofit = Retrofit.Builder()
        .baseUrl(WEATHER_API_URL)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    interface Service {

        /**
         *  Gets the current weather and 8 days prediction
         *  @return Observable
         * */
        @GET("onecall?units=metric&exclude=minutely,hourly,alerts")
        fun fetchWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("appid") appid: String
        ): Observable<Weather>
    }

    val service: Service = retrofit.create(Service::class.java)
}

