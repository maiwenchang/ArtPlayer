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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import org.salient.artplayer.audio.DefaultAudioManager
import org.salient.artplayer.audio.IAudioManager
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
        set(value) {
            field = value
            registerListener()
        }

    override var audioManager: IAudioManager = DefaultAudioManager(context, mediaPlayer)

    init {
        val params = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        addView(textureViewContainer, TEXTURE_VIEW_POSITION, params)
        registerListener()
    }

    val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }

    private fun registerListener() {
        val activity = if (context is FragmentActivity) context as FragmentActivity else return
        mediaPlayer?.videoSizeLD
        mediaPlayer?.videoSizeLD?.observe(activity, Observer {

        })
        mediaPlayer?.playerStateLD?.observe(activity, Observer {
            when (it) {
                PlayerState.PREPARED -> {
                    audioManager.requestAudioFocus()
                    mediaPlayer?.start()
                }
                PlayerState.STOP->{
                    audioManager.abandonAudioFocus()
                }
            }
        })
    }

    override fun start() {
        Log.d(tag, "play [" + hashCode() + "] ")
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

    private fun initialPlay() {
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
        if (this.surfaceTexture == null) {
            this.surfaceTexture = surfaceTexture
            prepare()
        } else if (textureView?.surfaceTexture != surfaceTexture){
            textureView?.surfaceTexture = surfaceTexture
        }
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, i: Int, i1: Int) {
        Log.i(tag, "onSurfaceTextureSizeChanged [" + "] ")
        if (textureView?.surfaceTexture != surfaceTexture){
            textureView?.surfaceTexture = surfaceTexture
        }
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        Log.i(tag, "onSurfaceTextureDestroyed [" + "] ")
        mediaPlayer?.release()
        return true
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
        if (textureView?.surfaceTexture != surfaceTexture){
            textureView?.surfaceTexture = surfaceTexture
        }
    }

}