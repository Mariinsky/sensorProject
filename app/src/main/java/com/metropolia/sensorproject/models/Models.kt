package com.metropolia.sensorproject.models

/**
 *  Weather data class for object from openweathermap.org
 *  custom getters.
 * */
data class Weather(
    val lat: Float,
    val lon: Float,
    val timezone: String,
    val current: Current,
    val daily: List<DayDescription>
) {
    val currentTemp: String
        get() { return "${current.temp} °C"}

    val feelsLike: String
        get() { return "Feels like ${current.feels_like} °C"}

    val windSpeed: String
        get() { return "${current.wind_speed} m/s"}

    val windDirection: Int
        get() { return current.wind_deg + 180}

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

data class DayDescription (
    val temp: Temp,
    val weather: List<WeatherDescription>
){
    val dayTemp: String
        get() { return "${temp.day} °C"}
}

data class Temp (
    val day: Float,
    val night: Float
)