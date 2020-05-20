package org.salient.artplayer.ui

import android.app.Activity
import android.app.Service
import android.media.AudioManager
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.*
import org.salient.artplayer.AbsControlPanel
import org.salient.artplayer.MediaPlayerManager
import org.salient.artplayer.Utils
import org.salient.artplayer.VideoView

/**
 * Created by Mai on 2018/8/7
 * *
 * Description:
 * *
 */
class VideoGestureListener : SimpleOnGestureListener, OnTouchListener {
    private var mControlPanel: AbsControlPanel? = null
    private var currentX = 0f
    private var currentY = 0f
    private var currentWidth = 0f
    private var currentHeight = 0f
    private var baseValue //缩放时，两指间初始距离 = 0f
    private var mVolume = -1 //当前声音
    private var mMaxVolume //最大声音 = 0
    private var mBrightness = -1f //当前亮度
    private var mAudioManager: AudioManager? = null
    var pbOperation //调节音量
            : ProgressBar? = null
    var imgOperation //
            : ImageView? = null
    var llOperation: LinearLayout? = null
    private var firstTouch //按住屏幕不放的第一次点击，则为true = false
    private var mChangePosition //判断是改变进度条则为true，否则为false = false
    private var mChangeBrightness //判断是不是改变亮度的操作 = false
    private var mChangeVolume //判断是不是改变音量的操作 = false
    private var seekBar: SeekBar? = null
    private var preDuration //手势调整进度条的滑动距离 = 0
    private var llProgressTime //展示手势滑动进度条的图层
            : LinearLayout? = null
    private var tvProgressTime //展示手势滑动改变多少的进度条
            : TextView? = null

    private constructor() {}
    constructor(controlPanel: AbsControlPanel?) {
        mControlPanel = controlPanel
        llOperation = mControlPanel.findViewById(R.id.llOperation)
        pbOperation = mControlPanel.findViewById(R.id.pbOperation)
        imgOperation = mControlPanel.findViewById(R.id.imgOperation)
        mAudioManager = mControlPanel.getContext().getSystemService(Service.AUDIO_SERVICE)
        mMaxVolume = mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        seekBar = mControlPanel.findViewById(R.id.bottom_seek_progress)
        llProgressTime = mControlPanel.findViewById(R.id.llProgressTime)
        tvProgressTime = mControlPanel.findViewById(R.id.tvProgressTime)
    }

    override fun onDown(e: MotionEvent): Boolean {
        val target: VideoView = mControlPanel.getTarget() ?: return false
        baseValue = 0f
        currentX = target.x
        currentY = target.y
        currentWidth = target.width.toFloat()
        currentHeight = target.height.toFloat()
        //取消隐藏音量和亮度的图层的操作
        llOperation!!.handler.removeCallbacks(runnable)
        firstTouch = true
        mChangePosition = false
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        mControlPanel.performClick()
        return false
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        val target: VideoView = mControlPanel.getTarget() ?: return false
        if (target.getWindowType() === org.salient.artplayer.ui.VideoView.WindowType.TINY) { //小窗
            if (e2.pointerCount == 1) { //单指移动
                return moveWindow(target, e1, e2)
            } else if (e2.pointerCount == 2) { //双指缩放
                return zoomWindow(target, e1, e2)
            }
        } else if (target.getWindowType() === org.salient.artplayer.ui.VideoView.WindowType.FULLSCREEN) { //全屏
            if (e2.pointerCount == 1) { //单指移动
                val mOldX = e1.x
                val mOldY = e1.y
                val x = e2.rawX.toInt()
                val y = e2.rawY.toInt()
                if (firstTouch) {
                    mChangePosition = Math.abs(distanceX) >= Math.abs(distanceY)
                    if (!mChangePosition) {
                        if (mOldX > currentWidth * 2.0 / 3) { //右边三分之一区域滑动
                            mChangeVolume = true
                        } else if (mOldX < currentWidth / 3.0) { //左边三分之一区域滑动
                            mChangeBrightness = true
                        }
                    }
                    firstTouch = false
                }
                if (mChangePosition) {
                    onSeekProgressControl(x - mOldX)
                } else if (mChangeBrightness) {
                    onBrightnessSlide((mOldY - y) * 2 / currentHeight)
                } else if (mChangeVolume) {
                    onVolumeSlide((mOldY - y) * 2 / currentHeight)
                }
                return true
            }
        }
        return false
    }

