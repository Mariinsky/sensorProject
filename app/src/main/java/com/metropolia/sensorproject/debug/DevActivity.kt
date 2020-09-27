package com.metropolia.sensorproject.debug

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.internal.ContextUtils.getActivity
import com.metropolia.sensorproject.R
import com.metropolia.sensorproject.database.AppDB
import com.metropolia.sensorproject.database.DayActivity
import com.metropolia.sensorproject.models.ProgressViewModel
import kotlinx.android.synthetic.main.activity_dev.*
import kotlinx.android.synthetic.main.fragment_progress.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class DevActivity : AppCompatActivity() {

    private lateinit var viewModel: ProgressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev)
        val preferences = this.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val goal = preferences.getInt("GOAL", 0)
        val db = AppDB.get(this)

        viewModel = ViewModelProvider(this).get(ProgressViewModel::class.java)

        generate.setOnClickListener {
            val random = Random.nextInt(0, goal)
            viewModel.insertActivity(DayActivity(Date(), random))
        }

        get.setOnClickListener {
            viewModel.getAllActivities()
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val today = Date()
            val todayWithZeroTime = formatter.parse(formatter.format(today))
            Log.i("XXX", todayWithZeroTime.toString())
            /* val db = AppDB.get(activity!!)
             GlobalScope.launch {
                 val last = db.activityDao().getLimitedActivities(1)
                 Log.i("XXX", last.toString())
                 if (formatter.parse(formatter.format(last.date)) == todayWithZeroTime) {
                     Log.i("XXX", "true")
                 } else {
                     Log.i("XXX", "false")
                 }
             }*/
        }
        clear.setOnClickListener {
            GlobalScope.launch { db.clearAllTables()  }
        }
    }
}