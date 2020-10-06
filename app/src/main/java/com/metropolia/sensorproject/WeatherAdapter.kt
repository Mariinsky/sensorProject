package com.metropolia.sensorproject


import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.metropolia.sensorproject.models.DayDescription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


/**
 *  Custom adapter to display a seven day weather horizontally
 * */
class WeatherAdapter(private var list: ArrayList<DayDescription>) : RecyclerView.Adapter<WeatherAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var day: TextView = view.findViewById(R.id.date)
        var temp: TextView = view.findViewById(R.id.temp)
        var img: ImageView = view.findViewById(R.id.icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val date = list[position]
        
        //get date for next 8days
        val day = LocalDate.now().plus((position+1).toLong(), ChronoUnit.DAYS)
        val formatted= day.format(DateTimeFormatter.ofPattern("EE, MM-dd"))
        holder.temp.text= date.dayTemp
        holder.day.text = formatted.toString()
        
        //get image from url
        GlobalScope.launch(Dispatchers.IO) {
            val imageUrl = URL("https://openweathermap.org/img/wn/${date.weather[0].icon}@4x.png")
            val httpConnection = imageUrl.openConnection() as HttpURLConnection
            httpConnection.doInput = true
            httpConnection.connect()

            val inputStream =  imageUrl.openConnection().getInputStream()
            val bitmapImage = BitmapFactory.decodeStream(inputStream)

            launch(Dispatchers.Main) {
                holder.img.setImageBitmap(bitmapImage)
            }
        }
        
        //animated item when hover on
        holder.itemView.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if(p1) {
                val anim = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_out)
                holder.itemView.startAnimation(anim)
                anim.fillAfter = true
            } else {
                val anim = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_in)
                holder.itemView.startAnimation(anim)
                anim.fillAfter = true
            }
        }
    }
}