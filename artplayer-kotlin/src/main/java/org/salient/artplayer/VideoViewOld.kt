package org.salient.artplayer


/**
 * Created by Mai on 2018/7/10
 * *
 * Description: 视频播放视图
 * *
 */
class VideoViewOld {
//    private val TAG = VideoViewOld::class.java.simpleName
//    private val TEXTURE_VIEW_POSITION = 0 //视频播放视图层
//    private val CONTROL_PANEL_POSITION = 1 //控制面板层
//    var textureViewContainer: FrameLayout? = null
//        private set
//    var screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        private set
//    // settable by the client
//    var data: Any? = null //video data like id, title, cover picture...
//    var dataSourceObject: Any? = null// video dataSource (Http url or Android assets file) would be posted to MediaPlayer.
//
//    //当前视频地址的请求头
//    var headers: Map<String, String>? = null
//    private var mControlPanel: AbsControlPanel? = null
//    var windowType = WindowType.NORMAL
//    var detachedListener: OnWindowDetachedListener? = null
//        private set
//    var parentVideoViewOld: VideoViewOld? = null
//
//    var comparator = object : Comparator {
//        override fun compare(videoViewOld: VideoViewOld?): Boolean {
//            val dataSource = MediaPlayerManagerOld.dataSource
//            return dataSource != null && videoViewOld != null && dataSource === videoViewOld.dataSourceObject
//        }
//    }
//
//
//    constructor(context: Context) : super(context) {
//        init(context)
//    }
//
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
//        init(context)
//    }
//
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        init(context)
//    }
//
//    private fun init(context: Context) {
//        textureViewContainer = FrameLayout(getContext())
//        textureViewContainer!!.setBackgroundColor(Color.BLACK)
//        val params = LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT)
//        addView(textureViewContainer, TEXTURE_VIEW_POSITION, params)
//        try {
//            screenOrientation = (context as AppCompatActivity).requestedOrientation
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun setUp(url: String?) {
//        setUp(url, WindowType.NORMAL, null)
//    }
//
//    fun setUp(url: String?, data: Any?) {
//        setUp(url, WindowType.NORMAL, data)
//    }
//
//    fun setUp(url: String?, windowType: WindowType) {
//        setUp(url, windowType, null)
//    }
//
//    fun setUp(dataSourceObjects: Any?, windowType: WindowType, data: Any?) {
//        dataSourceObject = dataSourceObjects
//        this.windowType = windowType
//        this.data = data
//    }
//
//    /**
//     * 列表匹配模式
//     */
//    private fun autoMatch() {
//        if (windowType != WindowType.LIST) {
//            return
//        }
//        val currentVideoView = MediaPlayerManagerOld.currentVideoViewOld
//        if (isCurrentPlaying) { //quit tiny window
//            if (currentVideoView != null && currentVideoView.windowType == WindowType.TINY) { //play at this video view
//                MediaPlayerManagerOld.playAt(this)
//                //自动退出小窗
//                MediaPlayerManagerOld.clearTinyLayout(context)
//                if (mControlPanel != null) {
//                    mControlPanel!!.onExitSecondScreen()
//                    mControlPanel!!.notifyStateChange()
//                }
//            } else if (currentVideoView != null && currentVideoView.windowType == WindowType.FULLSCREEN) {
//                if (mControlPanel != null) { //mControlPanel.onStateIdle();
//                }
//            } else { //play at this video view
//                MediaPlayerManagerOld.playAt(this)
//                if (mControlPanel != null) {
//                    mControlPanel!!.notifyStateChange()
//                }
//            }
//        } else if (currentVideoView === this) { // 该VideoView被复用了，设置了别的dataSource
//            MediaPlayerManagerOld.removeTextureView()
//            if (mControlPanel != null) {
//                mControlPanel!!.onStateIdle()
//            }
//        } else {
//            if (mControlPanel != null) {
//                mControlPanel!!.onStateIdle()
//            }
//        }
//    }
//
//    /**
//     * 退出全屏
//     */
//    fun exitFullscreen() {
//        Utils.setRequestedOrientation(context, screenOrientation)
//        MediaPlayerManagerOld.clearFullscreenLayout(context)
//        Utils.showSupportActionBar(context)
//        val parent = parentVideoViewOld
//        if (parent != null && parent.isCurrentPlaying) { //在常规窗口继续播放
//            MediaPlayerManagerOld.playAt(parent)
//            val controlPanel = parent.controlPanel
//            if (controlPanel != null) {
//                controlPanel.notifyStateChange()
//                controlPanel.onExitSecondScreen()
//            }
//        } else { //直接开启的全屏，没有常规窗口
//            MediaPlayerManagerOld.releasePlayerAndView(context)
//        }
//    }
//
//    /**
//     * 退出小窗
//     */
//    fun exitTinyWindow() {
//        MediaPlayerManagerOld.clearTinyLayout(context)
//        val parent = parentVideoViewOld
//        if (parent != null && parent.isCurrentPlaying) { //在常规窗口继续播放
//            MediaPlayerManagerOld.playAt(parent)
//            val controlPanel = parent.controlPanel
//            if (controlPanel != null) {
//                controlPanel.notifyStateChange()
//                controlPanel.onExitSecondScreen()
//            }
//        } else { //直接开启的小屏，没有常规窗口
//            MediaPlayerManagerOld.releasePlayerAndView(context)
//        }
//    }
//
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        autoMatch()
//    }
//
//    /**
//     * 开始播放
//     */
//    fun start() {
//        Log.d(TAG, "start [" + this.hashCode() + "] ")
//        if (dataSourceObject == null) {
//            Log.w(TAG, "No Url")
//            return
//        }
//        if (isCurrentPlaying) {
//            when (MediaPlayerManagerOld.playerState) {
//                PlayerState.IDLE, PlayerState.ERROR -> play()
//                PlayerState.PLAYBACK_COMPLETED -> {
//                    MediaPlayerManagerOld.seekTo(0)
//                    MediaPlayerManagerOld.start()
//                }
//                PlayerState.PREPARED, PlayerState.PAUSED -> MediaPlayerManagerOld.start()
//            }
//        } else {
//            play()
//        }
//    }
//
//    protected fun play() {
//        Log.d(TAG, "play [" + hashCode() + "] ")
//        //check data source
//        if (dataSourceObject == null) {
//            return
//        }
//        //get context
//        val context = context
//        //clear videoView opened before
//        val currentVideoView = MediaPlayerManagerOld.currentVideoViewOld
//        if (currentVideoView != null && currentVideoView !== this) {
//            if (windowType != WindowType.TINY) {
//                MediaPlayerManagerOld.clearTinyLayout(context)
//            } else if (windowType != WindowType.FULLSCREEN) {
//                MediaPlayerManagerOld.clearFullscreenLayout(context)
//            }
//        }
//        // releaseMediaPlayer
//        MediaPlayerManagerOld.releaseMediaPlayer()
//        //pass data to MediaPlayer
//        MediaPlayerManagerOld.setDataSource(dataSourceObject, headers)
//        MediaPlayerManagerOld.currentData = data
//        //keep screen on
//        Utils.scanForActivity(context)!!.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        //bind {@link AudioManager#OnAudioFocusChangeListener}
//        MediaPlayerManagerOld.bindAudioFocus(context)
//        //bind OrientationEventManager
//        MediaPlayerManagerOld.bindOrientationManager(context, this)
//        //init TextureView, we will prepare and start the player when surfaceTextureAvailable.
//        MediaPlayerManagerOld.initTextureView(context)
//        MediaPlayerManagerOld.addTextureView(this)
//    }
//
//    /**
//     * 暂停
//     */
//    fun pause() {
//        if (isCurrentPlaying) {
//            if (MediaPlayerManagerOld.playerState == PlayerState.PLAYING) {
//                Log.d(TAG, "pause [" + this.hashCode() + "] ")
//                MediaPlayerManagerOld.pause()
//            }
//        }
//    }
//
//    /**
//     * 设置控制面板
//     *
//     * @param mControlPanel AbsControlPanel
//     */
//    var controlPanel: AbsControlPanel?
//        get() = mControlPanel
//        set(mControlPanel) {
//            if (mControlPanel != null) {
//                mControlPanel.target = this
//                val parent = mControlPanel.parent
//                if (parent != null) {
//                    (parent as ViewGroup).removeView(mControlPanel)
//                }
//            }
//            this.mControlPanel = mControlPanel
//            val child = getChildAt(CONTROL_PANEL_POSITION)
//            if (child != null) {
//                removeViewAt(CONTROL_PANEL_POSITION)
//            }
//            addView(this.mControlPanel, CONTROL_PANEL_POSITION)
//            if (this.mControlPanel != null) {
//                this.mControlPanel!!.onStateIdle()
//            }
//        }
//
//    fun setOnWindowDetachedListener(mDetachedListener: OnWindowDetachedListener?) {
//        detachedListener = mDetachedListener
//    }
//
//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//        if (detachedListener != null) {
//            if (isCurrentPlaying && this === MediaPlayerManagerOld.currentVideoViewOld) {
//                detachedListener!!.onDetached(this)
//            }
//        }
//    }
//
//    override fun equals(obj: Any?): Boolean {
//        return obj is VideoViewOld && comparator.compare(this)
//    }
//
//    /**
//     * 判断VideoView 与 正在播放的多媒体资源是否匹配;
//     * 匹配规则可以通过[VideoViewOld.setComparator] 设置;
//     * 默认比较[VideoViewOld.dataSourceObject] 和 [AbsMediaPlayer.dataSource]
//     * See[this.comparator]
//     *
//     * @return VideoView
//     */
//    val isCurrentPlaying: Boolean
//        get() = comparator.compare(this)
//
//    /**
//     * 进入全屏模式
//     *
//     *
//     * 注意：这里把一个VideoView动态添加到[Window.ID_ANDROID_CONTENT]所指的View中
//     *
//     * @param screenOrientation like [android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE]
//     */
//    fun startFullscreen(screenOrientation: Int) {
//        check(parent == null) {
//            "The specified VideoView already has a parent. " +
//                    "You must call removeView() on the VideoView's parent first."
//        }
//        val context = context
//        windowType = WindowType.FULLSCREEN
//        Utils.hideSupportActionBar(context)
//        // add to window
//        val vp = Utils.scanForActivity(context)!!.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
//        val old = vp.findViewById<View>(R.id.salient_video_fullscreen_id)
//        if (old != null) {
//            vp.removeView(old)
//        }
//        id = R.id.salient_video_fullscreen_id
//        val lp = LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        vp.addView(this, lp)
//        //add TextureView
//        MediaPlayerManagerOld.removeTextureView()
//        MediaPlayerManagerOld.addTextureView(this)
//        //update ControlPanel State
//        val controlPanel = controlPanel
//        controlPanel?.onEnterSecondScreen()
//        //update Parent ControlPanel State
//        val parentVideoView = parentVideoViewOld
//        if (parentVideoView != null) {
//            val parentControlPanel = parentVideoView.controlPanel
//            parentControlPanel?.onEnterSecondScreen()
//        }
//        //Rotate window an enter fullscreen
//        systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
//        } else {
//            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
//        }
//        Utils.setRequestedOrientation(context, screenOrientation)
//        MediaPlayerManagerOld.updateState(MediaPlayerManagerOld.playerState)
//    }
//
//    /**
//     * 进入小屏模式
//     *
//     *
//     * 注意：这里把一个VideoView动态添加到[Window.ID_ANDROID_CONTENT]所指的View中
//     */
//    fun startTinyWindow() {
//        val layoutParams = LayoutParams(16 * 40, 9 * 40)
//        layoutParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
//        layoutParams.setMargins(0, 0, 30, 100)
//        startTinyWindow(layoutParams)
//    }
//
//    /**
//     * 进入小屏模式
//     *
//     *
//     * 注意：这里把一个VideoView动态添加到[Window.ID_ANDROID_CONTENT]所指的View中
//     */
//    fun startTinyWindow(lp: LayoutParams?) {
//        Log.i(TAG, "startWindowTiny " + " [" + this.hashCode() + "] ")
//        check(parent == null) {
//            "The specified VideoView already has a parent. " +
//                    "You must call removeView() on the VideoView's parent first."
//        }
//        val context = context
//        windowType = WindowType.TINY
//        // add to window
//        val vp = Utils.scanForActivity(context)!!.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
//        val old = vp.findViewById<View>(R.id.salient_video_tiny_id)
//        if (old != null) {
//            vp.removeView(old)
//        }
//        id = R.id.salient_video_tiny_id
//        if (lp != null) {
//            vp.addView(this, lp)
//        } else {
//            vp.addView(this)
//        }
//        //add TextureView
//        MediaPlayerManagerOld.removeTextureView()
//        MediaPlayerManagerOld.addTextureView(this)
//        //update ControlPanel State
//        val controlPanel = controlPanel
//        controlPanel?.onEnterSecondScreen()
//        //update Parent ControlPanel State
//        val parentVideoView = parentVideoViewOld
//        if (parentVideoView != null) {
//            val parentControlPanel = parentVideoView.controlPanel
//            parentControlPanel?.onEnterSecondScreen()
//        }
//        MediaPlayerManagerOld.updateState(MediaPlayerManagerOld.playerState)
//    }
//
//    enum class WindowType {
//        NORMAL, LIST, FULLSCREEN, TINY
//    }
}