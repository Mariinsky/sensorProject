package com.metropolia.sensorproject

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.metropolia.sensorproject.models.ProgressViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.metropolia.sensorproject.database.DayActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.metropolia.sensorproject.services.Weather
import com.metropolia.sensorproject.utils.updateRoute
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers.io
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_progress.*
import kotlinx.android.synthetic.main.fragment_progress.view.*
import kotlinx.android.synthetic.main.fragment_progress.view.map
import kotlinx.android.synthetic.main.fragment_today.view.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ProgressFragment : Fragment() {
    lateinit var preferences: SharedPreferences
    private lateinit var viewModel: ProgressViewModel
    private val barChartValueTapped = PublishSubject.create<DayActivity>()
    private val unsubscribeOnDestroy = CompositeDisposable()
    private val gson = Gson()
    private var goal = 0
    private var shouldAnimate = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout and set element for this fragment
        val rootView = inflater.inflate(R.layout.fragment_progress, container, false)

        rootView.map?.setTileSource(TileSourceFactory.MAPNIK)
        rootView.map?.setMultiTouchControls(true)
        rootView.map?.controller?.setZoom(9.0)

        viewModel = ViewModelProvider(this).get(ProgressViewModel::class.java)
        // retrieve data
        preferences = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        goal = preferences.getInt("GOAL", 0)
        val name = preferences.getString("NAME", "")
        val height = preferences.getInt("HEIGHT", 0)
        val weight = preferences.getInt("WEIGHT", 0)

        rootView.floating_action_button.setOnClickListener {
            //Inflate the dialog and element with custom view
            val mDialogView = LayoutInflater.from(activity).inflate(R.layout.alert_dialog, null)

            //set input for each text field
            mDialogView.editTxtName.setText(name)
            mDialogView.editTxtWeight.setText(weight.toString())
            mDialogView.editTxtHeight.setText(height.toString())
            mDialogView.editTxtGoal.setText(goal.toString())

            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(activity)
                .setView(mDialogView)
            //show dialog
            val mAlertDialog = mBuilder.show()

            mDialogView.editTxtName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (count > mDialogView.layoutEditName.counterMaxLength) {
                        mDialogView.layoutEditName.error = getString(R.string.name_input_error)
                    }else if (count == 0){
                        mDialogView.layoutEditName.error = getString(R.string.input_empty)
                    } else {
                        mDialogView.layoutEditName.error = null
                    }
                }
            })
            mDialogView.editTxtHeight.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (count == 0) {
                        mDialogView.layoutEditHeight.error = getString(R.string.input_empty)
                    } else {
                        mDialogView.layoutEditHeight.error = null
                    }
                }
            })
            mDialogView.editTxtWeight.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (count == 0) {
                        mDialogView.layoutEditWeight.error = getString(R.string.input_empty)
                    } else {
                        mDialogView.layoutEditWeight.error = null
                    }
                }
            })
            mDialogView.editTxtGoal.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (count == 0) {
                        mDialogView.layoutEditGoal.error = getString(R.string.input_empty)
                    } else {
                        mDialogView.layoutEditGoal.error = null
                    }
                }
            })


            mDialogView.btnSave.setOnClickListener(object : View.OnClickListener {
                val newName: String = mDialogView.editTxtName.text.toString()
                val newWeight: Int = mDialogView.editTxtWeight.text.toString().toInt()
                val newHeight: Int = mDialogView.editTxtHeight.text.toString().toInt()
                val newGoal: Int = mDialogView.editTxtGoal.text.toString().toInt()

                private fun validate(): Boolean {
                    if (mDialogView.editTxtName.text.toString().isEmpty()) {
                        mDialogView.layoutEditName.error = getString(R.string.input_empty)
                        return false
                    } else if (mDialogView.editTxtWeight.text.toString().isEmpty()) {
                        mDialogView.layoutEditWeight.error = getString(R.string.input_empty)
                        return false
                    } else if (mDialogView.editTxtHeight.text.toString().isEmpty()) {
                        mDialogView.layoutEditHeight.error = getString(R.string.input_empty)
                        return false
                    } else if (mDialogView.editTxtGoal.text.toString().isEmpty()) {
                        mDialogView.layoutEditGoal.error = getString(R.string.input_empty)
                        return false
                    } else if (mDialogView.editTxtName.text.toString().length > 20) {
                        mDialogView.layoutEditName.error = getString(R.string.name_input_error)
                        return false
                    } else if (mDialogView.editTxtWeight.text.toString().length > 3) {
                        mDialogView.layoutEditWeight.error = getString(R.string.input_error)
                        return false
                    } else if (mDialogView.editTxtHeight.text.toString().length > 3) {
                        mDialogView.layoutEditHeight.error = getString(R.string.input_error)
                        return false
                    }
                    return true
                }

                private fun saveData() {
                    val name: String = mDialogView.editTxtName.text.toString()
                    val weight: Int = mDialogView.editTxtWeight.text.toString().toInt()
                    val height: Int = mDialogView.editTxtHeight.text.toString().toInt()
                    val goal: Int = mDialogView.editTxtGoal.text.toString().toInt()
                    //save data
                    val editor: SharedPreferences.Editor = preferences.edit()
                    editor.putString("NAME", name)
                    editor.putInt("WEIGHT", weight)
                    editor.putInt("HEIGHT", height)
                    editor.putInt("GOAL", goal)
                    editor.putBoolean("CHECKBOX", true)
                    editor.apply()

                    val intent = Intent(activity, StepTrackerActivity::class.java)
                    intent.putExtra("TabNumber", "1");
                    startActivity(intent)
                }


                override fun onClick(p0: View?) {
                    when (p0?.id) {
                        R.id.btnSave -> {
                            if (validate()) {
                                saveData()
                            }
                        }
                    }
                }
            })

            mDialogView.btnCancel.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }
        return rootView
    }

    override fun onResume() {
        super.onResume()

        viewModel
            .limitedActivitiesSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (steps, activities) ->
                if (activities.isNotEmpty()) { setupBarChart(activities) }
                setupWeekStatistic(steps)
            }

        barChartValueTapped
            .observeOn(io())
            .map {
                var weather: Weather? = null
                var routeFromJson:MutableList<GeoPoint>?  = null
                if (it.weather != null) {
                    weather = gson.fromJson(it.weather, Weather::class.java)
                }
                if (it.route != null) {
                    val routeList: Type = object : TypeToken<MutableList<GeoPoint?>?>() {}.type
                    routeFromJson = gson.fromJson(it.route, routeList)
                }
                Triple(it, weather, routeFromJson)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (day, weather, route) ->
                day_steps?.text = "Steps: ${day.Steps}"
                day_distance?.text = "Distance: ${day.distance.toInt()}m"
                if(weather != null) {
                    day_weather_container?.visibility = View.VISIBLE
                    day_temp?.text = weather.curentTemp
                    day_wind?.text = weather.windSpeed
                }
                if(route != null) {
                    map?.updateRoute(route)
                }
            }.addTo(unsubscribeOnDestroy)
        viewModel.getLimitedActivities(7)
    }

    private fun setupWeekStatistic(steps: MutableList<Int>) {
        week_total_steps.text = steps.sum().toString()
        average_steps.text = steps.average().toInt().toString()
        best_result_day.text = steps.maxOrNull()?.toString() ?: "0"
    }

    private fun setupBarChart(activityData: List<DayActivity>) {
        val entries = ArrayList<BarEntry>()
        val days = ArrayList<String>()
        //generate data for each bar and xaxis
        activityData.forEach { day ->
            day.Steps?.toFloat()?.let { BarEntry(entries.size.toFloat(), it) }?.let { entries.add(it) }
            days.add(SimpleDateFormat("MM-dd", Locale.ENGLISH).format(day.date))
        }

        val barDataSet = BarDataSet(entries, "Steps")
        barDataSet.color = resources.getColor(R.color.darkPink)
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(barDataSet)
        //set description
        val description = Description()
        description.text = ""
        barchart.description = description
        val data = BarData(barDataSet)
        data.barWidth = 0.9f
        //no data display
        barchart.setNoDataText("No data")
        //generate data for chart
        barchart.data = data
        barchart.setBackgroundColor(resources.getColor(R.color.lightPink))
        barchart.setFitBars(true) // make the x-axis fit exactly all bars
        if (shouldAnimate) {
            shouldAnimate = false
            barchart.animateY(500)
        }
        // add listener
        barchart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {}
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    barChartValueTapped.onNext(activityData[e.x.toInt()])
                }
            }
        })
        //customize xAxis
        val xAxis = barchart.xAxis
        xAxis.granularity = 1f; // minimum axis-step (interval) is 1
        xAxis.position = XAxis.XAxisPosition.BOTTOM;
        val xAxisFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String? {
                return days.get(value.toInt())
            }
            fun getDecimalDigits(): Int { return 0 }
        }
        with(xAxis) {
            valueFormatter = xAxisFormatter
            labelCount = 7
        }
        barchart.invalidate(); // refresh
    }
}