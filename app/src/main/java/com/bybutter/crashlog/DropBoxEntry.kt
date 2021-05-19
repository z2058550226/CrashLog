package com.bybutter.crashlog

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.DropBoxManager
import java.io.BufferedReader
import java.io.StringReader

class DropBoxEntry(context: Context, entry: DropBoxManager.Entry) {
    val tag: String
    val timeMillis: Long
    val text: String

    var packageName: String? = null
    var appLabel: String? = null
    var appIcon: Drawable? = null

    init {
        tag = entry.tag
        timeMillis = entry.timeMillis
        text = entry.getText(Short.MAX_VALUE.toInt())

        val pm = context.packageManager
        val bufferedReader = BufferedReader(StringReader(text))

        while (true) {
            try {
                val readLine = bufferedReader.readLine()
                if (readLine != null) {
                    if ("Process:" in readLine) {
                        packageName = readLine.replace("Process:", "").trim()
                        break
                    }
                } else break
            } catch (e: Exception) {
            }
        }

        packageName?.let { pn ->
            val pm2 = context.packageManager
            val applicationInfo: ApplicationInfo? = try {
                pm2.getApplicationInfo(pn, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }

            appLabel = applicationInfo?.let { pm2.getApplicationLabel(it) }?.toString().orEmpty()

            appIcon = try {
                pm.getApplicationIcon(pn)
            } catch (e: Exception) {
                null
            }
        }
    }
}