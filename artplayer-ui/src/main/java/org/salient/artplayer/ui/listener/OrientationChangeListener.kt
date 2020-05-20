package org.salient.artplayer.ui.listener

import OrientationEventManager.OnOrientationChangeListener
import android.content.pm.ActivityInfo
import org.salient.artplayer.AbsControlPanel
import org.salient.artplayer.OrientationEventManager
import org.salient.artplayer.Utils
import org.salient.artplayer.VideoView

/**
 * Created by Mai on 2018/11/14
 * *
 * Description: 处理重力感应横竖屏切换事件
 * *
 */
class OrientationChangeListener : OnOrientationChangeListener {
    fun onOrientationLandscape(videoView: VideoView?) { //横屏(屏幕左边朝上)
        if (videoView == null) return
        val controlPanel: AbsControlPanel = videoView.getControlPanel() ?: return
        controlPanel.enterFullScreen(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    }

    fun onOrientationReverseLandscape(videoView: VideoView?) { //反向横屏(屏幕右边朝上)
        if (videoView == null) return
        val controlPanel: AbsControlPanel = videoView.getControlPanel() ?: return
        controlPanel.enterFullScreen(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
        Utils.setRequestedOrientation(videoView.getContext(), ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    }

    fun onOrientationPortrait(videoView: VideoView) { //竖屏
        if (videoView.getWindowType() === VideoView.WindowType.FULLSCREEN) {
            videoView.exitFullscreen()
        }
    }
}