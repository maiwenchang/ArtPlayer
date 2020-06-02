package org.salient.artvideoplayer.activity

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_fullscreen.*
import kotlinx.android.synthetic.main.activity_tiny.*
import org.salient.artplayer.MediaPlayerManager
import org.salient.artplayer.player.SystemMediaPlayer
import org.salient.artplayer.ui.FullscreenVideoView
import org.salient.artplayer.ui.TinyVideoView
import org.salient.artplayer.ui.VideoView
import org.salient.artplayer.ui.extend.OrientationEventManager
import org.salient.artvideoplayer.BaseActivity
import org.salient.artvideoplayer.DensityUtil.getWindowHeight
import org.salient.artvideoplayer.DensityUtil.getWindowWidth
import org.salient.artvideoplayer.R

/**
 * description:全屏播放demo
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-20 09:06 AM.
 */
class FullscreenActivity : BaseActivity() {

    private val orientationEventManager = OrientationEventManager()
    private val orientationEventListener = object : OrientationEventManager.OnOrientationChangeListener {
        override fun onOrientationLandscape(videoView: VideoView?) {
            //横屏
            videoView?.let {
                MediaPlayerManager.startFullscreen(this@FullscreenActivity, it as FullscreenVideoView)
            }
        }

        override fun onOrientationReverseLandscape(videoView: VideoView?) {
            //反向横屏
            videoView?.let {
                MediaPlayerManager.startFullscreenReverse(this@FullscreenActivity, it as FullscreenVideoView)
            }
        }

        override fun onOrientationPortrait(videoView: VideoView?) {
            //竖屏
            videoView?.let {
                MediaPlayerManager.dismissFullscreen(this@FullscreenActivity)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.start -> {
                val fullScreenVideoView = FullscreenVideoView(context = this, origin = video_view).apply {
                    this.isVolumeGestureEnable = cb_volume_gesture_enable.isChecked
                    this.isBrightnessGestureEnable = cb_brightness_gesture_enable.isChecked
                    this.isProgressGestureEnable = cb_progress_gesture_enable.isChecked
                }
                val systemMediaPlayer = SystemMediaPlayer()
                systemMediaPlayer.setDataSource(this, Uri.parse(randomVideo?.url))
                fullScreenVideoView.mediaPlayer = systemMediaPlayer
                video_view.mediaPlayer = systemMediaPlayer
                if (cb_auto_orientate_enable.isChecked) {
                    orientationEventManager.orientationEnable(this, fullScreenVideoView, orientationEventListener)
                } else {
                    orientationEventManager.orientationDisable()
                }
                //开始播放
                fullScreenVideoView.prepare()
                MediaPlayerManager.startFullscreen(this, fullScreenVideoView)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}