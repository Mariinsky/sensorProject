package com.metropolia.sensorproject

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.metropolia.sensorproject.services.DataStreams
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.fragment_weather.*
import java.net.URL


class WeatherFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_weather, container, false)!!

    override fun onResume() {
        super.onResume()
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
                textView2.text = weather.current.temp.toString()
                weather_icon.setImageBitmap(icon)
                loading.visibility = View.GONE
            }

        DataStreams.getWeater()
    }
}