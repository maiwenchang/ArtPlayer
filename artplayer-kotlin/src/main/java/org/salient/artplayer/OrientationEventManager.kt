package org.salient.artplayer

import android.content.Context
import android.content.pm.ActivityInfo
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.OrientationEventListener

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

    fun orientationEnable(context: Context,videoView: VideoView, orientationChangeListener: OnOrientationChangeListener?) {
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
                    onOrientationPortrait(videoView)
                    orientationListenerDelayTime = System.currentTimeMillis()
                } else if (orientation >= 260 && orientation <= 280 && System.currentTimeMillis() - orientationListenerDelayTime > 1000) { //屏幕左边朝上
                    onOrientationLandscape(videoView)
                    orientationListenerDelayTime = System.currentTimeMillis()
                } else if (orientation >= 70 && orientation <= 90 && System.currentTimeMillis() - orientationListenerDelayTime > 1000) { //屏幕右边朝上
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
        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                && videoView.windowType == VideoView.WindowType.FULLSCREEN) {
            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            return
        }
        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        if (videoView.windowType != VideoView.WindowType.FULLSCREEN) {
            if (mOrientationChangeListener != null) {
                mOrientationChangeListener!!.onOrientationLandscape(videoView)
            }
        }
    }

    /**
     * 反向横屏(屏幕右边朝上)
     */
    private fun onOrientationReverseLandscape(videoView: VideoView) {
        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) return
        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                && videoView.windowType == VideoView.WindowType.FULLSCREEN) {
            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            return
        }
        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        if (videoView.windowType != VideoView.WindowType.FULLSCREEN) {
            if (mOrientationChangeListener != null) {
                mOrientationChangeListener!!.onOrientationReverseLandscape(videoView)
            }
        }
    }

    /**
     * 竖屏
     */
    private fun onOrientationPortrait(videoView: VideoView) {
        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return
        }
        if ((currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        || currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                && videoView.windowType != VideoView.WindowType.FULLSCREEN) {
            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            return
        }
        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (mOrientationChangeListener != null) {
            mOrientationChangeListener!!.onOrientationPortrait(videoView)
        }
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