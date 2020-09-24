package com.metropolia.sensorproject

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.metropolia.sensorproject.models.ProgressViewModel
import com.metropolia.sensorproject.workmanager.StepWorkManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.fragment_today.view.*
import kotlin.math.round


class TodayFragment : Fragment() {
    private lateinit var preferences: SharedPreferences
    private lateinit var viewModel: ProgressViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(ProgressViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout and set element for this fragment
        val view = inflater.inflate(R.layout.fragment_today,container,false)
        // get goal data from shared preferences y
        preferences = activity!!.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val goal = preferences.getInt("GOAL", 0)
        view.txt_goal_step.text = goal.toString()
        view.progressBar.max = goal
        view.txt_total_step.text = viewModel.stepCount.toString()
        view.progressBar.progress = viewModel.stepCount
        view.txtKcal.text = (viewModel.stepCount * 0.4).toInt().toString()
        return view
    }

    override fun onResume() {
        super.onResume()
        btnStart.setOnClickListener {
            val stepWorker: WorkRequest = OneTimeWorkRequestBuilder<StepWorkManager>().build()
            WorkManager.getInstance(activity!!.application).enqueue(stepWorker)
        }

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