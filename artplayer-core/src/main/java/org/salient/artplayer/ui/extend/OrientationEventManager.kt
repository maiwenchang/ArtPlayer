package org.salient.artplayer.ui.extend

import android.content.Context
import android.content.pm.ActivityInfo
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import android.view.OrientationEventListener
import org.salient.artplayer.extend.Utils
import org.salient.artplayer.ui.VideoView

/**
 * description: 重力感应方向管理器
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
class OrientationEventManager {
    private var currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    private var orientationListenerDelayTime: Long = 0
    private var isRotateLocked = 0 //0 代表方向锁定，1 代表没方向锁定
    private var mOrientationChangeListener: OnOrientationChangeListener? = null
    private var orientationEventListener: OrientationEventListener? = null //加速度传感器监听

    fun orientationDisable() {
        orientationEventListener?.disable()
    }

    fun orientationEnable(context: Context, videoView: VideoView, orientationChangeListener: OnOrientationChangeListener?) {
        mOrientationChangeListener = orientationChangeListener
        orientationEventListener = object : OrientationEventListener(context, 5) {
            // 加速度传感器监听，用于自动旋转屏幕
            override fun onOrientationChanged(orientation: Int) {
                Log.d(javaClass.name, "onOrientationChanged() called with orientation: $orientation");
                try {
                    //系统是否开启方向锁定
                    isRotateLocked = Settings.System.getInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION)
                } catch (e: SettingNotFoundException) {
                    e.printStackTrace()
                }
                if (isRotateLocked == 0) return  //方向被锁定，直接返回
                val operationDelay = System.currentTimeMillis() - orientationListenerDelayTime > 500
                if ((orientation >= 300 || orientation <= 30) && operationDelay) {
                    //屏幕顶部朝上
                    onOrientationPortrait(videoView)
                    orientationListenerDelayTime = System.currentTimeMillis()
                } else if (orientation in 260..280 && operationDelay) {
                    //屏幕左边朝上
                    onOrientationLandscape(videoView)
                    orientationListenerDelayTime = System.currentTimeMillis()
                } else if (orientation in 70..90 && operationDelay) {
                    //屏幕右边朝上
                    onOrientationReverseLandscape(videoView)
                    orientationListenerDelayTime = System.currentTimeMillis()
                }
            }
        }
        currentOrientation = Utils.getRequestedOrientation(context)
        orientationEventListener?.enable()
    }

    /**
     * 横屏(屏幕左边朝上)
     */
    private fun onOrientationLandscape(videoView: VideoView) {
        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) return
        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            return
        }
        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        mOrientationChangeListener?.onOrientationLandscape(videoView)
    }

    /**
     * 反向横屏(屏幕右边朝上)
     */
    private fun onOrientationReverseLandscape(videoView: VideoView) {
        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) return
        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            return
        }
        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        mOrientationChangeListener?.onOrientationReverseLandscape(videoView)
    }

    /**
     * 竖屏
     */
    private fun onOrientationPortrait(videoView: VideoView) {
        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return
        }
        if ((currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        || currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)) {
            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            return
        }
        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mOrientationChangeListener?.onOrientationPortrait(videoView)
    }

    /**
     * 设置重力感应横竖屏管理
     */
    fun setOnOrientationChangeListener(orientationChangeListener: OnOrientationChangeListener?) {
        mOrientationChangeListener = orientationChangeListener
    }

    interface OnOrientationChangeListener {
        fun onOrientationLandscape(videoView: VideoView?)
        fun onOrientationReverseLandscape(videoView: VideoView?)
        fun onOrientationPortrait(videoView: VideoView?)
    }
}