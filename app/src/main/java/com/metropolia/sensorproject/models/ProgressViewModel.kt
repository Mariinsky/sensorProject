package com.metropolia.sensorproject.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.metropolia.sensorproject.services.DataStreams
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.subjects.PublishSubject

class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    val emitStepsCount: PublishSubject<Int> = PublishSubject.create()
    private val getStepCount =
        DataStreams
            .stepCountSubject
            .subscribe {
                emitStepsCount.onNext(it)
            }

    private val getData =
        Observables
            .combineLatest(DataStreams.stepCountSubject, DataStreams.locationSubject)
            .share()

}