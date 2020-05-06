package org.salient.artplayer.ui

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import org.salient.artplayer.conduction.WindowType

/**
 * description: 视频播放视容器 - 小窗
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
class TinyVideoView(
        context: Context,
        val origin: VideoView? = null,
        val params: ViewGroup.LayoutParams? = null
) : VideoView(context) {

    init {
        tag = WindowType.TINY
        if (params != null) {
            layoutParams = params
        } else {
            layoutParams = LayoutParams(16 * 40, 9 * 40).apply {
                gravity = Gravity.BOTTOM or Gravity.RIGHT
                setMargins(30, 30, 30, 30)
            }
        }

    }

}