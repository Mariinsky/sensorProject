package com.metropolia.sensorproject.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.metropolia.sensorproject.database.AppDB
import com.metropolia.sensorproject.database.DayActivity
import com.metropolia.sensorproject.services.DataStreams
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.schedulers.Schedulers.io
import io.reactivex.rxjava3.subjects.PublishSubject

class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val db by lazy { AppDB.get(application) }
    private val getAllActivities: PublishSubject<Unit> = PublishSubject.create()
    private val insertNewActivity: PublishSubject<DayActivity> = PublishSubject.create()
    private val getLimitedActivities: PublishSubject<Int> = PublishSubject.create()

    val allActivitiesSubject: PublishSubject<List<DayActivity>> = PublishSubject.create()
    val limitedActivitiesSubject: PublishSubject<Pair<MutableList<Int>, List<DayActivity>>> = PublishSubject.create()

    val context = getApplication<Application>().applicationContext
    val emitStepsCount: PublishSubject<Int> = PublishSubject.create()
    var stepCount = DataStreams.getStepCount()
    init {
        DataStreams
            .stepCountSubject
            .subscribe {
                stepCount = it
                emitStepsCount.onNext(it)
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
}