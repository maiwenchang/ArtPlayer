package org.salient.artvideoplayer

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics

/**
 * Created by Mai on 2018/5/18
 * *
 * Description:屏幕像素转换类
 * *
 */
object DensityUtil {
    /**
     * dip转像素
     */
    fun dip2px(context: Context, dip: Int): Int {
        val SCALE = context.resources.displayMetrics.density
        return (dip.toFloat() * SCALE + 0.5f).toInt()
    }

    /**
     * 像素转dip
     */
    fun px2dip(context: Context, Pixels: Int): Float {
        val SCALE = context.resources.displayMetrics.density
        return Pixels / SCALE
    }

    /**
     * 屏幕分辨率宽
     */
    fun getWindowWidth(activity: Activity): Int {
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    /**
     * 屏幕分辩类高
     */
    fun getWindowHeight(activity: Activity): Int {
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }

    /**
     * 屏幕的dpi
     */
    fun getDmDensityDpi(activity: Activity): Float {
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        return dm.density
    }
}