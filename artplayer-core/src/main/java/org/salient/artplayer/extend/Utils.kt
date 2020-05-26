package org.salient.artplayer.extend

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper

/**
 * description: 工具类
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
object Utils {

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
            getAppCompActivity(context)?.requestedOrientation ?: 0
        } else {
            scanForActivity(context)?.requestedOrientation ?: 0
        }
    }

    fun setRequestedOrientation(context: Context?, orientation: Int) {
        if (getAppCompActivity(context) != null) {
            getAppCompActivity(context)?.requestedOrientation = orientation
        } else {
            scanForActivity(context)?.requestedOrientation = orientation
        }
    }

    private fun getWindow(context: Context?): Window? {
        return if (getAppCompActivity(context) != null) {
            getAppCompActivity(context)?.window
        } else {
            scanForActivity(context)?.window
        }
    }

    @SuppressLint("RestrictedApi")
    fun hideSupportActionBar(context: Context?) {
        if (getAppCompActivity(context) != null) {
            val ab = getAppCompActivity(context)?.supportActionBar
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false)
                ab.hide()
            }
        }
        getWindow(context)?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    @SuppressLint("RestrictedApi")
    fun showSupportActionBar(context: Context?) {
        if (getAppCompActivity(context) != null) {
            val ab = getAppCompActivity(context)?.supportActionBar
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false)
                ab.show()
            }
        }
        getWindow(context)?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}