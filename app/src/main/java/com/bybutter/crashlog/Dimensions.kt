package com.bybutter.crashlog

import android.content.res.Resources

val Float.dp get() = Resources.getSystem().displayMetrics.density * this
val Float.dpInt get() = (Resources.getSystem().displayMetrics.density * this + 0.5f).toInt()