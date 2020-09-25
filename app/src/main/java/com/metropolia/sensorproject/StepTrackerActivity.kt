package com.metropolia.sensorproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        val intent = intent
        if (intent.hasExtra("TabNumber")) {
            val tab = intent.getStringExtra("TabNumber")
            switchToTab(tab)
        }
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

    //display specific tab when redirect to this activity
    fun switchToTab(tab: String?) {
        if (tab == "0") {
            viewPager.currentItem = 0
        } else if (tab == "1") {
            viewPager.currentItem = 1
        } else if (tab == "2") {
            viewPager.currentItem = 2
        }
    }
}