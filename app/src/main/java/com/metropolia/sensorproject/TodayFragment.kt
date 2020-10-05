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
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.fragment_today.view.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import kotlin.math.round

class TodayFragment : Fragment() {
    private lateinit var preferences: SharedPreferences
    private lateinit var viewModel: ProgressViewModel
    private var start = false
    private var timerStart = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(ProgressViewModel::class.java)
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
        view.map.controller.setZoom(14.0)
        StepApp.chronometer = view.txtTime
        StepApp.setChronoListener()
        return view
    }

    override fun onResume() {
        super.onResume()

        btnStart.setOnClickListener {
            startStopChronometer()
            if (!start) {
<<<<<<< HEAD
                val stepWorker: WorkRequest = OneTimeWorkRequestBuilder<StepWorkManager>()
                    .addTag("step")
                    .build()
                workManager.enqueue(stepWorker)
                //locationService.getLocation()
                locationService.startGettingLocation()
=======
                viewModel.startServices.onNext(Unit)
>>>>>>> 9a11b18a0e3f3cf5cbd95617434e177c088a823f
                start = true
                btnStart.text = getString(R.string.stop_button)
            } else {
                start = false
                viewModel.stopServices.onNext(Unit)
                btnStart.text = getString(R.string.start_button)

            }
        }
        txt_total_step.text = StepApp.getStepCount().toString()
        progressBar.progress = StepApp.getStepCount()
        txtKcal.text = (StepApp.getStepCount() * 0.4).toInt().toString()
        txtDistance.text = StepApp.getDistance().toInt().toString()
        StepApp.setChronoBase()

        viewModel
            .emitStepsCount
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                txt_total_step?.text = it.toString()
                progressBar?.progress = it
                val kcal = round(it * 0.3 * 100) / 100
                txtKcal?.text = kcal.toString()
            }

        viewModel
            .locationServiceSubject
            .subscribe { (distance, route) ->
                txtDistance?.text = distance
                map?.updateRoute(route)
            }


        viewModel
            .startingLocationSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                    map?.setStartingLocation(it)
            }
    }

   private fun startStopChronometer() {
       timerStart = when(timerStart) {
           true -> {
               StepApp.chronometer?.stop()
               false
           }
           else -> {
               StepApp.setChronoBase()
               StepApp.chronometer?.start()
               true
           }
       }
    }
}