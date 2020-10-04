package com.metropolia.sensorproject


import android.location.Location
import android.os.SystemClock
import android.widget.Chronometer
import com.google.gson.Gson
import com.metropolia.sensorproject.database.DayActivity
import com.metropolia.sensorproject.services.Weather
import com.metropolia.sensorproject.services.WeatherApi
import io.reactivex.rxjava3.kotlin.withLatestFrom
import io.reactivex.rxjava3.schedulers.Schedulers.io
import io.reactivex.rxjava3.subjects.PublishSubject
import org.osmdroid.util.GeoPoint


object StepApp {
    private var stepCount = 0
    private var route = mutableListOf<GeoPoint>()
    val dayActivity = DayActivity()
    private val api = WeatherApi().service
    val stepCountSubject: PublishSubject<Int> = PublishSubject.create()
    val locationSubject: PublishSubject<Location> = PublishSubject.create()
    val weatherSubject: PublishSubject<Weather> = PublishSubject.create()
    var chronometer: Chronometer? = null
    private val getCurrentWeather: PublishSubject<Unit> = PublishSubject.create()

    init {
        getCurrentWeather
            .withLatestFrom(locationSubject)
            .observeOn(io())
            .flatMap { (_, location) ->
                api.fetchWeather(location.latitude, location.longitude, WEATHER_API_KEY)
            }
            .subscribe {
                dayActivity.weather = Gson().toJson(it)
                weatherSubject.onNext(it)
            }
    }

    fun updateStepCount(steps: Int? = null) {
        if (steps == null ) { stepCount ++ } else { stepCount = steps }
        dayActivity.Steps = stepCount
        //dayActivity.timer = SystemClock.elapsedRealtime() - chronometer?.base!!
        stepCountSubject.onNext(stepCount)
    }

    fun updateRoute(geoPoint: GeoPoint) {
        route.add(geoPoint)
    }

    fun getWeather() { getCurrentWeather.onNext(Unit) }

    fun getStepCount(): Int { return stepCount }

    fun setChronoListener() {
        chronometer?.onChronometerTickListener
    }

    fun setChronoBase(start: Long) {
        chronometer?.base = SystemClock.elapsedRealtime() - start
    }

}

const val ZERO_STEPS = 0
const val FILE_STEPS = "steps.txt"
const val FILE_ROUTE = "route.txt"
const val TIME_FILE = "time.txt"
const val DAY_VALUES_FILE = "values.txt"
const val WEATHER_API_URL= "https://api.openweathermap.org/data/2.5/"