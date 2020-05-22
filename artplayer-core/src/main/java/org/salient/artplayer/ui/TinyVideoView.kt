package org.salient.artplayer.ui

import android.content.Context
import android.view.GestureDetector
import android.view.Gravity
import android.view.ViewGroup
import org.salient.artplayer.conduction.WindowType
import org.salient.artplayer.ui.gesture.TinyWindowGestureListener

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
                gravity = Gravity.BOTTOM or Gravity.END
                setMargins(30, 30, 30, 30)
            }
        }

        val gestureListener = TinyWindowGestureListener(this)
        val mGestureDetector = GestureDetector(getContext(), gestureListener)
        setOnTouchListener { v, event ->
            if (mGestureDetector.onTouchEvent(event)) true
            else gestureListener.onTouch(v, event)
        }
    }

}