package org.salient.artplayer.ui

import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.Surface
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import org.salient.artplayer.audio.DefaultAudioManager
import org.salient.artplayer.audio.IAudioManager
import org.salient.artplayer.bean.VideoSize
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.conduction.WindowType
import org.salient.artplayer.extend.Utils
import org.salient.artplayer.player.IMediaPlayer
import org.salient.artplayer.player.SystemMediaPlayer

/**
 * description: 视频播放视容器
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
class FullscreenVideoView(context: Context, val origin: VideoView? = null) : VideoView(context) {

    init {
        tag = WindowType.FULLSCREEN
    }


}