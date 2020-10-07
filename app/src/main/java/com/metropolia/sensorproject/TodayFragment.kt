package com.metropolia.sensorproject

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.metropolia.sensorproject.models.ProgressViewModel
import com.metropolia.sensorproject.utils.setStartingLocation
import com.metropolia.sensorproject.utils.updateRoute
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.fragment_today.view.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import kotlin.math.round

/**
 *  Fragment to start the pedometer.
 *   - shows steps
 *   - progress towards user set goal
 *   - distance traveled in meters
 *   - kcal burned walking
 *   - Timer
 *   - Map with starting location and route
 * */
class TodayFragment : Fragment() {
    private lateinit var preferences: SharedPreferences
    private lateinit var viewModel: ProgressViewModel
    private val unsubscribeOnDestroy = CompositeDisposable()
    private var timerStart = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(ProgressViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_today, container, false)
        setupPage(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        setSubscriptions()
        btnStart.setOnClickListener {
            startStopChronometer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unsubscribeOnDestroy.clear()
    }

    /**
     *  Sets up the view
     *  @param v root view of the fragment
     * */
    private fun setupPage(v: View) {
        // get goal data from shared preferences
        preferences = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val goal = preferences.getInt("GOAL", 0)
        v.txt_goal_step.text = goal.toString()
        v.progressBar.max = goal
        v.map.setTileSource(TileSourceFactory.MAPNIK)
        v.map.setMultiTouchControls(true)
        v.map.controller.setZoom(14.0)
        StepApp.chronometer = v.txtTime
        StepApp.setChronoListener()
        v.txt_total_step.text = StepApp.getStepCount().toString()
        v.progressBar.progress = StepApp.getStepCount()
        v.txtKcal.text = (StepApp.getStepCount() * 0.4).toInt().toString()
        v.txtDistance.text = StepApp.getDistance().toInt().toString()
        StepApp.setChronoBase()
    }

    /**
     *  Sets up rx subscriptions
     *  - step
     *  - location
     *  - starting location
     * */
    private fun setSubscriptions() {
        viewModel
            .emitStepsCount
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                txt_total_step?.text = it.toString()
                progressBar?.progress = it
                val kcal = round(it * 0.3 * 100) / 100
                txtKcal?.text = kcal.toString()
            }.addTo(unsubscribeOnDestroy)

        viewModel
            .locationServiceSubject
            .subscribe { (distance, route) ->
                txtDistance?.text = distance
                map?.updateRoute(route)
            }.addTo(unsubscribeOnDestroy)

        viewModel
            .startingLocationSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                map?.setStartingLocation(it)
            }.addTo(unsubscribeOnDestroy)
    }

    /**
     *  Starts and stops timer
     * */
    private fun startStopChronometer() {
        timerStart = when (timerStart) {
            true -> {
                StepApp.chronometer?.stop()
                viewModel.stopServices.onNext(Unit)
                btnStart.text = getString(R.string.start_button)
                false
            }
            else -> {
                StepApp.setChronoBase()
                viewModel.startServices.onNext(Unit)
                btnStart.text = getString(R.string.stop_button)
                StepApp.chronometer?.start()
                true
            }
        }
    }
}