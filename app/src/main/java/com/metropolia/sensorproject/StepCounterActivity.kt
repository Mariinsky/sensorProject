package com.metropolia.sensorproject


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.metropolia.sensorproject.services.DataStreams
import com.metropolia.sensorproject.workmanager.StepWorkManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_step_counter.*
import java.util.concurrent.TimeUnit

class StepCounterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_counter)
        
        // just placeholder stuff to check that step counter works.
        val stepWorker: WorkRequest = OneTimeWorkRequestBuilder<StepWorkManager>().build()
        WorkManager.getInstance(application).enqueue(stepWorker)

        Observable.interval(1, TimeUnit.SECONDS)
            .timeInterval()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                steps_text.text = DataStreams.steps.toString()
            }
    }
}