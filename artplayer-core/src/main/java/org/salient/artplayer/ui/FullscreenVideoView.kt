package org.salient.artplayer.ui

import android.content.Context
import android.view.GestureDetector
import android.view.ViewGroup
import org.salient.artplayer.conduction.WindowType
import org.salient.artplayer.ui.extend.FullscreenGestureListener

/**
 * description: 视频播放视容器 - 全屏
 * 特性：全屏播放，支持经典的亮度（左方），音量（右方），快进（下方）手势
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
open class FullscreenVideoView(context: Context,
                               override val origin: VideoView? = null,
                               final override val params: ViewGroup.LayoutParams? = null
) : VideoView(context), IFullscreenVideoView {

    var isVolumeGestureEnable: Boolean = true //是否开启音量手势
        set(value) {
            field = value
            gestureListener?.isVolumeGestureEnable = value
        }
    var isBrightnessGestureEnable: Boolean = true //是否开启亮度手势
        set(value) {
            field = value
            gestureListener?.isBrightnessGestureEnable = value
        }
    var isProgressGestureEnable: Boolean = true //是否开启进度拖动手势
        set(value) {
            field = value
            gestureListener?.isProgressGestureEnable = value
        }

    private val gestureListener: FullscreenGestureListener?

    init {
        tag = WindowType.FULLSCREEN
        layoutParams = params
                ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        gestureListener = FullscreenGestureListener(this, isVolumeGestureEnable, isBrightnessGestureEnable, isProgressGestureEnable)
        val gestureDetector = GestureDetector(getContext(), gestureListener)
        setOnTouchListener { v, event ->
            if (gestureDetector.onTouchEvent(event)) true
            else gestureListener.onTouch(v, event)
        }
    }


}