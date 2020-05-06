package org.salient.artplayer.ui

import android.content.Context
import org.salient.artplayer.conduction.WindowType

/**
 * description: 视频播放视容器 - 全屏
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