package com.metropolia.sensorproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.metropolia.sensorproject.database.User
import com.metropolia.sensorproject.database.UserDB
import com.metropolia.sensorproject.database.UserModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val db by lazy { UserDB.get(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val count = ViewModelProviders.of(this).get(UserModel::class.java).getCount()
        //count.getCount().observe(this, Observer {
        //Toast.makeText(this@MainActivity, "$count", Toast.LENGTH_SHORT).show()

        //})


        inputCheck()
        btnSave.setOnClickListener {
            addUser()
            val intent = Intent(this, StepTrackerActivity::class.java)
            startActivity(intent)
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

    private fun addUser() {
        var user = User(
            0,
            editTxtName.text.toString(),
            editTxtWeight.text.toString().toInt(),
            editTxtHeight.text.toString().toInt(),
            editTxtGoal.text.toString().toInt(),
        )
        GlobalScope.launch {
            val id = db.userDao().insert(user)
            withContext(Main) {
                Toast.makeText(this@MainActivity, "$id", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
