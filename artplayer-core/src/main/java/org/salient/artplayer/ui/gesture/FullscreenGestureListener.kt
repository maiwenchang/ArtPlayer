package org.salient.artplayer.ui.gesture

import android.util.Log
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.extend.Utils
import org.salient.artplayer.ui.VideoView
import kotlin.math.abs


/**
 * description: 全屏的手势监听，支持经典的亮度（左方），音量（右方），快进（下方）手势
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-22 10:06 PM.
 */
class FullscreenGestureListener(private val target: VideoView) : SimpleOnGestureListener(), View.OnTouchListener {
    private var currentX = 0f
    private var currentY = 0f
    private var currentWidth = 0f
    private var currentHeight = 0f

    private var isFirstTouch = false //按住屏幕不放的第一次点击，则为true
    private var isChangeProgress = false//判断是改变进度条则为true，否则为false
    private var isChangeBrightness = false //判断是不是改变亮度的操作
    private var isChangeVolume = false //判断是不是改变音量的操作

    override fun onDown(e: MotionEvent): Boolean {
        currentX = target.x
        currentY = target.y
        currentWidth = target.width.toFloat()
        currentHeight = target.height.toFloat()

        isFirstTouch = true
        isChangeProgress = false
        isChangeVolume = false
        isChangeBrightness = false
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return target.performClick()
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        //全屏
        if (e2.pointerCount == 1) { //单指移动
            val mOldX = e1.x
            val mOldY = e1.y
            val x = e2.rawX.toInt()
            val y = e2.rawY.toInt()
            if (isFirstTouch) {
                isChangeProgress = abs(distanceX) >= abs(distanceY)
                if (!isChangeProgress) {
                    if (mOldX > currentWidth * 2.0 / 3) { //右边三分之一区域滑动
                        isChangeVolume = true
                    } else if (mOldX < currentWidth / 3.0) { //左边三分之一区域滑动
                        isChangeBrightness = true
                    }
                }
                isFirstTouch = false
            }
            if (isChangeProgress) {
                onSeekProgressControl(((x - mOldX) * 0.1f / currentWidth).toInt())
            } else if (isChangeBrightness) {
                onBrightnessSlide((mOldY - y) * 0.1f / currentHeight)
            } else if (isChangeVolume) {
                onVolumeSlide((mOldY - y) * 0.1f / currentHeight)
            }
            return true
        }
        return false
    }

    /**
     * 滑动改变播放的快进快退
     *
     * @param seekDistance
     */
    private fun onSeekProgressControl(percent: Int) {
        Log.d(javaClass.simpleName, "seekDistance : $percent")
        val currentPosition = target.currentPosition
        val duration = target.duration
        var offset = currentPosition + percent
        offset = if (offset < 0) 0 else if (offset > duration) duration else offset
        Log.d(javaClass.simpleName, "offset : $offset")
        target.seekTo(offset)
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private fun onVolumeSlide(percent: Float) {
        val maxVolume = target.audioManager.getMaxVolume()
        val volume = target.audioManager.getVolume()
        var index: Float = volume + percent * maxVolume
        Log.d(javaClass.simpleName, "maxVolume: $maxVolume, volume: $volume index: index")
        index = if (index > maxVolume) maxVolume.toFloat() else if (index < 0) 0f else index
        Log.d(javaClass.simpleName, "index : $index")
        // 变更声音
        target.audioManager.setVolume(index.toInt())
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private fun onBrightnessSlide(percent: Float) {
        val activity = Utils.scanForActivity(target.context) ?: return
        val attributes = activity.window.attributes
        val currentBrightness = attributes.screenBrightness
        var brightness = currentBrightness + percent
        brightness = if (brightness > 1.0f) 1.0f else if (brightness < 0.1f) 0.1f else brightness
        Log.d(javaClass.simpleName, "percent : $percent, currentBrightness : $currentBrightness, brightness : $brightness")
        attributes.screenBrightness = brightness
        activity.window.attributes = attributes
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        //双击播放或暂停
        if (target.isPlaying) {
            target.pause()
        } else if (target.playerState.code > PlayerState.PREPARED.code) {
            target.start()
        }
        return true
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_UP) {
            isChangeProgress = false
            isChangeVolume = false
            isChangeBrightness = false
        }
        return false
    }
}