    /**
     * 滑动改变播放的快进快退
     *
     * @param seekDistance
     */
    private fun onSeekProgressControl(seekDistance: Float) {
        if (MediaPlayerManager.instance().getPlayerState() !== MediaPlayerManager.PlayerState.PLAYING &&
                MediaPlayerManager.instance().getPlayerState() !== MediaPlayerManager.PlayerState.PAUSED) return
        preDuration = seekBar!!.progress + (seekDistance / currentWidth * 30).toInt()
        if (preDuration > 100) {
            preDuration = 100
        } else if (preDuration < 0) {
            preDuration = 0
        }
        val time: Long = preDuration * MediaPlayerManager.instance().getDuration() / 100
        if (llProgressTime!!.visibility == View.GONE) {
            llProgressTime!!.visibility = View.VISIBLE
        }
        tvProgressTime.setText(Utils.stringForTime(time).toString() + "/" + Utils.stringForTime(MediaPlayerManager.instance().getDuration()))
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private fun onVolumeSlide(percent: Float) {
        pbOperation!!.max = mMaxVolume * 100
        if (mVolume == -1) {
            if (mVolume < 0) mVolume = 0
            mVolume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            // 显示
            imgOperation!!.setImageResource(R.drawable.salient_volume)
            llOperation!!.visibility = View.VISIBLE
        }
        var index = percent * mMaxVolume + mVolume
        if (index > mMaxVolume) index = mMaxVolume.toFloat() else if (index < 0) index = 0f
        // 变更声音
        mAudioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, index.toInt(), 0)
        // 变更进度条
        pbOperation!!.progress = (index * 100) as Int
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private fun onBrightnessSlide(percent: Float) {
        pbOperation!!.max = (1f * 100).toInt()
        if (mBrightness < 0) {
            mBrightness = (mControlPanel.getContext() as Activity).window.attributes.screenBrightness
            if (mBrightness <= 0.00f) mBrightness = 0.50f
            if (mBrightness < 0.01f) mBrightness = 0.01f
            // 显示
            imgOperation!!.setImageResource(R.drawable.salient_brightness)
            llOperation!!.visibility = View.VISIBLE
        }
        val lpa = (mControlPanel.getContext() as Activity).window.attributes
        lpa.screenBrightness = mBrightness + percent
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f
        }
        (mControlPanel.getContext() as Activity).window.attributes = lpa
        // 变更进度条
        pbOperation!!.progress = (lpa.screenBrightness * 100).toInt()
    }

    /**
     * Move the window according to the finger position
     * 根据手指移动窗口
     */
    private fun moveWindow(target: VideoView, e1: MotionEvent, e2: MotionEvent): Boolean {
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
    private fun zoomWindow(target: VideoView, e1: MotionEvent, e2: MotionEvent): Boolean {
        if (e2.pointerCount == 2 && e2.action == MotionEvent.ACTION_MOVE) {
            val x = e2.getX(0) - e2.getX(1)
            val y = e2.getY(0) - e2.getY(1)
            val value = Math.sqrt(x * x + y * y.toDouble()).toFloat() // 计算两点的距离
            if (baseValue == 0f) {
                baseValue = value
            } else if (Math.abs(value - baseValue) >= 2) {
                val scale = value / baseValue // 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
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
    private fun revisePosition(target: VideoView) {
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

    private val runnable = Runnable {
        llOperation!!.visibility = View.GONE
        llProgressTime!!.visibility = View.GONE
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_UP) { //音量变量清空，延迟隐藏声音控件
            mVolume = -1
            llOperation!!.postDelayed(runnable, 500)
            //亮度变量清空
            mBrightness = -1f
            if (mChangePosition) {
                if (seekBar != null) {
                    seekBar!!.progress = preDuration
                    mControlPanel.onStopTrackingTouch(seekBar)
                }
            }
            //拖动或缩放窗口后修正位置
            if (v is AbsControlPanel) {
                val absControlPanel: AbsControlPanel = v as AbsControlPanel
                val target: VideoView = absControlPanel.getTarget()
                if (target != null) {
                    val windowType: VideoView.WindowType = target.getWindowType()
                    if (windowType === VideoView.WindowType.TINY) {
                        revisePosition(target)
                    }
                }
            }
        }
        return false
    }

    override fun onDoubleTap(e: MotionEvent): Boolean { //双击播放或暂停
        if (mControlPanel.getTarget().isCurrentPlaying()) {
            if (MediaPlayerManager.instance().isPlaying()) {
                mControlPanel.getTarget().pause()
            } else {
                mControlPanel.getTarget().start()
            }
        }
        return true
    }
}