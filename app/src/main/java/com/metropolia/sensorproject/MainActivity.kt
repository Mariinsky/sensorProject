package com.metropolia.sensorproject

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.metropolia.sensorproject.sensors.Steps
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    var isLogedIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences= getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        isLogedIn = sharedPreferences.getBoolean("CHECKBOX", false)
        if(isLogedIn){
            val intent = Intent(this, StepTrackerActivity::class.java)
            startActivity(intent)
            finish()
        }
        inputCheck()
        btnSave.setOnClickListener{
            val name: String = editTxtName.text.toString()
            val weight: Int = editTxtWeight.text.toString().toInt()
            val height: Int = editTxtHeight.text.toString().toInt()
            val goal: Int = editTxtGoal.text.toString().toInt()
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("NAME", name)
            editor.putInt("WEIGHT", weight)
            editor.putInt("HEIGHT", height)
            editor.putInt("GOAL", goal)
            editor.putBoolean("CHECKBOX", true)
            editor.apply()

            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()
            val intent = Intent(this, StepTrackerActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun inputCheck() {
        //check length of name input
        editTxtName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > layoutEditName.counterMaxLength) {
                    Log.d("main","$count")
                    layoutEditName.error = getString(R.string.name_input_error)
                } else {
                    layoutEditName.isErrorEnabled = false
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        // PLACEHOLDER STUFF
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(this as Activity, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 100)
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            Log.i("XXX", "reading file")
            try {
                val reader = openFileInput("steps2.txt")?.bufferedReader().use { it?.readText() ?: "-1" }
                Log.i("XXX", "Reading value $reader")
                Steps.steps = reader.toInt()
            } catch (e: Exception) {
                Log.i("XXX", "error" + e.message.toString())
            }
            Log.i("XXX", "reading file done")
        }

        placeholderbutton.setOnClickListener {
            startActivity(Intent(this, StepCounterActivity::class.java))
        }
    }
}

