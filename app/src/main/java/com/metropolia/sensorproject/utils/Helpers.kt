package com.metropolia.sensorproject.utils

import android.graphics.Matrix
import android.graphics.Paint
import android.location.Location
import android.widget.ImageView
import com.metropolia.sensorproject.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.text.SimpleDateFormat
import java.util.*

/**
 * Compare dates helper
 * @param date  Date
 * @return Boolean
 * */
fun compareDate(date: Date): Boolean {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    return formatter.parse(formatter.format(date)) == formatter.parse(formatter.format(Date()))
}

/**
 *  Sets the tarting location on the map with a pin
 * */
fun MapView.setStartingLocation(location: Location) {
        val startingPoint = GeoPoint(
            location.latitude,
            location.longitude
        )
        val marker = Marker(this)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Start"
        marker.snippet ="You are here"
        marker.position = startingPoint
        this.controller.setZoom(14.0)
        this.controller.setCenter(startingPoint)
        this.overlays.add(marker)
}

/**
 *  Updates the map view with the route polyline
 * */
fun MapView.updateRoute(geoPoints: MutableList<GeoPoint>) {
    this.setMultiTouchControls(true)
    this.setBuiltInZoomControls(true)
    this.overlays.clear()
    val polyline = Polyline()
    polyline.setColor(R.color.pink)
    polyline.getPaint().setStrokeCap(Paint.Cap.ROUND)
    polyline.setPoints(geoPoints)
    polyline.setColor(R.color.darkPink)
    this.controller.setCenter(geoPoints.last())
    this.overlays.add(polyline)
    this.invalidate()
}

/**
 * Get the wind direction as a heading string
 * @param num wind direction as Int
 * @return String
 * */
fun degToCompass(num: Int): String {
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

/**
 * Rotates wind arrow according to angle
 * @param image ImageView
 * @param angle Int
 * */
fun rotateImage(image: ImageView, angle: Int) {
    val matrix = Matrix()
    image.scaleType = ImageView.ScaleType.MATRIX
    matrix.postRotate(
        angle.toFloat(),
        image.drawable.bounds.width() / 2.toFloat(),
        image.drawable.bounds.height() / 2.toFloat()
    )
    image.imageMatrix = matrix
}
