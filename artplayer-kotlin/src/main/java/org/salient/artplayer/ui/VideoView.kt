package org.salient.artplayer.ui

import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import org.salient.artplayer.audio.DefaultAudioManager
import org.salient.artplayer.audio.IAudioManager
import org.salient.artplayer.bean.VideoBean
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.extend.Utils
import org.salient.artplayer.player.IMediaPlayer

/**
 * Created by Mai on 2018/7/10
 * *
 * Description: 视频播放视图
 * *
 */
class VideoView : FrameLayout, IVideoView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    //const
    private val tag = javaClass.simpleName
    private val TEXTURE_VIEW_POSITION = 0 //视频播放视图层
    private val CONTROL_PANEL_POSITION = 1 //控制面板层
    //surface an layout
    private val textureViewContainer = FrameLayout(context).apply { setBackgroundColor(Color.BLACK) }
    private var textureView: ResizeTextureView? = null
    private var surfaceTexture: SurfaceTexture? = null
    private var surface: Surface? = null

    override var mediaPlayer: IMediaPlayer<*>? = null
    override var videoBean: VideoBean? = null
    override var playingState = PlayerState.IDLE
    override var audioManager: IAudioManager = DefaultAudioManager(context, mediaPlayer)

    init {

        val params = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        addView(textureViewContainer, TEXTURE_VIEW_POSITION, params)

        if (context is FragmentActivity) {
            val activity = (context as FragmentActivity)
            registerListener(activity)
        }
    }

    val isPlaying: Boolean
        get() = playingState == PlayerState.PLAYING && mediaPlayer?.isPlaying == true

    fun setUp(url: String?) {
        videoBean = VideoBean().also { it.url = url }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }

    private fun registerListener(activity: FragmentActivity) {
        mediaPlayer?.videoSizeLD?.observe(activity, Observer {

        })
    }

    override fun start() {
        Log.d(tag, "play [" + hashCode() + "] ")
        if (videoBean == null) {
            Log.w(tag, "VideoView needs a url or a dataSource to initialize playing")
            return
        }

        when (playingState) {
            PlayerState.IDLE, PlayerState.ERROR -> initialPlay()
            PlayerState.PREPARING -> {
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> play()
            PlayerState.PLAYING -> pause()
            PlayerState.PLAYBACK_COMPLETED -> replay()
        }

        //keep screen on
        Utils.scanForActivity(context)?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }

    private fun initialPlay() {
        videoBean?.url?.let {
            mediaPlayer?.prepare()
            return
        }

    }

    /**
     * 开始播放
     */
    override fun play() {
        mediaPlayer?.start()
    }

    override fun replay() {
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
    }

    /**
     * 暂停
     */
    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun stop() {
        mediaPlayer?.release()
    }

    override fun release() {
        mediaPlayer?.release()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }


    fun initTextureView() {
        (textureView?.parent as ViewGroup?)?.removeView(textureView)
        surfaceTexture = null
        textureView = ResizeTextureView(context)
        textureView?.surfaceTextureListener = this
    }

    /**
     * go into prepare and start
     */
    private fun prepare() {
        surfaceTexture?.let {
            surface?.release()
            surface = Surface(it)
            mediaPlayer?.setSurface(surface)
        }
        mediaPlayer?.prepare()
    }

    fun seekTo(time: Long) {
        mediaPlayer?.seekTo(time)
    }

    /**
     * 设置音量
     *
     * @param leftVolume  左声道
     * @param rightVolume 右声道
     */
    fun setVolume(volume: Int) {
        audioManager.setVolume(volume)
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, i: Int, i1: Int) {
        Log.i(tag, "onSurfaceTextureAvailable [" + "] ")
//        if (this.surfaceTexture == null) {
//            this.surfaceTexture = surfaceTexture
//
//        } else {
//        }
        textureView?.surfaceTexture = surfaceTexture
        prepare()
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, i: Int, i1: Int) {
        Log.i(tag, "onSurfaceTextureSizeChanged [" + "] ")
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        Log.i(tag, "onSurfaceTextureDestroyed [" + "] ")
        mediaPlayer?.release()
        return true
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
        textureView?.surfaceTexture = surfaceTexture
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
//        MediaPlayerManagerOld.updateState(PlayerState.PREPARED)
        audioManager.requestAudioFocus()
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
//        MediaPlayerManagerOld.updateState(PlayerState.PLAYBACK_COMPLETED)
    }

    override fun onBufferingUpdate(mediaPlayer: MediaPlayer, percent: Int) {
//        MediaPlayerManagerOld.currentControlPanel?.onBufferingUpdate(percent)
    }

    override fun onSeekComplete(mediaPlayer: MediaPlayer) {
//        MediaPlayerManagerOld.currentControlPanel?.onSeekComplete()
    }

    override fun onError(mediaPlayer: MediaPlayer, what: Int, extra: Int): Boolean {
//        MediaPlayerManagerOld.updateState(PlayerState.ERROR)
        return true
    }

    override fun onInfo(mediaPlayer: MediaPlayer, what: Int, extra: Int): Boolean {
//        MediaPlayerManagerOld.currentControlPanel?.onInfo(what, extra)
        return false
    }

    override fun onVideoSizeChanged(mediaPlayer: MediaPlayer, width: Int, height: Int) {
//        MediaPlayerManagerOld.onVideoSizeChanged(width, height)
    }
}