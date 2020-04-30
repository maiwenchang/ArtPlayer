package org.salient.artplayer.extend

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import java.util.*

/**
 * Created by Mai on 2018/7/10
 * *
 * Description:
 * *
 */
object Utils {
    fun stringForTime(timeMs: Long): String {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00"
        }
        val totalSeconds = timeMs / 1000
        val seconds = (totalSeconds % 60).toInt()
        val minutes = (totalSeconds / 60 % 60).toInt()
        val hours = (totalSeconds / 3600).toInt()
        val stringBuilder = StringBuilder()
        val mFormatter = Formatter(stringBuilder, Locale.getDefault())
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    fun isNetConnected(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.activeNetworkInfo
            if (info != null && info.isConnected) {
                return info.state == NetworkInfo.State.CONNECTED
            }
        }
        return false
    }

    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return wifiNetworkInfo.isConnected
        }
        return false
    }

    /**
     * Get activity from context object
     *
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    fun scanForActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return scanForActivity(context.baseContext)
        }
        return null
    }

    /**
     * Get AppCompatActivity from context
     *
     * @param context context
     * @return AppCompatActivity if it's not null
     */
    fun getAppCompActivity(context: Context?): AppCompatActivity? {
        if (context == null) return null
        if (context is AppCompatActivity) {
            return context
        } else if (context is ContextThemeWrapper) {
            return getAppCompActivity(context.baseContext)
        }
        return null
    }

    fun getRequestedOrientation(context: Context?): Int {
        return if (getAppCompActivity(context) != null) {
            getAppCompActivity(context)!!.requestedOrientation
        } else {
            scanForActivity(context)!!.requestedOrientation
        }
    }

    fun setRequestedOrientation(context: Context?, orientation: Int) {
        if (getAppCompActivity(context) != null) {
            getAppCompActivity(context)!!.requestedOrientation = orientation
        } else {
            scanForActivity(context)!!.requestedOrientation = orientation
        }
    }

    fun getWindow(context: Context?): Window {
        return if (getAppCompActivity(context) != null) {
            getAppCompActivity(context)!!.window
        } else {
            scanForActivity(context)!!.window
        }
    }

    @SuppressLint("RestrictedApi")
    fun hideSupportActionBar(context: Context?) {
        if (getAppCompActivity(context) != null) {
            val ab = getAppCompActivity(context)!!.supportActionBar
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false)
                ab.hide()
            }
        }
        getWindow(context).setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    @SuppressLint("RestrictedApi")
    fun showSupportActionBar(context: Context?) {
        if (getAppCompActivity(context) != null) {
            val ab = getAppCompActivity(context)!!.supportActionBar
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false)
                ab.show()
            }
        }
        getWindow(context).clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}