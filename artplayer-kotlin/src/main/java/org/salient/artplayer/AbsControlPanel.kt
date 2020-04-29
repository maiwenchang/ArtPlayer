package org.salient.artplayer

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import org.salient.artplayer.MediaPlayerManager.PlayerState

/**
 * Created by Mai on 2018/7/10
 * *
 * Description:视频播放控制面板的基类
 * *
 */
abstract class AbsControlPanel : FrameLayout, MediaStateListener, View.OnClickListener, OnTouchListener, OnSeekBarChangeListener {
    var target: VideoView? = null

    constructor(context: Context?) : super(context!!) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        init(context)
    }

    protected abstract val resourceId: Int
    protected fun init(context: Context?) {
        View.inflate(context, resourceId, this)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }

    fun notifyStateChange() {
        when (MediaPlayerManager.playerState) {
            PlayerState.ERROR -> onStateError()
            PlayerState.IDLE -> onStateIdle()
            PlayerState.PAUSED -> onStatePaused()
            PlayerState.PLAYING -> onStatePlaying()
            PlayerState.PREPARED -> onStatePrepared()
            PlayerState.PREPARING -> onStatePreparing()
            PlayerState.PLAYBACK_COMPLETED -> onStatePlaybackCompleted()
        }
    }

    fun hideUI(vararg views: View?) {
        for (view in views) {
            if (view != null) {
                view.visibility = View.INVISIBLE
            }
        }
    }

    fun showUI(vararg views: View?) {
        for (view in views) {
            if (view != null) {
                view.visibility = View.VISIBLE
            }
        }
    }

    override fun onStateError() {}
    override fun onStateIdle() {}
    override fun onStatePreparing() {}
    override fun onStatePrepared() {}
    override fun onStatePlaying() {}
    override fun onStatePaused() {}
    override fun onStatePlaybackCompleted() {}
    override fun onSeekComplete() {}
    override fun onBufferingUpdate(progress: Int) {}
    override fun onInfo(what: Int, extra: Int) {}
    override fun onProgressUpdate(progress: Int, position: Long, duration: Long) {}
    override fun onEnterSecondScreen() {}
    override fun onExitSecondScreen() {}
    override fun onClick(v: View) {}
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return false
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    /**
     * 进入全屏
     * @param screenOrientation 屏幕方向
     */
    abstract fun enterFullScreen(screenOrientation: Int)
}