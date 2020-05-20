package org.salient.artplayer.ui

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent
import android.widget.*
import org.salient.artplayer.AbsControlPanel
import org.salient.artplayer.MediaPlayerManager
import org.salient.artplayer.Utils
import org.salient.artplayer.VideoView
import org.salient.artplayer.ui.ControlPanel

/**
 * Created by Mai on 2018/7/10
 * *
 * Description: 视频播放器的控制面板，旨在展示与视频播放相关的核心功能；
 *
 * *
 */
class ControlPanel : AbsControlPanel {
    private val TAG = ControlPanel::class.java.simpleName
    private val autoDismissTime: Long = 3000
    private var mWhat = 0
    private var mExtra = 0
    protected var mGestureDetector: GestureDetector? = null
    private var start: ImageView? = null
    private var ivVolume: CheckBox? = null
    private var bottom_seek_progress: SeekBar? = null
    private var layout_bottom: View? = null
    private var layout_top: View? = null
    private var current: TextView? = null
    private var total: TextView? = null
    private var loading: ProgressBar? = null
    private var ivLeft: ImageView? = null
    private var video_cover: ImageView? = null
    private var ivRight: ImageView? = null
    private var llAlert: LinearLayout? = null
    private var tvAlert: TextView? = null
    private var tvConfirm: TextView? = null
    private var tvTitle: TextView? = null
    private var llOperation: LinearLayout? = null
    private var llProgressTime: LinearLayout? = null
    private var cbBottomPlay //底部播放按钮
            : CheckBox? = null
    private val mDismissTask: Runnable? = Runnable {
        if (MediaPlayerManager.instance().getCurrentVideoView() === mTarget && MediaPlayerManager.instance().isPlaying()) {
            hideUI(layout_bottom, layout_top, start)
        }
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    protected val resourceId: Int
        protected get() = R.layout.salient_layout_video_control_panel

    protected fun init(context: Context?) {
        super.init(context)
        start = findViewById(R.id.start)
        bottom_seek_progress = findViewById(R.id.bottom_seek_progress)
        layout_bottom = findViewById(R.id.layout_bottom)
        layout_top = findViewById(R.id.layout_top)
        current = findViewById(R.id.current)
        total = findViewById(R.id.total)
        ivVolume = findViewById(R.id.ivVolume)
        loading = findViewById(R.id.loading)
        ivLeft = findViewById(R.id.ivLeft)
        video_cover = findViewById(R.id.video_cover)
        llAlert = findViewById(R.id.llAlert)
        tvAlert = findViewById(R.id.tvAlert)
        tvConfirm = findViewById(R.id.tvConfirm)
        ivRight = findViewById(R.id.ivRight)
        tvTitle = findViewById(R.id.tvTitle)
        llOperation = findViewById(R.id.llOperation)
        llProgressTime = findViewById(R.id.llProgressTime)
        cbBottomPlay = findViewById(R.id.cbBottomPlay)
        ivRight!!.setOnClickListener(this)
        ivLeft!!.setOnClickListener(this)
        bottom_seek_progress!!.setOnSeekBarChangeListener(this)
        ivVolume!!.setOnClickListener(this)
        start!!.setOnClickListener(this)
        cbBottomPlay!!.setOnClickListener(this)
        setOnClickListener(object : OnClickListener() {
            fun onClick(v: View?) {
                if (mTarget == null) return
                if (!mTarget.isCurrentPlaying()) {
                    return
                }
                if (MediaPlayerManager.instance().getPlayerState() === MediaPlayerManager.PlayerState.PLAYING) {
                    cancelDismissTask()
                    if (layout_bottom!!.visibility != VISIBLE) {
                        showUI(layout_bottom, layout_top)
                    } else {
                        hideUI(layout_top, layout_bottom)
                    }
                    startDismissTask()
                }
            }
        })
        val videoGestureListener = VideoGestureListener(this)
        mGestureDetector = GestureDetector(getContext(), videoGestureListener)
        setOnTouchListener(object : OnTouchListener() {
            fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return if (mGestureDetector!!.onTouchEvent(event)) true else videoGestureListener.onTouch(v, event)
            }
        })
    }

