package org.salient.artplayer

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.util.Log
import android.view.*
import android.view.TextureView.SurfaceTextureListener
import android.widget.FrameLayout
import org.salient.artplayer.OrientationEventManager.OnOrientationChangeListener
import java.util.*

/**
 * Created by Mai on 2018/7/10
 * *
 * Description: 视频播放器管理类
 * *
 */
class MediaPlayerManager private constructor() : SurfaceTextureListener {
    private val TAG = javaClass.simpleName
    private val CLICK_EVENT_SPAN = 300
    //surface
    private var textureView: ResizeTextureView? = null
    private var surfaceTexture: SurfaceTexture? = null
    private var surface: Surface? = null
    var playerState = PlayerState.IDLE
        private set
    var currentData: Any? = null
    private val mClickTime: Long = 0
    private var mProgressTimer: Timer? = null
    private var mProgressTimerTask: ProgressTimerTask? = null
    // settable by client 可设置的属性
    private var mOnAudioFocusChangeListener //音频焦点事件监听
            : OnAudioFocusChangeListener? = null
    private var mOnOrientationChangeListener //重力感应事件监听
            : OnOrientationChangeListener? = null
    private var mOrientationEventManager //重力感应事件管理
            : OrientationEventManager? = null
    var mediaPlayer //播放器内核
            : AbsMediaPlayer? = null
    /**
     * 设置静音
     *
     * @param isMute boolean
     */
    var isMute = false //是否静音
        set(isMute) {
            field = isMute
            mediaPlayer!!.mute(isMute)
        }
    /**
     * 设置循环播放
     *
     * @param isLooping boolean
     */
    var isLooping = false //是否循环播放
        set(isLooping) {
            field = isLooping
            mediaPlayer!!.setLooping(isLooping)
        }

    //正在播放的url或者uri
    val dataSource: Any?
        get() = mediaPlayer.getDataSource()

    fun setDataSource(dataSource: Any?, headers: Map<String?, String?>?) {
        mediaPlayer.setDataSource(dataSource)
        mediaPlayer!!.headers = headers
    }

    val duration: Long
        get() = mediaPlayer.getDuration()

    fun seekTo(time: Long) {
        mediaPlayer!!.seekTo(time)
    }

    fun pause() {
        if (isPlaying) {
            mediaPlayer!!.pause()
        }
    }

    fun start() {
        mediaPlayer!!.start()
    }

    /**
     * 设置音量
     *
     * @param leftVolume  左声道
     * @param rightVolume 右声道
     */
    fun setVolume(leftVolume: Float, rightVolume: Float) {
        mediaPlayer!!.setVolume(leftVolume, rightVolume)
    }

    /**
     * 在指定的VideoView上播放,
     * 即把textureView放到目标VideoView上
     *
     * @param target VideoView
     */
    fun playAt(target: VideoView?) {
        if (target == null) return
        removeTextureView()
        addTextureView(target)
    }

    val isPlaying: Boolean
        get() = playerState == PlayerState.PLAYING && mediaPlayer!!.isPlaying

    fun releasePlayerAndView(context: Context?) {
        if (System.currentTimeMillis() - mClickTime > CLICK_EVENT_SPAN) {
            Log.d(TAG, "release")
            if (context != null) {
                clearFullscreenLayout(context)
                clearTinyLayout(context)
                Utils.scanForActivity(context)!!.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                unbindAudioFocus(context)
                unbindOrientationManager()
                Utils.showSupportActionBar(context)
            }
            releaseMediaPlayer()
            removeTextureView()
            if (surfaceTexture != null) {
                surfaceTexture!!.release()
            }
            if (surface != null) {
                surface!!.release()
            }
            textureView = null
            surfaceTexture = null
        }
    }

    fun clearFullscreenLayout(context: Context?) {
        val vp = Utils.scanForActivity(context)!!.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        var oldF = vp.findViewById<View>(R.id.salient_video_fullscreen_id)
        if (oldF != null) {
            vp.removeView(oldF)
            oldF = null
        }
    }

    fun clearTinyLayout(context: Context?) {
        val vp = Utils.scanForActivity(context)!!.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        var oldT = vp.findViewById<View>(R.id.salient_video_tiny_id)
        if (oldT != null) {
            vp.removeView(oldT)
            oldT = null
        }
    }

    fun releaseMediaPlayer() {
        cancelProgressTimer()
        mediaPlayer!!.release()
        mediaPlayer!!.headers = null
        mediaPlayer.setDataSource(null)
        currentData = null
        updateState(PlayerState.IDLE)
    }

    /**
     * go into prepare and start
     */
    private fun prepare() {
        mediaPlayer!!.prepare()
        if (surfaceTexture != null) {
            if (surface != null) {
                surface!!.release()
            }
            surface = Surface(surfaceTexture)
            mediaPlayer!!.setSurface(surface)
        }
    }

    /**
     * 解绑音频焦点管理
     */
    fun unbindAudioFocus(context: Context) {
        val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManager?.abandonAudioFocus(mOnAudioFocusChangeListener)
    }

