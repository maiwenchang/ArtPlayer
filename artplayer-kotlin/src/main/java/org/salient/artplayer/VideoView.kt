package org.salient.artplayer

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import org.salient.artplayer.MediaPlayerManager.PlayerState

/**
 * Created by Mai on 2018/7/10
 * *
 * Description: 视频播放视图
 * *
 */
class VideoView : FrameLayout {
    private val TAG = VideoView::class.java.simpleName
    private val TEXTURE_VIEW_POSITION = 0 //视频播放视图层
    private val CONTROL_PANEL_POSITION = 1 //控制面板层
    var textureViewContainer: FrameLayout? = null
        private set
    var screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        private set
    // settable by the client
    var data: Any? = null //video data like id, title, cover picture...
    var dataSourceObject // video dataSource (Http url or Android assets file) would be posted to MediaPlayer.
            : Any? = null
    var headers //当前视频地址的请求头
            : Map<String?, String?>? = null
    private var mControlPanel: AbsControlPanel? = null
    var windowType = WindowType.NORMAL
    var detachedListener: OnWindowDetachedListener? = null
        private set
    var parentVideoView: VideoView? = null
    var comparator = Comparator { videoView ->
        val dataSource: Any = MediaPlayerManager.Companion.instance().getDataSource()
        dataSource != null && videoView != null && dataSource === videoView.dataSourceObject
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        textureViewContainer = FrameLayout(getContext())
        textureViewContainer!!.setBackgroundColor(Color.BLACK)
        val params = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        addView(textureViewContainer, TEXTURE_VIEW_POSITION, params)
        try {
            screenOrientation = (context as AppCompatActivity).requestedOrientation
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setUp(url: String?) {
        setUp(url, WindowType.NORMAL, null)
    }

    fun setUp(url: String?, data: Any?) {
        setUp(url, WindowType.NORMAL, data)
    }

    fun setUp(url: String?, windowType: WindowType) {
        setUp(url, windowType, null)
    }

    fun setUp(dataSourceObjects: Any?, windowType: WindowType, data: Any?) {
        dataSourceObject = dataSourceObjects
        this.windowType = windowType
        this.data = data
    }

    /**
     * 列表匹配模式
     */
    private fun autoMatch() {
        if (windowType != WindowType.LIST) {
            return
        }
        val currentVideoView: VideoView = MediaPlayerManager.Companion.instance().getCurrentVideoView()
        if (isCurrentPlaying) { //quit tiny window
            if (currentVideoView != null && currentVideoView.windowType == WindowType.TINY) { //play at this video view
                MediaPlayerManager.Companion.instance().playAt(this)
                //自动退出小窗
                MediaPlayerManager.Companion.instance().clearTinyLayout(context)
                if (mControlPanel != null) {
                    mControlPanel!!.onExitSecondScreen()
                    mControlPanel!!.notifyStateChange()
                }
            } else if (currentVideoView != null && currentVideoView.windowType == WindowType.FULLSCREEN) {
                if (mControlPanel != null) { //mControlPanel.onStateIdle();
                }
            } else { //play at this video view
                MediaPlayerManager.Companion.instance().playAt(this)
                if (mControlPanel != null) {
                    mControlPanel!!.notifyStateChange()
                }
            }
        } else if (currentVideoView === this) { // 该VideoView被复用了，设置了别的dataSource
            MediaPlayerManager.Companion.instance().removeTextureView()
            if (mControlPanel != null) {
                mControlPanel!!.onStateIdle()
            }
        } else {
            if (mControlPanel != null) {
                mControlPanel!!.onStateIdle()
            }
        }
    }

    /**
     * 退出全屏
     */
    fun exitFullscreen() {
        Utils.setRequestedOrientation(context, screenOrientation)
        MediaPlayerManager.Companion.instance().clearFullscreenLayout(context)
        Utils.showSupportActionBar(context)
        val parent = parentVideoView
        if (parent != null && parent.isCurrentPlaying) { //在常规窗口继续播放
            MediaPlayerManager.Companion.instance().playAt(parent)
            val controlPanel = parent.controlPanel
            if (controlPanel != null) {
                controlPanel.notifyStateChange()
                controlPanel.onExitSecondScreen()
            }
        } else { //直接开启的全屏，没有常规窗口
            MediaPlayerManager.Companion.instance().releasePlayerAndView(context)
        }
    }

    /**
     * 退出小窗
     */
    fun exitTinyWindow() {
        MediaPlayerManager.Companion.instance().clearTinyLayout(context)
        val parent = parentVideoView
        if (parent != null && parent.isCurrentPlaying) { //在常规窗口继续播放
            MediaPlayerManager.Companion.instance().playAt(parent)
            val controlPanel = parent.controlPanel
            if (controlPanel != null) {
                controlPanel.notifyStateChange()
                controlPanel.onExitSecondScreen()
            }
        } else { //直接开启的小屏，没有常规窗口
            MediaPlayerManager.Companion.instance().releasePlayerAndView(context)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        autoMatch()
    }

    /**
     * 开始播放
     */
    fun start() {
        Log.d(TAG, "start [" + this.hashCode() + "] ")
        if (dataSourceObject == null) {
            Log.w(TAG, "No Url")
            return
        }
        if (isCurrentPlaying) {
            when (MediaPlayerManager.Companion.instance().getPlayerState()) {
                PlayerState.IDLE, PlayerState.ERROR -> play()
                PlayerState.PLAYBACK_COMPLETED -> {
                    MediaPlayerManager.Companion.instance().seekTo(0)
                    MediaPlayerManager.Companion.instance().start()
                }
                PlayerState.PREPARED, PlayerState.PAUSED -> MediaPlayerManager.Companion.instance().start()
            }
        } else {
            play()
        }
    }

    protected fun play() {
        Log.d(TAG, "play [" + hashCode() + "] ")
        //check data source
        if (dataSourceObject == null) {
            return
        }
        //get context
        val context = context
        //clear videoView opened before
        val currentVideoView: VideoView = MediaPlayerManager.Companion.instance().getCurrentVideoView()
        if (currentVideoView != null && currentVideoView !== this) {
            if (windowType != WindowType.TINY) {
                MediaPlayerManager.Companion.instance().clearTinyLayout(context)
            } else if (windowType != WindowType.FULLSCREEN) {
                MediaPlayerManager.Companion.instance().clearFullscreenLayout(context)
            }
        }
        // releaseMediaPlayer
        MediaPlayerManager.Companion.instance().releaseMediaPlayer()
        //pass data to MediaPlayer
        MediaPlayerManager.Companion.instance().setDataSource(dataSourceObject, headers)
        MediaPlayerManager.Companion.instance().setCurrentData(data)
        //keep screen on
        Utils.scanForActivity(context)!!.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //bind {@link AudioManager#OnAudioFocusChangeListener}
        MediaPlayerManager.Companion.instance().bindAudioFocus(context)
        //bind OrientationEventManager
        MediaPlayerManager.Companion.instance().bindOrientationManager(context)
        //init TextureView, we will prepare and start the player when surfaceTextureAvailable.
        MediaPlayerManager.Companion.instance().initTextureView(context)
        MediaPlayerManager.Companion.instance().addTextureView(this)
    }

    /**
     * 暂停
     */
    fun pause() {
        if (isCurrentPlaying) {
            if (MediaPlayerManager.Companion.instance().getPlayerState() == PlayerState.PLAYING) {
                Log.d(TAG, "pause [" + this.hashCode() + "] ")
                MediaPlayerManager.Companion.instance().pause()
            }
        }
    }

    /**
     * 设置控制面板
     *
     * @param mControlPanel AbsControlPanel
     */
    var controlPanel: AbsControlPanel?
        get() = mControlPanel
        set(mControlPanel) {
            if (mControlPanel != null) {
                mControlPanel.target = this
                val parent = mControlPanel.parent
                if (parent != null) {
                    (parent as ViewGroup).removeView(mControlPanel)
                }
            }
            this.mControlPanel = mControlPanel
            val child = getChildAt(CONTROL_PANEL_POSITION)
            if (child != null) {
                removeViewAt(CONTROL_PANEL_POSITION)
            }
            addView(this.mControlPanel, CONTROL_PANEL_POSITION)
            if (this.mControlPanel != null) {
                this.mControlPanel!!.onStateIdle()
            }
        }

    fun setOnWindowDetachedListener(mDetachedListener: OnWindowDetachedListener?) {
        detachedListener = mDetachedListener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (detachedListener != null) {
            if (isCurrentPlaying && this === MediaPlayerManager.Companion.instance().getCurrentVideoView()) {
                detachedListener!!.onDetached(this)
            }
        }
    }

    override fun equals(obj: Any?): Boolean {
        return obj is VideoView && comparator.compare(this)
    }

    /**
     * 判断VideoView 与 正在播放的多媒体资源是否匹配;
     * 匹配规则可以通过[VideoView.setComparator] 设置;
     * 默认比较[VideoView.dataSourceObject] 和 [AbsMediaPlayer.dataSource]
     * See[this.comparator]
     *
     * @return VideoView
     */
    val isCurrentPlaying: Boolean
        get() = comparator.compare(this)

    /**
     * 进入全屏模式
     *
     *
     * 注意：这里把一个VideoView动态添加到[Window.ID_ANDROID_CONTENT]所指的View中
     *
     * @param screenOrientation like [android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE]
     */
    fun startFullscreen(screenOrientation: Int) {
        check(parent == null) {
            "The specified VideoView already has a parent. " +
                    "You must call removeView() on the VideoView's parent first."
        }
        val context = context
        windowType = WindowType.FULLSCREEN
        Utils.hideSupportActionBar(context)
        // add to window
        val vp = Utils.scanForActivity(context)!!.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        val old = vp.findViewById<View>(R.id.salient_video_fullscreen_id)
        if (old != null) {
            vp.removeView(old)
        }
        id = R.id.salient_video_fullscreen_id
        val lp = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        vp.addView(this, lp)
        //add TextureView
        MediaPlayerManager.Companion.instance().removeTextureView()
        MediaPlayerManager.Companion.instance().addTextureView(this)
        //update ControlPanel State
        val controlPanel = controlPanel
        controlPanel?.onEnterSecondScreen()
        //update Parent ControlPanel State
        val parentVideoView = parentVideoView
        if (parentVideoView != null) {
            val parentControlPanel = parentVideoView.controlPanel
            parentControlPanel?.onEnterSecondScreen()
        }
        //Rotate window an enter fullscreen
        systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
        } else {
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        Utils.setRequestedOrientation(context, screenOrientation)
        MediaPlayerManager.Companion.instance().updateState(MediaPlayerManager.Companion.instance().getPlayerState())
    }

    /**
     * 进入小屏模式
     *
     *
     * 注意：这里把一个VideoView动态添加到[Window.ID_ANDROID_CONTENT]所指的View中
     */
    fun startTinyWindow() {
        val layoutParams = LayoutParams(16 * 40, 9 * 40)
        layoutParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        layoutParams.setMargins(0, 0, 30, 100)
        startTinyWindow(layoutParams)
    }

    /**
     * 进入小屏模式
     *
     *
     * 注意：这里把一个VideoView动态添加到[Window.ID_ANDROID_CONTENT]所指的View中
     */
    fun startTinyWindow(lp: LayoutParams?) {
        Log.i(TAG, "startWindowTiny " + " [" + this.hashCode() + "] ")
        check(parent == null) {
            "The specified VideoView already has a parent. " +
                    "You must call removeView() on the VideoView's parent first."
        }
        val context = context
        windowType = WindowType.TINY
        // add to window
        val vp = Utils.scanForActivity(context)!!.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        val old = vp.findViewById<View>(R.id.salient_video_tiny_id)
        if (old != null) {
            vp.removeView(old)
        }
        id = R.id.salient_video_tiny_id
        if (lp != null) {
            vp.addView(this, lp)
        } else {
            vp.addView(this)
        }
        //add TextureView
        MediaPlayerManager.Companion.instance().removeTextureView()
        MediaPlayerManager.Companion.instance().addTextureView(this)
        //update ControlPanel State
        val controlPanel = controlPanel
        controlPanel?.onEnterSecondScreen()
        //update Parent ControlPanel State
        val parentVideoView = parentVideoView
        if (parentVideoView != null) {
            val parentControlPanel = parentVideoView.controlPanel
            parentControlPanel?.onEnterSecondScreen()
        }
        MediaPlayerManager.Companion.instance().updateState(MediaPlayerManager.Companion.instance().getPlayerState())
    }

    enum class WindowType {
        NORMAL, LIST, FULLSCREEN, TINY
    }
}