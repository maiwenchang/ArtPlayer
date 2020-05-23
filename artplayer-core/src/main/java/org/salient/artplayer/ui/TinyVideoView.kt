package org.salient.artplayer.ui

import android.content.Context
import android.view.GestureDetector
import android.view.Gravity
import android.view.ViewGroup
import org.salient.artplayer.conduction.WindowType
import org.salient.artplayer.ui.gesture.TinyViewGestureListener

/**
 * description: 视频播放视容器 - 小窗模式
 * 特性：单指拖动，双指缩放，双击暂停/恢复播放
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
class TinyVideoView(
        context: Context,
        override val origin: VideoView? = null,
        override val params: ViewGroup.LayoutParams? = null
) : VideoView(context), ITinyVideoView {

    /**
     * 是否可以拖动
     */
    override var isMovable: Boolean = true
    /**
     * 是否可以缩放
     */
    override var isScalable: Boolean = true

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

        val gestureListener = TinyViewGestureListener(this, isMovable, isScalable)
        val gestureDetector = GestureDetector(getContext(), gestureListener)
        setOnTouchListener { v, event ->
            if (gestureDetector.onTouchEvent(event)) true
            else gestureListener.onTouch(v, event)
        }
    }
}