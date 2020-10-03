package com.metropolia.sensorproject

import android.content.Context
import android.content.SharedPreferences
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.metropolia.sensorproject.models.ProgressViewModel
import com.metropolia.sensorproject.services.LocationService
import com.metropolia.sensorproject.services.StepSensorService
import com.metropolia.sensorproject.utils.setStartingLocation
import com.metropolia.sensorproject.utils.updateRoute
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.fragment_today.view.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import kotlin.math.round


class TodayFragment : Fragment() {
    private lateinit var preferences: SharedPreferences
    private lateinit var viewModel: ProgressViewModel
    private lateinit var locationService: LocationService
    private lateinit var stepSensor: StepSensorService
    private var start = false
    private var timerStart = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(ProgressViewModel::class.java)
        stepSensor = StepSensorService(context.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
        locationService = LocationService(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // get goal data from shared preferences y
        val view = inflater.inflate(R.layout.fragment_today, container, false)
        preferences = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val goal = preferences.getInt("GOAL", 0)
        view.txt_goal_step.text = goal.toString()
        view.progressBar.max = goal
        view.map.setTileSource(TileSourceFactory.MAPNIK)
        view.map.setMultiTouchControls(true)
        view.map.controller.setZoom(9.0)
        locationService.getLocation()
        StepApp.chronometer = view.txtTime
        StepApp.setChronoListener()
        return view
    }

    override fun onResume() {
        super.onResume()

        btnStart.setOnClickListener {
            startStopChronometer()
            if (!start) {
                stepSensor.registerListener()
                viewModel.startStepWriter()
                locationService.startGettingLocation()
                start = true
                btnStart.text = getString(R.string.stop_button)
            } else {
                start = false
                stepSensor.unregisterListener()
                btnStart.text = getString(R.string.start_button)
                locationService.stopLocationService()
                viewModel.disposeWriter()
            }
        }
        txt_total_step.text = viewModel.stepCount.toString()
        progressBar.progress = viewModel.stepCount
        txtKcal.text = (viewModel.stepCount * 0.4).toInt().toString()

        viewModel
            .emitStepsCount
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                txt_total_step.text = it.toString()
                progressBar.progress = it
                val kcal = round(it * 0.4 * 100) / 100
                txtKcal.text = kcal.toString()
            }


        viewModel
            .routeLocationSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it != null) {
                    map.updateRoute(it)
                }
            }

        viewModel
            .strtingLocationSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (map != null) {
                    map.setStartingLocation(it)
                }
            }
    }

   private fun startStopChronometer() {
        when(timerStart) {
            true -> {
                StepApp.chronometer?.stop();
                timerStart = false;
            }
            else -> {
                StepApp.chronometer?.base = SystemClock.elapsedRealtime();
                StepApp.chronometer?.start();
                timerStart = true
            }
        }
    }
}