    fun onStateError() {
        hideUI(start, layout_top, layout_bottom, loading)
        showUI(llAlert)
        //MediaPlayerManager.instance().releaseMediaPlayer();
        tvAlert!!.text = "oops~~ unknown error"
        tvConfirm!!.text = "retry"
        tvConfirm!!.setOnClickListener {
            if (mTarget != null) {
                hideUI(llAlert)
                mTarget.start()
            }
        }
    }

    fun onStateIdle() {
        hideUI(layout_bottom, layout_top, loading, llAlert)
        showUI(video_cover, start)
        cbBottomPlay!!.isChecked = false
        if (MediaPlayerManager.instance().isMute()) {
            ivVolume!!.isChecked = false
        } else {
            ivVolume!!.isChecked = true
        }
        if (mTarget != null && mTarget.getParentVideoView() != null && mTarget.getParentVideoView().getControlPanel() != null) {
            val title: TextView = mTarget.getParentVideoView().getControlPanel().findViewById(R.id.tvTitle)
            tvTitle!!.text = if (title.text == null) "" else title.text
        }
    }

    fun onStatePreparing() {
        showUI(loading)
    }

    fun onStatePrepared() {
        hideUI(loading)
    }

    fun onStatePlaying() {
        cbBottomPlay!!.isChecked = true
        showUI(layout_bottom, layout_top)
        hideUI(start, video_cover, loading, llOperation, llProgressTime, llAlert)
        startDismissTask()
    }

    fun onStatePaused() {
        cbBottomPlay!!.isChecked = false
        showUI(layout_bottom)
        hideUI(video_cover, loading, llOperation, llProgressTime)
    }

    fun onStatePlaybackCompleted() {
        cbBottomPlay!!.isChecked = false
        hideUI(layout_bottom, loading)
        showUI(start)
        if (mTarget.getWindowType() === VideoView.WindowType.FULLSCREEN || mTarget.getWindowType() === VideoView.WindowType.TINY) {
            showUI(layout_top)
        }
    }

    fun onSeekComplete() {}
    fun onBufferingUpdate(progress: Int) {
        if (progress != 0) bottom_seek_progress!!.secondaryProgress = progress
    }

    fun onInfo(what: Int, extra: Int) {
        mWhat = what
        mExtra = extra
    }

    fun onProgressUpdate(progress: Int, position: Long, duration: Long) {
        post(Runnable {
            bottom_seek_progress!!.progress = progress
            current.setText(Utils.stringForTime(position))
            total.setText(Utils.stringForTime(duration))
        })
    }

    fun onEnterSecondScreen() {
        if (mTarget != null && mTarget.getWindowType() === VideoView.WindowType.FULLSCREEN) {
            hideUI(ivRight)
        }
        showUI(ivLeft)
        SynchronizeViewState()
    }

    fun onExitSecondScreen() {
        if (mTarget != null && mTarget.getWindowType() !== VideoView.WindowType.TINY) {
            ivLeft!!.visibility = GONE
        }
        showUI(ivRight)
        SynchronizeViewState()
    }

