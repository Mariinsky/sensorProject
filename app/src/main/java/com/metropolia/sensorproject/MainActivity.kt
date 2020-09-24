package com.metropolia.sensorproject


import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ActivityCompat
import com.metropolia.sensorproject.services.DataStreams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var sharedPreferences: SharedPreferences
    private val REQUEST_ALL_NEEDED_PERMISSIONS = 999
    private val permissions =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
        )

    var isLogedIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
        readStepFromFile()
        sharedPreferences= getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        isLogedIn = sharedPreferences.getBoolean("CHECKBOX", false)
        if(isLogedIn){
            val intent = Intent(this, StepTrackerActivity::class.java)
            startActivity(intent)
            finish()
        }
        inputCheck()
        btnSave.setOnClickListener(this)

    }

    private fun validate(): Boolean {
        if(editTxtName.text.toString().isEmpty()){
            layoutEditName.error = getString(R.string.input_empty)
            return false
        } else if (editTxtWeight.text.toString().isEmpty()){
            layoutEditWeight.error = getString(R.string.input_empty)
            return false
        }else if (editTxtHeight.text.toString().isEmpty()) {
            layoutEditHeight.error = getString(R.string.input_empty)
            return false
        }else if (editTxtGoal.text.toString().isEmpty()) {
            layoutEditGoal.error = getString(R.string.input_empty)
            return false
        }else if (editTxtName.text.toString().length>20) {
            layoutEditName.error = getString(R.string.name_input_error)
            return false
        }
        return true
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnSave-> {
                if(validate()){
                    saveData()
                }
            }
        }
    }

    private fun inputCheck() {
        editTxtName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > layoutEditName.counterMaxLength) {
                    Log.d("main","$count")
                    layoutEditName.error = getString(R.string.name_input_error)
                } else {
                    layoutEditName.error = null
                }
            }
        })
        editTxtHeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 0) {
                    Log.d("main","$count")
                    layoutEditHeight.error = getString(R.string.input_empty)
                } else {
                    layoutEditHeight.error = null
                }
            }
        })
        editTxtWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 0) {
                    Log.d("main","$count")
                    layoutEditWeight.error = getString(R.string.input_empty)
                } else {
                    layoutEditWeight.error = null
                }
            }
        })
        editTxtGoal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 0) {
                    Log.d("main","$count")
                    layoutEditGoal.error = getString(R.string.input_empty)
                } else {
                    layoutEditGoal.error = null
                }
            }
        })
    }

    private fun saveData() {
        val name: String = editTxtName.text.toString()
        val weight: Int = editTxtWeight.text.toString().toInt()
        val height: Int = editTxtHeight.text.toString().toInt()
        val goal: Int = editTxtGoal.text.toString().toInt()
        //save data
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("NAME", name)
        editor.putInt("WEIGHT", weight)
        editor.putInt("HEIGHT", height)
        editor.putInt("GOAL", goal)
        editor.putBoolean("CHECKBOX", true)
        editor.apply()

        val intent = Intent(this, StepTrackerActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun checkPermissions(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_ALL_NEEDED_PERMISSIONS)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_ALL_NEEDED_PERMISSIONS) {
            if (grantResults.contains(PackageManager.PERMISSION_DENIED)) {
                showPermissionsAlert()
            }
        }
    }

    private fun showPermissionsAlert() {
        AlertDialog
            .Builder(this)
            .apply {
                setTitle("Missing permissions")
                setMessage("Permissions are needed to use this app")
                setPositiveButton("I understand") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        permissions,
                        REQUEST_ALL_NEEDED_PERMISSIONS
                    )
                }
            }
            .create()
            .show()
    }

    private fun readStepFromFile() {
        GlobalScope.launch(Dispatchers.IO) {
            Log.i("XXX", "reading file")
            try {
                val reader = openFileInput("steps2.txt")?.bufferedReader().use { it?.readText() ?: "-1" }
                Log.i("XXX", "Reading value $reader")
                DataStreams.updateStepCount(reader.toInt())
            } catch (e: Exception) {
                Log.i("XXX", "error" + e.message.toString())
            }
            Log.i("XXX", "reading file done")
        }
    }
}