    /**
     * 绑定音频焦点管理
     * @param context Context
     */
    fun bindAudioFocus(context: Context) {
        val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener)
            mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
    }

    /**
     * 设置音频焦点监听
     */
    fun setOnAudioFocusChangeListener(onAudioFocusChangeListener: OnAudioFocusChangeListener?) {
        mOnAudioFocusChangeListener = onAudioFocusChangeListener
    }

    /**
     * 解绑重力感应横竖屏管理
     */
    fun unbindOrientationManager() {
        mOrientationEventManager!!.orientationDisable()
    }

    /**
     * 绑定重力感应横竖屏管理
     */
    fun bindOrientationManager(context: Context) {
        mOrientationEventManager!!.orientationDisable()
        mOrientationEventManager!!.orientationEnable(context, mOnOrientationChangeListener)
    }

    /**
     * 设置重力感应横竖屏管理
     */
    fun setOnOrientationChangeListener(orientationChangeListener: OnOrientationChangeListener?) {
        mOnOrientationChangeListener = orientationChangeListener
    }

    fun updateState(playerState: PlayerState) {
        Log.i(TAG, "updateState [" + playerState.name + "] ")
        this.playerState = playerState
        when (this.playerState) {
            PlayerState.PLAYING, PlayerState.PAUSED -> startProgressTimer()
            PlayerState.ERROR, PlayerState.IDLE, PlayerState.PLAYBACK_COMPLETED -> cancelProgressTimer()
        }
        val currentFloor = currentVideoView
        if (currentFloor != null && currentFloor.isCurrentPlaying) {
            val controlPanel = currentFloor.controlPanel
            controlPanel?.notifyStateChange()
        }
    }

    fun onVideoSizeChanged(width: Int, height: Int) {
        Log.i(TAG, "onVideoSizeChanged " + " [" + this.hashCode() + "] ")
        if (textureView != null) {
            textureView!!.setVideoSize(width, height)
        }
    }

    fun setScreenScale(scaleType: ScaleType?) {
        if (textureView != null) {
            textureView!!.setScreenScale(scaleType)
        }
    }

    fun initTextureView(context: Context?) {
        removeTextureView()
        surfaceTexture = null
        textureView = ResizeTextureView(context)
        textureView!!.surfaceTextureListener = instance()
    }

    fun addTextureView(videoView: VideoView) {
        if (textureView == null) {
            return
        }
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ")
        val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER)
        videoView.textureViewContainer.addView(textureView, layoutParams)
    }

    fun removeTextureView() {
        if (textureView != null && textureView!!.parent != null) {
            (textureView!!.parent as ViewGroup).removeView(textureView)
        }
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, i: Int, i1: Int) {
        if (currentVideoView == null) return
        Log.i(TAG, "onSurfaceTextureAvailable [" + "] ")
        if (this.surfaceTexture == null) {
            this.surfaceTexture = surfaceTexture
            prepare()
        } else {
            textureView!!.surfaceTexture = this.surfaceTexture
        }
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, i: Int, i1: Int) {
        Log.i(TAG, "onSurfaceTextureSizeChanged [" + "] ")
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        Log.i(TAG, "onSurfaceTextureDestroyed [" + "] ")
        return this.surfaceTexture == null
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}

    /**
     * 拦截返回键
     */
    fun backPress(): Boolean {
        Log.i(TAG, "backPress")
        try {
            val currentVideoView = currentVideoView
            if (currentVideoView != null && currentVideoView.windowType == VideoView.WindowType.FULLSCREEN) { //退出全屏
                currentVideoView.exitFullscreen()
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun startProgressTimer() {
        Log.i(TAG, "startProgressTimer: " + " [" + this.hashCode() + "] ")
        cancelProgressTimer()
        mProgressTimer = Timer()
        mProgressTimerTask = ProgressTimerTask()
        mProgressTimer!!.schedule(mProgressTimerTask, 0, 300)
    }

    fun cancelProgressTimer() {
        if (mProgressTimerTask != null) {
            mProgressTimerTask!!.cancel()
        }
        if (mProgressTimer != null) {
            mProgressTimer!!.cancel()
        }
    }

    val currentPositionWhenPlaying: Long
        get() {
            var position: Long = 0
            if (playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED) {
                try {
                    position = mediaPlayer.getCurrentPosition()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
            return position
        }

    /**
     * 获得 MediaPlayer 绑定的 VideoView
     *
     * @return VideoView
     */
    val currentVideoView: VideoView?
        get() {
            if (textureView == null) return null
            val surfaceContainer = textureView!!.parent ?: return null
            val parent = surfaceContainer.parent
            return if (parent != null && parent is VideoView) {
                parent
            } else null
        }

    val currentControlPanel: AbsControlPanel?
        get() {
            val currentVideoView = currentVideoView ?: return null
            return currentVideoView.controlPanel
        }

    // all possible MediaPlayer states
    enum class PlayerState {
        ERROR, IDLE, PREPARING, PREPARED, PLAYING, PAUSED, PLAYBACK_COMPLETED
    }

    //内部类实现单例模式
    private object ManagerHolder {
        val INSTANCE = MediaPlayerManager()
    }

    inner class ProgressTimerTask : TimerTask() {
        override fun run() {
            if (playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED) {
                val position = currentPositionWhenPlaying
                val duration = duration
                val progress = (position * 100 / if (duration == 0L) 1 else duration).toInt()
                val currentVideoView = currentVideoView ?: return
                val controlPanel = currentVideoView.controlPanel
                controlPanel?.onProgressUpdate(progress, position, duration)
            }
        }
    }

    companion object {
        @JvmStatic
        fun instance(): MediaPlayerManager {
            return ManagerHolder.INSTANCE
        }
    }

    init {
        if (mediaPlayer == null) {
            mediaPlayer = SystemMediaPlayer()
            mOrientationEventManager = OrientationEventManager()
        }
    }
}