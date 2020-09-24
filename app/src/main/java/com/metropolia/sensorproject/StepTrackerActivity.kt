package com.metropolia.sensorproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_step_tracker.*

class StepTrackerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_tracker)
        toolbar.setLogo(R.drawable.ic_man)
        toolbar.setTitle(R.string.slogan)
        toolbar.setTitleTextColor(getResources().getColor(R.color.dark))
        toolbar.setBackgroundColor(getResources().getColor(R.color.lightPink))
        setupTabs()
    }

    private fun setupTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        //add title and fragment to tab
        adapter.addFragment(TodayFragment(),"Today")
        adapter.addFragment(ProgressFragment(),"Progress")
        adapter.addFragment(WeatherFragment(),"Weather")
        viewPager.adapter = adapter
        tablayout.setupWithViewPager(viewPager)

        //add icon to tab
        tablayout.getTabAt(0)!!.setIcon(R.drawable.ic_footprint)
        tablayout.getTabAt(1)!!.setIcon(R.drawable.ic_development)
        tablayout.getTabAt(2)!!.setIcon(R.drawable.ic_storm)
    }
}