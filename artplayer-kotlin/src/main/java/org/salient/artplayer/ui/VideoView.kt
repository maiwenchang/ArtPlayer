package org.salient.artplayer.ui

import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.Surface
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import org.salient.artplayer.audio.DefaultAudioManager
import org.salient.artplayer.audio.IAudioManager
import org.salient.artplayer.bean.VideoSize
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.conduction.WindowType
import org.salient.artplayer.extend.Utils
import org.salient.artplayer.player.IMediaPlayer

/**
 * description: 视频播放视容器
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
open class VideoView : FrameLayout, IVideoView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val TAG = javaClass.simpleName
    private var textureView: ResizeTextureView? = null
    private var surfaceTexture: SurfaceTexture? = null
    private var surface: Surface? = null

    final override var mediaPlayer: IMediaPlayer<*>? = null
        set(value) {
            removeMediaPlayerObserver(field)
            field = value
            registerMediaPlayerObserver(field)
        }

    override var audioManager: IAudioManager = DefaultAudioManager(context, this.mediaPlayer)

    override val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true

    init {
        tag = WindowType.NORMAL
        this.setBackgroundColor(Color.BLACK)
        textureView = ResizeTextureView(context)
        textureView?.surfaceTextureListener = this
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER)
        this.addView(textureView, layoutParams)
        registerMediaPlayerObserver(this.mediaPlayer)
    }

    override fun prepare() {
        Log.d(TAG, "prepare() called")
        attach()
        mediaPlayer?.prepare()
    }

    /**
     * 开始播放
     */
    override fun start() {
        Log.d(TAG, "start() called")
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
        mediaPlayer?.stop()
    }

    /**
     * 释放
     */
    override fun release() {
        Log.d(TAG, "release() called")
        mediaPlayer?.release()
        surfaceTexture = null
    }

    /**
     * 重置
     */
    override fun reset() {
        Log.d(TAG, "reset() called")
        mediaPlayer?.reset()
    }

    /**
     * 跳转
     */
    override fun seekTo(time: Long) {
        Log.d(TAG, "seekTo() called with: time = $time")
        mediaPlayer?.seekTo(time)
    }

    /**
     * 设置音量
     *
     */
    override fun setVolume(volume: Int) {
        Log.d(TAG, "setVolume() called with: volume = $volume")
        audioManager.setVolume(volume)
    }

    override fun attach() {
        Log.d(TAG, "attach() called")
        surfaceTexture?.let {
            surface?.release()
            surface = Surface(it)
            mediaPlayer?.setSurface(surface)
        }
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceTextureAvailable() called with: surfaceTexture = $surfaceTexture, width = $width, height = $height")
        if (this.surfaceTexture == null) {
            this.surfaceTexture = surfaceTexture
            attach()
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
    private fun removeMediaPlayerObserver(mediaPlayer: IMediaPlayer<*>?) {
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
        Log.d(TAG, "PlayerState: ${it.javaClass.canonicalName}")
        when (it) {
            PlayerState.PREPARED -> {
                audioManager.requestAudioFocus()
                mediaPlayer?.start()
            }
            PlayerState.STOPPED -> {
                audioManager.abandonAudioFocus()
            }
            PlayerState.ERROR -> {
                audioManager.abandonAudioFocus()
            }
        }
    }

}