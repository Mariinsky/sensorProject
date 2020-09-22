package com.metropolia.sensorproject

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_step_tracker.*

class StepTrackerActivity : AppCompatActivity() {
    lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_tracker)

        preferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val name = preferences.getString("NAME","")
        txtWelcome.text = name
    }
}