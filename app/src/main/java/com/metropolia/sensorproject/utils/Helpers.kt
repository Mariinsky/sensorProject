package com.metropolia.sensorproject.utils

import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

fun compareDate(date: Date): Boolean {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    return formatter.parse(formatter.format(date)) == formatter.parse(formatter.format(Date()))
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