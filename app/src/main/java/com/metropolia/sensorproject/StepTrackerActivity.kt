package com.metropolia.sensorproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.metropolia.sensorproject.debug.DevActivity
import kotlinx.android.synthetic.main.activity_step_tracker.*

/**
 *  Core activity for the app with three fragments.
 *   - Today fragment
 *   - Progress fragment
 *   - Weather fragment
 * */
class StepTrackerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_tracker)
        setupToolbar()
        setupTabs()
        val intent = intent
        if (intent.hasExtra("TabNumber")) {
            val tab = intent.getStringExtra("TabNumber")
            switchToTab(tab)
        }
    }

    /**
     *  Display specific tab when redirect to this activity
     *  @param tab tab number as string
     * */
    private fun switchToTab(tab: String?) {
        when (tab) {
            "0" -> viewPager.currentItem = 0
            "1" -> viewPager.currentItem = 1
            "2" -> viewPager.currentItem = 2
        }
    }

    /**
     *  Setup for toolbar
     * */
    private fun setupToolbar() {
        toolbar.setLogo(R.drawable.ic_man)
        toolbar.setTitle(R.string.slogan)
        toolbar.setTitleTextColor(getResources().getColor(R.color.dark))
        toolbar.setBackgroundColor(getResources().getColor(R.color.lightPink))
        toolbar.setOnClickListener {
            startActivity(Intent(this, DevActivity::class.java))
        }
    }

    /**
     *  Sets up viewpager tabs with fragments.
     * */
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