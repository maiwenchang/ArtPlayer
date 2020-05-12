package org.salient.artplayer.ijk

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.view.Surface
import android.view.SurfaceHolder
import androidx.lifecycle.MutableLiveData
import org.salient.artplayer.bean.VideoInfo
import org.salient.artplayer.bean.VideoSize
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import tv.danmaku.ijk.media.player.misc.IMediaDataSource
import java.io.FileDescriptor
import java.io.IOException

/**
 * description: ijk视频播放器封装
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
class IjkPlayer : IMediaPlayer<IjkMediaPlayer>,
        tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener,
        tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener,
        tv.danmaku.ijk.media.player.IMediaPlayer.OnBufferingUpdateListener,
        tv.danmaku.ijk.media.player.IMediaPlayer.OnSeekCompleteListener,
        tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener,
        tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener,
        tv.danmaku.ijk.media.player.IMediaPlayer.OnVideoSizeChangedListener {

    override var impl: IjkMediaPlayer = IjkMediaPlayer()
    override var playWhenReady: Boolean = true

    override val playerStateLD: MutableLiveData<PlayerState> = MutableLiveData()
    override val videoSizeLD: MutableLiveData<VideoSize> = MutableLiveData()
    override val bufferingProgressLD: MutableLiveData<Int> = MutableLiveData()
    override val videoInfoLD: MutableLiveData<VideoInfo> = MutableLiveData()
    override val videoErrorLD: MutableLiveData<VideoInfo> = MutableLiveData()

    init {
        playerStateLD.value = PlayerState.IDLE
        impl.setAudioStreamType(AudioManager.STREAM_MUSIC)
        impl.setScreenOnWhilePlaying(true)
        impl.setOnPreparedListener(this)
        impl.setOnCompletionListener(this)
        impl.setOnBufferingUpdateListener(this)
        impl.setOnSeekCompleteListener(this)
        impl.setOnErrorListener(this)
        impl.setOnInfoListener(this)
        impl.setOnVideoSizeChangedListener(this)
    }

    @Throws(IOException::class, IllegalArgumentException::class, SecurityException::class, java.lang.IllegalStateException::class)
    fun setDataSource(context: Context?, uri: Uri?) {
        impl.setDataSource(context, uri)
        playerStateLD.value = PlayerState.INITIALIZED
    }

    @Throws(IOException::class, IllegalArgumentException::class, SecurityException::class, java.lang.IllegalStateException::class)
    fun setDataSource(context: Context?, uri: Uri?, headers: Map<String?, String?>?) {
        impl.setDataSource(context, uri, headers)
        playerStateLD.value = PlayerState.INITIALIZED
    }

    @Throws(IOException::class, IllegalArgumentException::class, java.lang.IllegalStateException::class)
    fun setDataSource(fd: FileDescriptor?) {
        impl.setDataSource(fd)
        playerStateLD.value = PlayerState.INITIALIZED
    }

    @Throws(IOException::class, IllegalArgumentException::class, SecurityException::class, java.lang.IllegalStateException::class)
    fun setDataSource(path: String?, headers: Map<String?, String?>?) {
        impl.setDataSource(path, headers)
        playerStateLD.value = PlayerState.INITIALIZED
    }

    @Throws(IOException::class, IllegalArgumentException::class, SecurityException::class, java.lang.IllegalStateException::class)
    fun setDataSource(path: String?) {
        impl.dataSource = path
        playerStateLD.value = PlayerState.INITIALIZED
    }

    fun setDataSource(mediaDataSource: IMediaDataSource?) {
        this.impl.setDataSource(mediaDataSource)
        playerStateLD.value = PlayerState.INITIALIZED
    }

    override val isPlaying: Boolean
        get() {
            try {
                return impl.isPlaying
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    override val currentPosition: Long
        get() = try {
            impl.currentPosition
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    override val duration: Long
        get() = try {
            impl.duration
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    override val videoHeight: Int
        get() = try {
            impl.videoHeight
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    override val videoWidth: Int
        get() = try {
            impl.videoHeight
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    override val playerState: PlayerState
        get() = playerStateLD.value ?: PlayerState.IDLE

    override fun prepare() {
        try {
            impl.prepareAsync()
            playerStateLD.value = PlayerState.PREPARING
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun prepareAsync() {
        try {
            impl.prepareAsync()
            playerStateLD.value = PlayerState.PREPARING
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun start() {
        try {
            impl.start()
            playerStateLD.value = PlayerState.STARTED
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun pause() {
        try {
            impl.pause()
            playerStateLD.value = PlayerState.PAUSED
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        try {
            impl.stop()
            playerStateLD.value = PlayerState.STOPPED
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun seekTo(time: Long) {
        try {
            impl.seekTo(time)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun release() {
        try {
            impl.release()
            playerStateLD.value = PlayerState.END
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun reset() {
        try {
            impl.reset()
            playerStateLD.value = PlayerState.IDLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setVolume(volume: Float) {
        try {
            impl.setVolume(volume, volume)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setLooping(isLoop: Boolean) {
        try {
            impl.isLooping = isLoop
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setSurface(surface: Surface?) {
        try {
            impl.setSurface(surface)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setDisplay(surfaceHolder: SurfaceHolder) {
        try {
            impl.setDisplay(surfaceHolder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPrepared(p0: tv.danmaku.ijk.media.player.IMediaPlayer?) {
        playerStateLD.value = PlayerState.PREPARED
    }

    override fun onCompletion(p0: tv.danmaku.ijk.media.player.IMediaPlayer?) {
        playerStateLD.value = PlayerState.COMPLETED
    }

    override fun onBufferingUpdate(mediaPlayer: tv.danmaku.ijk.media.player.IMediaPlayer?, percent: Int) {
        bufferingProgressLD.value = percent
    }

    override fun onSeekComplete(mediaPlayer: tv.danmaku.ijk.media.player.IMediaPlayer?) {
        playerStateLD.value = PlayerState.SEEK_COMPLETE
    }

    override fun onError(mediaPlayer: tv.danmaku.ijk.media.player.IMediaPlayer?, what: Int, extra: Int): Boolean {
        videoErrorLD.value = VideoInfo(what, extra)
        playerStateLD.value = PlayerState.ERROR
        return false
    }

    override fun onInfo(mediaPlayer: tv.danmaku.ijk.media.player.IMediaPlayer?, what: Int, extra: Int): Boolean {
        videoInfoLD.value = VideoInfo(what, extra)
        return false
    }

    override fun onVideoSizeChanged(mediaPlayer: tv.danmaku.ijk.media.player.IMediaPlayer?, p1: Int, p2: Int, p3: Int, p4: Int) {
        val videoWidth = mediaPlayer?.videoWidth ?: 0
        val videoHeight = mediaPlayer?.videoHeight ?: 0
        if (videoWidth != 0 && videoHeight != 0) {
            videoSizeLD.value = VideoSize(videoWidth, videoHeight)
        }
    }

    // +++++++++++++++++++++++++++++ ijk only ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    /**
     * 设置倍速
     */
    fun setSpeed(speed: Float) {
        impl.setSpeed(speed)
    }

    /**
     * 是否开启硬件加速
     */
    fun setEnableMediaCodec(isEnable: Boolean) {
        val value = if (isEnable) 1 else 0
        impl.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value.toLong()) //开启硬解码
        impl.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value.toLong())
        impl.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value.toLong())
    }

    // +++++++++++++++++++++++++++++ ijk only ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
}