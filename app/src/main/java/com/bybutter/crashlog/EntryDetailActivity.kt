package com.bybutter.crashlog

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EntryDetailActivity : AppCompatActivity() {
    companion object {
        const val IK_PKG_NAME = "pkgName"
        const val IK_TEXT = "text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pkgName = intent.getStringExtra(IK_PKG_NAME) ?: ""
        val detailText = intent.getStringExtra(IK_TEXT) ?: "no detail"
        setContentView(ScrollView(this).apply {
            setBackgroundColor(-1)
            addView(TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(-1, -2)
                setTextColor(Color.BLACK)
                textSize = 13f
                val padding = (resources.displayMetrics.density * 4 + 0.5f).toInt()
                setPadding(padding, padding, padding, padding)
                text = detailText
                setOnClickListener {
                    clip(detailText)
                    Toast.makeText(context, "copied", Toast.LENGTH_LONG).show()
                }
            })
        })
    }
}