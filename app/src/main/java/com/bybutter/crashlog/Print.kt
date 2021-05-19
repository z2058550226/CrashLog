package com.bybutter.crashlog

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Long.formatTime(skeleton: String = "MM/dd hh:mm:ss"): String {
    return if (Build.VERSION.SDK_INT >= 24) SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Resources.getSystem().configuration.locales.get(0), skeleton)
    ).format(Date(this)) else SimpleDateFormat(skeleton).format(Date(this))
}