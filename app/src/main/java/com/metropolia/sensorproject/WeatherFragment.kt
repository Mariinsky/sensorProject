package com.metropolia.sensorproject


import android.graphics.Bitmap
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
import com.metropolia.sensorproject.services.DayDescription
import com.metropolia.sensorproject.services.Weather
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.fragment_weather.*
import java.net.URL


class WeatherFragment : Fragment() {
    companion object {
        fun newInstance(): WeatherFragment = WeatherFragment()
    }

    private var dayList = ArrayList<DayDescription>()
    private lateinit var dayAdapter: CustomAdapter
    private var added: Boolean = false

    private val disposeOnDestroy = CompositeDisposable()

   override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_weather, container, false)!!


    override fun onResume() {
        super.onResume()
        Glide.with(this)
            .load(R.drawable.giphy)
            .into(gif)
        StepApp
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
                recyclerViewSetUp(weather)
                //loading disappears after data loaded
                gif.visibility = View.GONE
            }
            .addTo(disposeOnDestroy)
        StepApp.getWeather()
    }


    //recycler view for 8 days forecast
    private fun recyclerViewSetUp(weather: Weather) {
        dayList.clear()
        weather.daily.forEach { dayList.add(it) }
        dayAdapter = CustomAdapter(dayList)
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        lv_weather.layoutManager = mLayoutManager
        lv_weather.adapter = dayAdapter
        Log.d("weather", "$dayList")
    }

    //define direction from angle
    private fun degToCompass(num: Int): String {
        val degree = num / 22.5 + 0.5
        val arr = arrayOf(
            "N",
            "NNE",
            "NE",
            "ENE",
            "E",
            "ESE",
            "SE",
            "SSE",
            "S",
            "SSW",
            "SW",
            "WSW",
            "W",
            "WNW",
            "NW",
            "NNW"
        )
        return arr[(degree % 16).toInt()]
    }

    //rotate arrow according to wind degree
    private fun rotateImage(image: ImageView, angle: Int) {
        val matrix = Matrix()
        image.scaleType = ImageView.ScaleType.MATRIX
        matrix.postRotate(
            angle.toFloat(),
            image.drawable.bounds.width() / 2.toFloat(),
            image.drawable.bounds.height() / 2.toFloat()
        )
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
        rotateImage(pointer, weather.windDirection)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeOnDestroy.clear()
    }

}
