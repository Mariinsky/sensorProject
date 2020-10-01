package com.metropolia.sensorproject

import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.metropolia.sensorproject.services.DataStreams
import com.metropolia.sensorproject.services.DayDescription
import com.metropolia.sensorproject.services.LocationService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.fragment_weather.*
import java.net.URL


class WeatherFragment : Fragment() {
    private var dayList = ArrayList<DayDescription>()
    private lateinit var dayAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_weather, container, false)!!

    override fun onResume() {
        super.onResume()

        Glide.with(this)
            .load(R.drawable.giphy)
            .into(gif)
        DataStreams
            .weatherSubject
            .take(1)
            .observeOn(io())
            .map {
                val url = URL("https://openweathermap.org/img/wn/${it.current.weather[0].icon}@4x.png")
                val imgStream = url.openConnection().getInputStream()
                Pair(it, BitmapFactory.decodeStream(imgStream))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {(weather, icon) ->
                txtTemp.setText("${weather.current.temp} °F")
                txtMain.setText("${weather.current.weather[0].main}")
                txtFeel.setText("Feels like ${weather.current.feels_like} °F")
                txtHumidity.setText("${weather.current.humidity}%")
                txtWind.setText("${weather.current.win_speed} mph")
                val direction = degToCompass(weather.current.wind_deg)
                txtDirection.setText("$direction")
                weather_icon.setImageBitmap(icon)
                rotateImage(pointer,weather.current.wind_deg)

                //recycler view for 7 days forecast
                weather.daily.forEach {dayList.add(it)}
                dayAdapter = CustomAdapter(dayList)
                val mLayoutManager = LinearLayoutManager(activity)
                mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                lv_weather.layoutManager = mLayoutManager
                lv_weather.adapter = dayAdapter
                Log.d("weather", "$dayList")
                //loading disappears after data loaded
                gif.visibility = View.GONE

            }
        DataStreams.getWeather()
    }

    private fun degToCompass(num: Int): String {
        val degree = num / 22.5 + 0.5
        val arr = arrayOf<String>("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        return arr[(degree % 16).toInt()]
    }

    private fun rotateImage(image: ImageView, angle: Int){
        val matrix = Matrix()
        image.scaleType= ImageView.ScaleType.MATRIX
        matrix.postRotate(angle.toFloat(), image.getDrawable().getBounds().width()/2.toFloat(),image.getDrawable().getBounds().height()/2.toFloat())
        image.setImageMatrix(matrix)
    }
}

