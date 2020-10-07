package com.metropolia.sensorproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.metropolia.sensorproject.models.DayDescription
import com.metropolia.sensorproject.models.Weather
import com.metropolia.sensorproject.utils.degToCompass
import com.metropolia.sensorproject.utils.rotateImage
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.fragment_weather.*
import java.net.URL

/**
 *  Displays current weather for location and a weather prediction for upcoming days
 * */
class WeatherFragment : Fragment() {

    private var dayList = ArrayList<DayDescription>()
<<<<<<< HEAD
    private lateinit var dayAdapter: CustomAdapter
    private var added: Boolean = false
=======
    private lateinit var dayAdapter: WeatherAdapter
>>>>>>> 5e888ef1f73b4f6488e8a487df9f417fe5a6f9be
    private val disposeOnDestroy = CompositeDisposable()

   override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_weather, container, false)!!

    override fun onResume() {
        super.onResume()
        // Loads a loading gif
        Glide.with(this)
            .load(R.drawable.giphy)
            .into(gif)
<<<<<<< HEAD
=======

        // Rx Subscription to load weather, icon and bind the data to views
>>>>>>> 5e888ef1f73b4f6488e8a487df9f417fe5a6f9be
        StepApp
            .weatherStream
            .take(1)
            .observeOn(io())
            .map {
                val url = URL(it.icon)
                val imgStream = url.openConnection().getInputStream()
                Pair(it, BitmapFactory.decodeStream(imgStream))
            }
            .observeOn(AndroidSchedulers.mainThread())


            .subscribe { (weather, icon) ->
                setupWeatherViews(weather, icon)
                recyclerViewSetUp(weather)

                //loading disappears after data loaded
                gif.visibility = View.GONE
            }
            .addTo(disposeOnDestroy)

        StepApp.getCurrentWeather()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeOnDestroy.clear()
    }

    /**
     * Recyclerview for weather forcast
     * @param weather Weather
     * */
    private fun recyclerViewSetUp(weather: Weather) {
        dayList.clear()
        weather.daily.forEach { dayList.add(it) }
        dayAdapter = WeatherAdapter(dayList)
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        lv_weather.layoutManager = mLayoutManager
        lv_weather.adapter = dayAdapter
        Log.d("weather", "$dayList")
    }

    /**
     *  Sets up the views
     *  @param weather Weather
     *  @param icon Bitmap
     * */
    private fun setupWeatherViews(weather: Weather, icon: Bitmap) {
        txtTemp.text = weather.currentTemp
        txtMain.text = weather.description
        txtFeel.text = weather.feelsLike
        txtHumidity.text = weather.humidity
        txtWind.text = weather.windSpeed
        val direction = degToCompass(weather.windDirection)
        txtDirection.text = direction
        weather_icon.setImageBitmap(icon)
        rotateImage(pointer, weather.windDirection)
    }
}
