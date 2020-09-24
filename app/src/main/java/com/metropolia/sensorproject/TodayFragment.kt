package com.metropolia.sensorproject

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_today.*

class TodayFragment : Fragment() {
    lateinit var preferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout and set element for this fragment
        val rootView = inflater.inflate(R.layout.fragment_today,container,false)
        val twGoal: TextView = rootView.findViewById(R.id.txt_goal_step)
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)
        // get goal data from shared preferences
        preferences = getActivity()!!.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val goal = preferences.getInt("GOAL", 0)
        twGoal!!.text = goal.toString()
        progressBar.max = goal
        progressBar.progress =1500
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}