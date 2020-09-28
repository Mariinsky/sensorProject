package com.metropolia.sensorproject.debug

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.internal.ContextUtils.getActivity
import com.metropolia.sensorproject.R
import com.metropolia.sensorproject.WEATHER_API_KEY
import com.metropolia.sensorproject.database.AppDB
import com.metropolia.sensorproject.database.DayActivity
import com.metropolia.sensorproject.models.ProgressViewModel
import com.metropolia.sensorproject.services.WeatherApi
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers.io
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_dev.*
import kotlinx.android.synthetic.main.fragment_progress.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class DevActivity : AppCompatActivity() {

    private lateinit var viewModel: ProgressViewModel
    private val subject: PublishSubject<Unit> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev)
        val preferences = this.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val goal = preferences.getInt("GOAL", 0)
        val db = AppDB.get(this)

        viewModel = ViewModelProvider(this).get(ProgressViewModel::class.java)

        subject
            .observeOn(io())
            .flatMap {
                val api = WeatherApi().service
                api.fetchWeather(60.249523,24.904677, WEATHER_API_KEY)
            }.subscribe {
                Log.i("XXX", it.toString())
            }

        generate.setOnClickListener {
            val random = Random.nextInt(0, goal)
            viewModel.insertActivity(DayActivity(Date(), random))
        }
        get.text = "test weather"
        get.setOnClickListener {
            subject.onNext(Unit)

        }
        clear.setOnClickListener {
            GlobalScope.launch { db.clearAllTables()  }
        }
    }
}