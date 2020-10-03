package com.metropolia.sensorproject.utils

import android.animation.ObjectAnimator
import android.location.Location
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import com.metropolia.sensorproject.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

fun compareDate(date: Date): Boolean {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    return formatter.parse(formatter.format(date)) == formatter.parse(formatter.format(Date()))
}

fun MapView.setStartingLocation(location: Location) {
        val startingPoint = GeoPoint(
            location.latitude,
            location.longitude
        )
        val marker = Marker(this)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "start"
        marker.position = startingPoint
        this.controller.setZoom(9.0)
        this.controller.setCenter(startingPoint)
        this.overlays.add(marker)
}

fun MapView.updateRoute(geoPoints: MutableList<GeoPoint>) {
    val polyline = Polyline()
    polyline.setPoints(geoPoints)
    this.controller.setCenter(geoPoints.last())
    this.overlays.add(polyline)
    this.invalidate()
}


fun getFormattedDate(date: Date): String {
    val formatter = SimpleDateFormat("d.M.yyyy", Locale.ENGLISH)
    return  formatter.format(date)
}

fun ProgressBar.setBigMax(max: Int) {
    this.max = max * 1000
}

fun ProgressBar.animateTo(progressTo: Int, startDelay: Long) {
    val animation = ObjectAnimator.ofInt(
        this,
        "progress",
        this.progress,
        progressTo * 1000
    )
    animation.duration = 800
    animation.interpolator = DecelerateInterpolator()
    animation.startDelay = startDelay
    animation.start()
}