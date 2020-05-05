package org.salient.artplayer

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.view.Window
import org.salient.artplayer.extend.Utils
import org.salient.artplayer.ui.VideoView

/**
 * description: 视频播放器管理类
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
object MediaPlayerManager {

    fun startFullscreen(activity: Activity, videoView: VideoView) {
        Utils.hideSupportActionBar(activity)
        val decorView = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        val lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        decorView.addView(videoView, lp)
        Utils.setRequestedOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    }

    fun dismissFullscreen(activity: Activity, videoView: VideoView) {

    }
}