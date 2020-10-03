package com.metropolia.sensorproject

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Chronometer
import com.metropolia.sensorproject.services.Weather
import com.metropolia.sensorproject.services.WeatherApi
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.withLatestFrom
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.Schedulers.io
import io.reactivex.rxjava3.subjects.PublishSubject
import org.osmdroid.util.GeoPoint
import java.util.concurrent.TimeUnit

object StepApp {
    private var stepCount = 0
    private var route = mutableListOf<GeoPoint>()
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
                weatherSubject.onNext(it)
            }

    }

    fun updateStepCount(steps: Int? = null) {
        if (steps == null ) { stepCount ++ } else { stepCount = steps }
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

}

const val ZERO_STEPS = 0
const val FILE_STEPS = "steps.txt"
const val FILE_ROUTE = "route.txt"