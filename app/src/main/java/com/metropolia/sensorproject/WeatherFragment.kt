package com.metropolia.sensorproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.metropolia.sensorproject.services.DataStreams
import com.metropolia.sensorproject.services.Weather
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.fragment_weather.*
import java.net.URL
import java.util.concurrent.TimeUnit


class WeatherFragment : Fragment() {

    private val disposeOnDestroy = CompositeDisposable()

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
                val url = URL(it.icon)
                val imgStream = url.openConnection().getInputStream()
                Pair(it, BitmapFactory.decodeStream(imgStream))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (weather, icon) ->
                setupWeatherViews(weather, icon)
                gif.visibility = View.GONE
            }
            .addTo(disposeOnDestroy)

        DataStreams.getWeather()
    }

    private fun degToCompass(num: Int): String {
        val degree = num / 22.5 + 0.5
        val arr = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        return arr[(degree % 16).toInt()]
    }

    private fun rotateImage(image: ImageView, angle: Int){
        val matrix = Matrix()
        image.scaleType= ImageView.ScaleType.MATRIX
        matrix.postRotate(angle.toFloat(), image.drawable.bounds.width()/2.toFloat(),image.drawable.bounds.height()/2.toFloat())
        image.imageMatrix = matrix
    }

    private fun setupWeatherViews(weather: Weather, icon: Bitmap) {
        txtTemp.text = weather.curentTemp
        txtMain.text = weather.description
        txtFeel.text = weather.feelsLike
        txtHumidity.text = weather.humidity
        txtWind.text = weather.windSpeed
        val direction = degToCompass(weather.windDirection)
        txtDirection.text = direction
        weather_icon.setImageBitmap(icon)
        rotateImage(pointer,weather.windDirection)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeOnDestroy.clear()
    }

}