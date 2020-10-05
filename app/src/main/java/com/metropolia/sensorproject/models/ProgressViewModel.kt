package com.metropolia.sensorproject.models

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import android.location.Location

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.metropolia.sensorproject.DAY_VALUES_FILE

import com.metropolia.sensorproject.StepApp

import com.metropolia.sensorproject.database.AppDB
import com.metropolia.sensorproject.database.DayActivity
import com.metropolia.sensorproject.services.LocationService
import com.metropolia.sensorproject.services.StepSensorService
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers.io
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
import org.osmdroid.util.GeoPoint
import java.util.*
import java.util.concurrent.TimeUnit


class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val db by lazy { AppDB.get(application) }
    private val getAllActivities: PublishSubject<Unit> = PublishSubject.create()
    private val insertNewActivity: PublishSubject<DayActivity> = PublishSubject.create()
    private val getLimitedActivities: PublishSubject<Int> = PublishSubject.create()
    private val locationService = LocationService(application)
    private val stepSensor = StepSensorService(application.getSystemService(Context.SENSOR_SERVICE) as SensorManager)

    private var valuesWriter: Disposable? = null

    val allActivitiesSubject: PublishSubject<List<DayActivity>> = PublishSubject.create()
    val limitedActivitiesSubject: PublishSubject<Pair<MutableList<Int>, List<DayActivity>>> = PublishSubject.create()

    val context = getApplication<Application>().applicationContext
    val emitStepsCount: PublishSubject<Int> = PublishSubject.create()
    val startingLocationSubject: ReplaySubject<Location> = ReplaySubject.create()

    val locationServiceSubject: PublishSubject<Pair<String, MutableList<GeoPoint>>> = PublishSubject.create()
    val startServices = PublishSubject.create<Unit>()
    val stopServices = PublishSubject.create<Unit>()


    val gson = Gson()

    init {
        StepApp
            .stepStream
            .subscribe {
                emitStepsCount.onNext(it)
            }

        StepApp
            .locationServiceStream
            .subscribe {(distance, route) ->
                locationServiceSubject.onNext(Pair(distance, route))
            }

        startServices
            .subscribe {
                locationService.startGettingLocation()
                stepSensor.registerListener()
                startStepWriter()
            }

        stopServices
            .subscribe {
                locationService.stopLocationService()
                stepSensor.unregisterListener()
                disposeWriter()
            }


        StepApp
            .locationStream
            .take(1)
            .subscribe {
                startingLocationSubject.onNext(it)
            }

        getAllActivities
            .observeOn(io())
            .subscribe {
                val activites = db.activityDao().getAll()
                allActivitiesSubject.onNext(activites)
            }

        insertNewActivity
            .observeOn(io())
            .subscribe { db.activityDao().insert(it) }

        getLimitedActivities
            .observeOn(io())
            .subscribe {
                val query = db.activityDao().getLimitedActivities(it)
                val steps = mutableListOf<Int>()
                query.forEach { day ->
                     steps.add(day.Steps)
                }
                limitedActivitiesSubject.onNext(Pair(steps, query))
            }

    }

    fun getAllActivities() {
        getAllActivities.onNext(Unit)
    }

    fun insertActivity(activity: DayActivity) {
        insertNewActivity.onNext(activity)
    }

    fun getLimitedActivities(limit: Int) {
        getLimitedActivities.onNext(limit)
    }

    fun startStepWriter() {
        valuesWriter = Observable
            .interval(5, TimeUnit.SECONDS)
            .timeInterval()
            .observeOn(io())
            .subscribe {
                val day = DayActivity(
                    Date(),
                    StepApp.getStepCount(),
                    StepApp.getTimer(),
                    StepApp.getWeather(),
                    StepApp.getRoute(),
                    StepApp.getDistance()
                )
                val json = gson.toJson(day)
                    context.openFileOutput(DAY_VALUES_FILE, Context.MODE_PRIVATE).use {
                        Log.i("XXX", "writing values")
                        it?.write(json.toByteArray())
                    }
            }
    }
    fun disposeWriter() {
        valuesWriter?.dispose()
    }
}