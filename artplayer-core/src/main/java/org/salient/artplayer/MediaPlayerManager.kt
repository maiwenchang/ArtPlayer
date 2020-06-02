package org.salient.artplayer

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.view.Window
import org.salient.artplayer.conduction.WindowType
import org.salient.artplayer.extend.Utils
import org.salient.artplayer.ui.FullscreenVideoView
import org.salient.artplayer.ui.TinyVideoView
import org.salient.artplayer.ui.VideoView

/**
 * description: 视频播放器管理类
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
object MediaPlayerManager {

    /**
     * 拦截返回事件
     */
    fun blockBackPress(activity: Activity): Boolean {
        val decorView = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        val videoView = decorView.findViewWithTag<VideoView?>(WindowType.FULLSCREEN)
        videoView?.let {
            dismissFullscreen(activity)
            return true
        }
        return false
    }

    /**
     * 开启全屏
     */
    fun startFullscreen(activity: Activity, fullscreenVideoView: FullscreenVideoView, orientation: Int = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        Utils.hideSupportActionBar(activity)
        val decorView = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        decorView.findViewWithTag<VideoView?>(WindowType.FULLSCREEN)?.let {
            decorView.removeView(it)
        }
        decorView.addView(fullscreenVideoView, fullscreenVideoView.layoutParams)
        fullscreenVideoView.origin?.getBitmap()?.let {
            if (!fullscreenVideoView.isPlaying) {
                fullscreenVideoView.cover.setImageBitmap(it)
                fullscreenVideoView.cover.visibility = View.VISIBLE
            }
        }
        Utils.setRequestedOrientation(activity, orientation)
        Utils.setRequestedOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    }

    /**
     * 开启反向全屏
     */
    fun startFullscreenReverse(activity: Activity, fullscreenVideoView: FullscreenVideoView) {
        startFullscreen(activity, fullscreenVideoView, ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
    }

    /**
     * 取消全屏
     */
    fun dismissFullscreen(activity: Activity) {
        val decorView = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        val fullscreenVideoView = decorView.findViewWithTag<FullscreenVideoView?>(WindowType.FULLSCREEN)
        fullscreenVideoView?.let {
            it.origin?.attach()
            Utils.setRequestedOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            decorView.removeView(it)
            Utils.showSupportActionBar(activity)
        }
    }

    /**
     * 开启小窗
     */
    fun startTinyWindow(activity: Activity, tinyVideoView: TinyVideoView) {
        val decorView = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        decorView.findViewWithTag<VideoView?>(WindowType.TINY)?.let {
            decorView.removeView(it)
        }
        decorView.addView(tinyVideoView)
        tinyVideoView.origin?.getBitmap()?.let {
            if (!tinyVideoView.isPlaying) {
                tinyVideoView.cover.setImageBitmap(it)
                tinyVideoView.cover.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 取消小窗
     */
    fun dismissTinyWindow(activity: Activity) {
        val decorView = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        val tinyVideoView = decorView.findViewWithTag<TinyVideoView?>(WindowType.TINY)
        tinyVideoView?.let {
            it.origin?.attach()
            decorView.removeView(it)
        }
    }


}