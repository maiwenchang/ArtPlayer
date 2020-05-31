package org.salient.artplayer.ui.extend

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.ui.VideoView


/**
 * description: 小窗的手势监听，支持单指拖动，双指缩放
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-22 10:06 PM.
 */
class TinyViewGestureListener(
        private val target: VideoView,
        var isMovable: Boolean = true, //是否可以拖动
        var isScalable: Boolean = true //是否可以缩放
) : SimpleOnGestureListener(), OnTouchListener {
    private var currentX = 0f
    private var currentY = 0f
    private var currentWidth = 0f
    private var currentHeight = 0f
    private var initDistance = 0f  //缩放时，两指间初始距离

    override fun onDown(e: MotionEvent): Boolean {
        currentX = target.x
        currentY = target.y
        currentWidth = target.width.toFloat()
        currentHeight = target.height.toFloat()
        initDistance = 0f
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        if (e2.pointerCount == 1) { //单指移动
            return moveWindow(target, e1, e2)
        } else if (e2.pointerCount == 2) { //双指缩放
            return zoomWindow(target, e1, e2)
        }
        return false
    }

    /**
     * Move the window according to the finger position
     * 根据手指移动窗口
     */
    private fun moveWindow(target: View, e1: MotionEvent, e2: MotionEvent): Boolean {
        if (!isMovable) return false
        val viewParent = target.parent as ViewGroup
        val parentWidth = viewParent.width
        val parentHeight = viewParent.height
        when (e2.action) {
            MotionEvent.ACTION_MOVE -> {
                var x = currentX + e2.rawX - e1.rawX
                var y = currentY + e2.rawY - e1.rawY
                if (x < 0) {
                    x = 0f
                }
                if (x > parentWidth - target.width) {
                    x = parentWidth - target.width.toFloat()
                }
                if (y < 0) {
                    y = 0f
                }
                if (y > parentHeight - target.height) {
                    y = parentHeight - target.height.toFloat()
                }
                target.y = y
                target.x = x
            }
        }
        return true
    }

    /**
     * Zoom window according to two fingers
     * @param e1 The first down motion event that started the scrolling.
     * @param e2 The move motion event that triggered the current onScroll.
     * 根据两个手指缩放窗口
     */
    private fun zoomWindow(target: View, e1: MotionEvent, e2: MotionEvent): Boolean {
        if (!isScalable) return false
        if (e2.pointerCount == 2 && e2.action == MotionEvent.ACTION_MOVE) {
            val x = e2.getX(0) - e2.getX(1)
            val y = e2.getY(0) - e2.getY(1)
            val value = Math.sqrt(x * x + y * y.toDouble()).toFloat() // 计算两点的距离
            if (initDistance == 0f) {
                initDistance = value
            } else if (Math.abs(value - initDistance) >= 2) {
                val scale = value / initDistance // 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                if (Math.abs(scale) > 0.05) {
                    val layoutParams = target.layoutParams
                    var height = currentHeight * scale
                    var width = currentWidth * scale
                    val WH = width / height
                    if (width < 400 * WH) {
                        width = 400 * WH
                    }
                    val viewParent = target.parent as ViewGroup
                    val parentWidth = viewParent.width
                    if (width > parentWidth) {
                        width = parentWidth.toFloat()
                    }
                    if (height < 400 / WH) {
                        height = 400 / WH
                    }
                    val parentHeight = viewParent.height
                    if (height > parentHeight) {
                        height = parentHeight.toFloat()
                    }
                    val parentWH = parentWidth / parentHeight
                    if (WH > parentWH) {
                        height = width / WH
                    } else {
                        width = height * WH
                    }
                    layoutParams.width = width.toInt()
                    layoutParams.height = height.toInt()
                    target.requestLayout()
                }
            }
        }
        return true
    }

    /**
     * revise position
     * 修正位置
     */
    private fun revisePosition(target: View) {
        var X = target.x
        var Y = target.y
        if (X < 0) {
            X = 0f
        }
        val viewParent = target.parent as ViewGroup
        val parentWidth = viewParent.width
        if (X > parentWidth - target.width) {
            X = parentWidth - target.width.toFloat()
        }
        if (Y < 0) {
            Y = 0f
        }
        val parentHeight = viewParent.height
        if (Y > parentHeight - target.height) {
            Y = parentHeight - target.height.toFloat()
        }
        target.y = Y
        target.x = X
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_UP) {
            revisePosition(target)
            initDistance = 0f
        }
        return false
    }


    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        target.performClick()
        return false
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
}