    fun onStartTrackingTouch(seekBar: SeekBar?) {
        Log.i(TAG, "bottomProgress onStartTrackingTouch [" + this.hashCode().toString() + "] ")
        MediaPlayerManager.instance().cancelProgressTimer()
        cancelDismissTask()
        var vpdown: ViewParent? = getParent()
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true)
            vpdown = vpdown.parent
        }
    }

    fun onStopTrackingTouch(seekBar: SeekBar) {
        Log.i(TAG, "bottomProgress onStopTrackingTouch [" + this.hashCode().toString() + "] ")
        MediaPlayerManager.instance().startProgressTimer()
        startDismissTask()
        var vpup: ViewParent? = getParent()
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false)
            vpup = vpup.parent
        }
        if (MediaPlayerManager.instance().getPlayerState() !== MediaPlayerManager.PlayerState.PLAYING &&
                MediaPlayerManager.instance().getPlayerState() !== MediaPlayerManager.PlayerState.PAUSED) return
        val time = (seekBar.progress * 1.00 / 100 * MediaPlayerManager.instance().getDuration()) as Long
        MediaPlayerManager.instance().seekTo(time)
        Log.i(TAG, "seekTo " + time + " [" + this.hashCode() + "] ")
    }

    fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            val duration: Long = MediaPlayerManager.instance().getDuration()
            current.setText(Utils.stringForTime(progress / 100 * duration))
        }
    }

    //显示WiFi状态提醒
    fun showWifiAlert() {
        hideUI(start, layout_bottom, layout_top, loading)
        showUI(llAlert)
        tvAlert!!.text = "Is in non-WIFI"
        tvConfirm!!.text = "continue"
        tvConfirm!!.setOnClickListener {
            if (mTarget != null) {
                hideUI(llAlert)
                mTarget.start()
            }
        }
    }

    fun onClick(v: View) {
        cancelDismissTask()
        val id = v.id
        if (id == R.id.ivLeft) { //返回
            if (mTarget == null) return
            if (mTarget.getWindowType() === VideoView.WindowType.FULLSCREEN) {
                mTarget.exitFullscreen()
            } else if (mTarget.getWindowType() === VideoView.WindowType.TINY) {
                mTarget.exitTinyWindow()
            }
        } else if (id == R.id.ivRight) { //全屏
            enterFullScreen(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
        } else if (id == R.id.ivVolume) { //音量
            if (ivVolume!!.isChecked) {
                MediaPlayerManager.instance().setMute(false)
            } else {
                MediaPlayerManager.instance().setMute(true)
            }
        } else if (id == R.id.start) { //开始
            if (mTarget == null) {
                return
            }
            if (mTarget.isCurrentPlaying() && MediaPlayerManager.instance().isPlaying()) {
                return
            }
            if (!Utils.isNetConnected(getContext())) {
                onStateError()
                return
            }
            if (!Utils.isWifiConnected(getContext())) {
                showWifiAlert()
                return
            }
            mTarget.start()
        } else if (id == R.id.cbBottomPlay) { //暂停及恢复
            if (mTarget == null) {
                return
            }
            if (cbBottomPlay!!.isChecked) {
                if (mTarget.isCurrentPlaying() && MediaPlayerManager.instance().isPlaying()) {
                    return
                }
                if (!Utils.isNetConnected(getContext())) {
                    onStateError()
                    return
                }
                if (!Utils.isWifiConnected(getContext())) {
                    showWifiAlert()
                    return
                }
                mTarget.start()
            } else {
                mTarget.pause()
            }
        }
        startDismissTask()
    }

    fun enterFullScreen(screenOrientation: Int) {
        if (mTarget == null) return
        if (mTarget.getWindowType() !== VideoView.WindowType.FULLSCREEN) { //new VideoView
            val videoView = VideoView(getContext())
            //set parent
            videoView.setParentVideoView(mTarget)
            videoView.setUp(mTarget.getDataSourceObject(), VideoView.WindowType.FULLSCREEN, mTarget.getData())
            videoView.setControlPanel(ControlPanel(getContext()))
            //start fullscreen0
            videoView.startFullscreen(screenOrientation)
            //MediaPlayerManager.instance().startFullscreen(videoView, ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    fun onTouch(v: View?, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return false
    }

    //同步跟MediaPlayer状态无关的视图 //todo 如果这样的控件的数量很多，应该尝试寻找更好的实现方式
    fun SynchronizeViewState() {
        if (MediaPlayerManager.instance().isMute()) {
            ivVolume!!.isChecked = false
        } else {
            ivVolume!!.isChecked = true
        }
        if (MediaPlayerManager.instance().getPlayerState() !== MediaPlayerManager.PlayerState.PLAYING
                && MediaPlayerManager.instance().getPlayerState() !== MediaPlayerManager.PlayerState.PAUSED) {
            showUI(start)
        } else {
            hideUI(start)
        }
        if (mTarget != null && mTarget.getParentVideoView() != null && mTarget.getParentVideoView().getControlPanel() != null) {
            val title: TextView = mTarget.getParentVideoView().getControlPanel().findViewById(R.id.tvTitle)
            tvTitle!!.text = if (title.text == null) "" else title.text
        }
    }

    protected fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelDismissTask()
    }

    private fun startDismissTask() {
        cancelDismissTask()
        postDelayed(mDismissTask, autoDismissTime)
    }

    private fun cancelDismissTask() {
        val handler: Handler = getHandler()
        if (handler != null && mDismissTask != null) {
            handler.removeCallbacks(mDismissTask)
        }
    }
}