package com.bybutter.crashlog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun Context.clip(text: String) {
    val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(packageName, text))
}