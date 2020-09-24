package com.metropolia.sensorproject.workmanager

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.metropolia.sensorproject.services.SensorService
import com.metropolia.sensorproject.services.DataStreams
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers.io
import java.util.concurrent.TimeUnit


class StepWorkManager(private val context: Context, params: WorkerParameters): Worker(context, params) {

    override fun doWork(): Result {
        Log.i("XXX", "WORK STARTED")
        val sen = SensorService(context.getSystemService(SENSOR_SERVICE) as SensorManager)
        sen.registerListener()
        Observable
            .interval(5, TimeUnit.SECONDS)
            .timeInterval()
            .observeOn(io())
            .subscribe {
                if (DataStreams.getStepCount() != 0) {
                    context.openFileOutput("steps2.txt", Context.MODE_PRIVATE).use {
                        it?.write(DataStreams.getStepCount().toString().toByteArray())
                    }
                }
            }
        return Result.success()
    }

}