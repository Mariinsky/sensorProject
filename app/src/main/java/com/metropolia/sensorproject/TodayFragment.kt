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
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.metropolia.sensorproject.models.ProgressViewModel
import com.metropolia.sensorproject.services.LocationService
import com.metropolia.sensorproject.workmanager.StepWorkManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.fragment_today.view.*
import kotlin.math.round

class TodayFragment : Fragment() {
    private lateinit var preferences: SharedPreferences
    private lateinit var viewModel: ProgressViewModel
    private lateinit var workManager: WorkManager
    private lateinit var locationService: LocationService
    private var start = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(ProgressViewModel::class.java)
        workManager = WorkManager.getInstance(activity!!.application)
        locationService = LocationService(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // get goal data from shared preferences y
        val view = inflater.inflate(R.layout.fragment_today,container,false)
        preferences = activity!!.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val goal = preferences.getInt("GOAL", 0)
        view.txt_goal_step.text = goal.toString()
        view.progressBar.max = goal

        return view
    }

    override fun onResume() {
        super.onResume()
        btnStart.setOnClickListener {
            if (!start) {
                val stepWorker: WorkRequest = OneTimeWorkRequestBuilder<StepWorkManager>()
                    .addTag("step")
                    .build()
                workManager.enqueue(stepWorker)
                locationService.startGettingLocation()
                start = true
                btnStart.text = getString(R.string.stop_button)
            } else {
                start = false
                btnStart.text = getString(R.string.start_button)
                locationService.stopLocationService()
                workManager.cancelAllWork()
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
                val kcal = round(it*0.4*100) / 100
                txtKcal.text = kcal.toString()
            }
    }
}