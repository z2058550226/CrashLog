package com.bybutter.crashlog

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.granted(vararg permissions: String): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}