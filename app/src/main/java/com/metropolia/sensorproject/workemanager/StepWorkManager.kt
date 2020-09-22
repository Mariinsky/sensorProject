package com.metropolia.sensorproject.workemanager

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.metropolia.sensorproject.sensors.SensorService
import com.metropolia.sensorproject.sensors.Steps
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers.io
import java.util.concurrent.TimeUnit


class StepWorkManager(private val context: Context, params: WorkerParameters): Worker(context, params) {

    override fun doWork(): Result {
        Log.i("XXX", "WORK STARTED")
        val sen = SensorService(context.getSystemService(SENSOR_SERVICE) as SensorManager)
        sen.registerListener()
        Observable.interval(5, TimeUnit.SECONDS)
            .timeInterval()
            .observeOn(io())
            .subscribe {
                if (Steps.steps != 0) {
                    context.openFileOutput("steps2.txt", Context.MODE_PRIVATE).use {
                        Log.i("XXX", "writing to file ${Steps.steps}")
                        it?.write(Steps.steps.toString().toByteArray())
                    }
                    Log.i("XXX", "writing to file done")
                }
            }
        return Result.success()
    }

}