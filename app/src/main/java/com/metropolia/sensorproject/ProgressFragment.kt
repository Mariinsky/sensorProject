package com.metropolia.sensorproject

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.metropolia.sensorproject.models.ProgressViewModel
import com.metropolia.sensorproject.utils.animateTo
import com.metropolia.sensorproject.utils.getFormattedDate
import com.metropolia.sensorproject.utils.setBigMax
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.component_progress_bar.view.*
import kotlinx.android.synthetic.main.fragment_progress.*
import kotlinx.android.synthetic.main.fragment_progress.view.*
import java.text.SimpleDateFormat
import java.util.*


class ProgressFragment : Fragment() {
    lateinit var preferences: SharedPreferences
    private lateinit var viewModel: ProgressViewModel
    private var goal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout and set element for this fragment
        val rootView = inflater.inflate(R.layout.fragment_progress, container, false)

        viewModel = ViewModelProvider(this).get(ProgressViewModel::class.java)
        // retrieve data
        preferences = activity!!.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        goal = preferences.getInt("GOAL", 0)
        val name = preferences.getString("NAME", "")
        val height = preferences.getInt("HEIGHT", 0)
        val weight = preferences.getInt("WEIGHT", 0)

        rootView.floating_action_button.setOnClickListener{
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
            val  mAlertDialog = mBuilder.show()
            mDialogView.btnSave.setOnClickListener{

                val newName: String = mDialogView.editTxtName.text.toString()
                val newWeight: Int = mDialogView.editTxtWeight.text.toString().toInt()
                val newHeight: Int = mDialogView.editTxtHeight.text.toString().toInt()
                val newGoal: Int = mDialogView.editTxtGoal.text.toString().toInt()
                //validate
                /*if(newName.length >20 ){
                    layoutName.error = getString(R.string.name_input_error)
                } else if (newWeight.toString().length > 3 ){
                    layoutWeight.error = getString(R.string.input_error)
                }else if (newHeight.toString().length > 3) {
                    layoutHeight.error = getString(R.string.input_error)
                }else if (editName.text.toString().isEmpty()) {
                    layoutName.error = getString(R.string.input_empty)
                }else if (editWeight.text.toString().isEmpty()) {
                    layoutWeight.error = getString(R.string.input_empty)
                }else if (editHeight.text.toString().isEmpty()) {
                    layoutHeight.error = getString(R.string.input_empty)
                } else if (editGoal.text.toString().isEmpty()) {
                    layoutGoal.error = getString(R.string.input_empty)
                } else {*/
                    //save data
                    preferences =
                        getActivity()!!.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = preferences.edit()
                    editor.putString("NAME", newName)
                    editor.putInt("WEIGHT", newWeight)
                    editor.putInt("HEIGHT", newHeight)
                    editor.putInt("GOAL", newGoal)
                    editor.commit()

                    val intent = Intent(activity, StepTrackerActivity::class.java)
                    intent.putExtra("TabNumber", "1");
                    startActivity(intent)

            }
            mDialogView.btnCancel.setOnClickListener{
                mAlertDialog.dismiss()
            }
        }
        return rootView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        viewModel
            .limitedActivitiesSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                /*prgress_container.removeAllViews()
                if (it.isNotEmpty()) {
                    day_details.text = "${getFormattedDate(it.first().date)} ${it.first().Steps} steps"
                } else {
                    day_details.text = ""
                }
                    it.forEach { day ->
                        val v = layoutInflater.inflate(R.layout.component_progress_bar, null) as LinearLayout
                        v.date.text = SimpleDateFormat("EE", Locale.ENGLISH).format(day.date)
                        v.progress_bar.setBigMax(goal)
                        v.progress_bar.animateTo(day.Steps, 200)
                        v.setOnClickListener {
                            day_details.text = "${getFormattedDate(day.date)} ${day.Steps} steps"
                        }
                        prgress_container.addView(v)
                    }*/
                it.forEach {day ->
                    val entries = ArrayList<BarEntry>()
                    for(x in 0..7) entries.add(BarEntry(day.Steps.toFloat(),x.toFloat()))
                    val barDataSet = BarDataSet(entries, "Steps")
                    val labels = ArrayList<String>()
                    labels.add(SimpleDateFormat("EE", Locale.ENGLISH).format(day.date))
                    val data = BarData(barDataSet)
                    data.setBarWidth(0.9f)
                    barchart.data = data
                    barchart.setFitBars(true); // make the x-axis fit exactly all bars
                    barchart.invalidate(); // refresh// set the data and list of lables into chart
                }

            }
        viewModel.getLimitedActivities(7)
    }
}