package org.salient.artplayer.extend

import android.content.Context
import android.content.pm.ActivityInfo
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.OrientationEventListener
import org.salient.artplayer.VideoViewOld

/**
 * Created by Mai on 2018/11/14
 * *
 * Description: 重力感应方向管理器
 * *
 */
object OrientationEventManager {
    private var currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    private var orientationListenerDelayTime: Long = 0
    private var isRotate = 0 //0 代表方向锁定，1 代表没方向锁定 = 0
    private var mOrientationChangeListener: OnOrientationChangeListener? = null
    /**
     * 加速度传感器监听
     */
    private var orientationEventListener: OrientationEventListener? = null

    fun orientationDisable() {
        if (orientationEventListener != null) {
            orientationEventListener!!.disable()
            orientationEventListener = null
        }
    }

    fun orientationEnable(context: Context, videoViewOld: VideoViewOld, orientationChangeListener: OnOrientationChangeListener?) {
        mOrientationChangeListener = orientationChangeListener
        orientationEventListener = object : OrientationEventListener(context, 5) {
            // 加速度传感器监听，用于自动旋转屏幕
            override fun onOrientationChanged(orientation: Int) { //Log.d(getClass().getSimpleName(), "onOrientationChanged: " + orientation);
                try { //系统是否开启方向锁定
                    isRotate = Settings.System.getInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION)
                } catch (e: SettingNotFoundException) {
                    e.printStackTrace()
                }
                if (isRotate == 0) return  //方向被锁定，直接返回
                if ((orientation >= 300 || orientation <= 30) && System.currentTimeMillis() - orientationListenerDelayTime > 1000) { //屏幕顶部朝上
                    onOrientationPortrait(videoViewOld)
                    orientationListenerDelayTime = System.currentTimeMillis()
                } else if (orientation >= 260 && orientation <= 280 && System.currentTimeMillis() - orientationListenerDelayTime > 1000) { //屏幕左边朝上
                    onOrientationLandscape(videoViewOld)
                    orientationListenerDelayTime = System.currentTimeMillis()
                } else if (orientation >= 70 && orientation <= 90 && System.currentTimeMillis() - orientationListenerDelayTime > 1000) { //屏幕右边朝上
                    onOrientationReverseLandscape(videoViewOld)
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
    private fun onOrientationLandscape(videoViewOld: VideoViewOld) {
//        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) return
//        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                && videoViewOld.windowType == VideoViewOld.WindowType.FULLSCREEN) {
//            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
//            return
//        }
//        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
//        if (videoViewOld.windowType != VideoViewOld.WindowType.FULLSCREEN) {
//            if (mOrientationChangeListener != null) {
//                mOrientationChangeListener!!.onOrientationLandscape(videoViewOld)
//            }
//        }
    }

    /**
     * 反向横屏(屏幕右边朝上)
     */
    private fun onOrientationReverseLandscape(videoViewOld: VideoViewOld) {
//        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) return
//        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                && videoViewOld.windowType == VideoViewOld.WindowType.FULLSCREEN) {
//            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
//            return
//        }
//        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
//        if (videoViewOld.windowType != VideoViewOld.WindowType.FULLSCREEN) {
//            if (mOrientationChangeListener != null) {
//                mOrientationChangeListener!!.onOrientationReverseLandscape(videoViewOld)
//            }
//        }
    }

    /**
     * 竖屏
     */
    private fun onOrientationPortrait(videoViewOld: VideoViewOld) {
//        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            return
//        }
//        if ((currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//                        || currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
//                && videoViewOld.windowType != VideoViewOld.WindowType.FULLSCREEN) {
//            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            return
//        }
//        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        if (mOrientationChangeListener != null) {
//            mOrientationChangeListener!!.onOrientationPortrait(videoViewOld)
//        }
    }

    /**
     * 设置重力感应横竖屏管理
     */
    fun setOnOrientationChangeListener(orientationChangeListener: OnOrientationChangeListener?) {
        mOrientationChangeListener = orientationChangeListener
    }

    interface OnOrientationChangeListener {
        fun onOrientationLandscape(videoViewOld: VideoViewOld?)
        fun onOrientationReverseLandscape(videoViewOld: VideoViewOld?)
        fun onOrientationPortrait(videoViewOld: VideoViewOld?)
    }
}