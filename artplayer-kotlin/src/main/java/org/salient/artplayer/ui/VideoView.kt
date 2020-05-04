package org.salient.artplayer.ui

import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.Surface
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import org.salient.artplayer.audio.DefaultAudioManager
import org.salient.artplayer.audio.IAudioManager
import org.salient.artplayer.bean.VideoSize
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.extend.Utils
import org.salient.artplayer.player.IMediaPlayer

/**
 * description: 视频播放视容器
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
class VideoView : FrameLayout, IVideoView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    //const
    private val TAG = javaClass.simpleName
    private val TEXTURE_VIEW_POSITION = 0 //视频播放视图层
    //surface an layout
    private val textureViewContainer = FrameLayout(context).apply { setBackgroundColor(Color.BLACK) }
    private var textureView: ResizeTextureView? = null
    private var surfaceTexture: SurfaceTexture? = null
    private var surface: Surface? = null

    override var mediaPlayer: IMediaPlayer<*>? = null
        set(value) {
            removeMediaPlayerObserver(field)
            field = value
            registerMediaPlayerObserver(field)
        }

    override var audioManager: IAudioManager = DefaultAudioManager(context, mediaPlayer)

    override val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true

    init {
        val params = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        addView(textureViewContainer, TEXTURE_VIEW_POSITION, params)
        registerMediaPlayerObserver(mediaPlayer)
    }

    override fun start() {
        Log.d(TAG, "start() called")
        when (mediaPlayer?.playerStateLD?.value) {
            PlayerState.IDLE, PlayerState.ERROR -> initialPlay()
            PlayerState.PREPARED, PlayerState.PAUSED -> play()
            PlayerState.PLAYING -> pause()
            PlayerState.COMPLETED -> replay()
        }

        //keep screen on
        Utils.scanForActivity(context)?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }

    /**
     * 开始播放
     */
    override fun play() {
        Log.d(TAG, "play() called")
        mediaPlayer?.start()
    }

    override fun replay() {
        Log.d(TAG, "replay() called")
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
    }

    /**
     * 暂停
     */
    override fun pause() {
        Log.d(TAG, "pause() called")
        mediaPlayer?.pause()
    }

    /**
     * 停止
     */
    override fun stop() {
        Log.d(TAG, "stop() called")
        mediaPlayer?.release()
    }

    /**
     * 释放
     */
    override fun release() {
        Log.d(TAG, "release() called")
        mediaPlayer?.release()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }

    private fun initialPlay() {
        Log.d(TAG, "initialPlay() called")
        (textureView?.parent as ViewGroup?)?.removeView(textureView)
        surfaceTexture = null
        textureView = ResizeTextureView(context)
        textureView?.surfaceTextureListener = this
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER)
        textureViewContainer.addView(textureView, layoutParams)
    }

    /**
     * go into prepare and start
     */
    private fun prepare() {
        Log.d(TAG, "prepare() called")
        surfaceTexture?.let {
            surface?.release()
            surface = Surface(it)
            mediaPlayer?.setSurface(surface)
        }
        mediaPlayer?.prepare()
    }

    fun seekTo(time: Long) {
        Log.d(TAG, "seekTo() called with: time = $time")
        mediaPlayer?.seekTo(time)
    }

    /**
     * 设置音量
     *
     * @param leftVolume  左声道
     * @param rightVolume 右声道
     */
    fun setVolume(volume: Int) {
        Log.d(TAG, "setVolume() called with: volume = $volume")
        audioManager.setVolume(volume)
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceTextureAvailable() called with: surfaceTexture = $surfaceTexture, width = $width, height = $height")
        if (this.surfaceTexture == null) {
            this.surfaceTexture = surfaceTexture
            prepare()
        } else if (textureView?.surfaceTexture != surfaceTexture) {
            textureView?.surfaceTexture = surfaceTexture
        }
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceTextureSizeChanged() called with: surfaceTexture = $surfaceTexture, width = $width, height = $height")
        if (textureView?.surfaceTexture != surfaceTexture) {
            textureView?.surfaceTexture = surfaceTexture
        }
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        Log.d(TAG, "onSurfaceTextureDestroyed() called with: surfaceTexture = $surfaceTexture")
        mediaPlayer?.release()
        this.surfaceTexture = null
        return true
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
        if (textureView?.surfaceTexture != surfaceTexture) {
            textureView?.surfaceTexture = surfaceTexture
        }
    }

    /**
     * 注册播放器内核的监听
     */
    private fun registerMediaPlayerObserver(mediaPlayer: IMediaPlayer<*>?) {
        val activity = Utils.scanForActivity(context)
        val lifecycleOwner = if (activity is LifecycleOwner) activity else return
        mediaPlayer?.videoSizeLD?.observe(lifecycleOwner, videoSizeObserver)
        mediaPlayer?.playerStateLD?.observe(lifecycleOwner, playerStateObserver)
    }

    /**
     * 移除播放器内核监听
     */
    private fun removeMediaPlayerObserver(mediaPlayer: IMediaPlayer<*>?){
        mediaPlayer?.videoSizeLD?.removeObserver(videoSizeObserver)
        mediaPlayer?.playerStateLD?.removeObserver(playerStateObserver)
    }

    /**
     * 视频尺寸监听
     */
    private val videoSizeObserver = Observer<VideoSize> {
        Log.d(TAG, "VideoSize: width = ${it.width}, height = ${it.height}")
        textureView?.setVideoSize(it.width, it.height)
    }

    /**
     * 视频播放器状态监听
     */
    private val playerStateObserver = Observer<PlayerState> {
        Log.d(TAG, "PlayerState: $it")
        when (it) {
            PlayerState.PREPARED -> {
                audioManager.requestAudioFocus()
                mediaPlayer?.start()
            }
            PlayerState.STOP -> {
                audioManager.abandonAudioFocus()
            }
            PlayerState.ERROR -> {
                audioManager.abandonAudioFocus()
            }
        }
    }

}