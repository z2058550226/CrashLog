package com.bybutter.crashlog

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.DropBoxManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.util.*

class MainActivity : AppCompatActivity() {
    private val mData = mutableListOf<DropBoxEntry>()
    private lateinit var stopRefresh: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(SwipeRefreshLayout(this).apply {
            addView(RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        outRect.bottom = 1f.dpInt
                    }
                })
            })
            setOnRefreshListener(::refresh)
            stopRefresh = { isRefreshing = false }
        })
        refresh()
    }

    private fun refresh() {
        if (granted(Manifest.permission.PACKAGE_USAGE_STATS, Manifest.permission.READ_LOGS)) {
            mData.clear()
            mData += getEntries()
            mAdapter.notifyDataSetChanged()
        } else {
            val cmd = "adb shell pm grant $packageName ${Manifest.permission.READ_LOGS}"

            val cmd2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                "adb shell pm grant $packageName ${Manifest.permission.PACKAGE_USAGE_STATS}"
            } else ""

            clip("$cmd\n\n$cmd2")
            Toast.makeText(this, "use adb to grant permission, the command is copied", Toast.LENGTH_SHORT).show()
        }
        stopRefresh()
    }

    private fun getEntries(): List<DropBoxEntry> {
        val resultList = mutableListOf<DropBoxEntry>()
        val dbm: DropBoxManager = getSystemService(Context.DROPBOX_SERVICE) as DropBoxManager

        fun getAllEntry(tag: String, msec: Long) {
            val entry = dbm.getNextEntry(tag, msec) ?: return
            val dropBoxEntry = DropBoxEntry(this, entry)
            resultList += dropBoxEntry
            getAllEntry(tag, entry.timeMillis)
        }

        mTags.forEach {
            getAllEntry(it, 0)
        }

        return resultList
    }

    private val mTags = listOf(
            // crash tags
            "system_server_crash",
            "system_server_watchdog",
            "system_app_crash",
            "data_app_crash",
            "system_app_native_crash",
            "data_app_native_crash",
            "SYSTEM_TOMBSTONE",
            // anr tags
            "system_server_anr",
            "system_app_anr",
            "data_app_anr",
    )

    private val mAdapter = object : RecyclerView.Adapter<CrashViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrashViewHolder {
            return CrashViewHolder(layoutInflater.inflate(R.layout.dropbox_entry_list_item, parent, false))
        }

        override fun onBindViewHolder(holder: CrashViewHolder, position: Int) {
            val entry = mData[position]

            val appIcon = entry.appIcon
            if (appIcon != null) {
                holder.ivAppIcon.setImageDrawable(appIcon)
            } else {
                holder.ivAppIcon.setImageResource(R.mipmap.ic_launcher)
            }

            val appLabel = entry.appLabel
            if (appLabel.isNullOrEmpty()) {
                holder.tvAppName.isVisible = false
            } else {
                holder.tvAppName.text = appLabel
                holder.tvAppName.isVisible = true
            }

            val packageName = entry.packageName
            holder.tvPkgName.text = packageName

            holder.tvTime.text = entry.timeMillis.formatTime()

            val stringBuffer = StringBuffer()
            val stringTokenizer = StringTokenizer(entry.tag, "_")
            while (stringTokenizer.hasMoreTokens()) {
                val nextToken = stringTokenizer.nextToken()
                if (!nextToken.isNullOrEmpty()) {
                    stringBuffer.append(nextToken.substring(0, 1).uppercase(Locale.getDefault()) + nextToken.substring(1).lowercase(Locale.getDefault()))
                    stringBuffer.append(" ")
                }
            }
            holder.tvTag.text = stringBuffer.toString().trim { it <= ' ' }

            holder.root.setOnClickListener {
                startActivity(Intent(this@MainActivity, EntryDetailActivity::class.java).apply {
                    putExtra(EntryDetailActivity.IK_PKG_NAME, packageName)
                    putExtra(EntryDetailActivity.IK_TEXT, entry.text)
                })
            }
        }

        override fun getItemCount(): Int = mData.size
    }

    class CrashViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
        val ivAppIcon: ImageView by lazy { root.findViewById(R.id.iv_app_icon) }
        val tvAppName: TextView by lazy { root.findViewById(R.id.app_name_txt) }
        val tvPkgName: TextView by lazy { root.findViewById(R.id.pkg_name_txt) }
        val tvTag: TextView by lazy { root.findViewById(R.id.tag_txt) }
        val tvTime: TextView by lazy { root.findViewById(R.id.time_txt) }
    }
}