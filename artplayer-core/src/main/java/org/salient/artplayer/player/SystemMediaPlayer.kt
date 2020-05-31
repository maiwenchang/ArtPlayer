package org.salient.artplayer.player

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaDataSource
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.net.Uri
import android.os.Build
import android.view.Surface
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import org.salient.artplayer.bean.VideoInfo
import org.salient.artplayer.bean.VideoSize
import org.salient.artplayer.conduction.PlayerState
import java.io.FileDescriptor
import java.io.IOException
import java.net.HttpCookie

/**
 * description: 系统视频播放器的封装
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
open class SystemMediaPlayer : IMediaPlayer<MediaPlayer>, OnPreparedListener,
        OnCompletionListener,
        OnBufferingUpdateListener,
        OnSeekCompleteListener,
        OnErrorListener,
        OnInfoListener,
        OnVideoSizeChangedListener {

    final override var impl: MediaPlayer = MediaPlayer()
    override var playWhenReady: Boolean = true

    final override val playerStateLD: MutableLiveData<PlayerState> = MutableLiveData()
    override val videoSizeLD: MutableLiveData<VideoSize> = MutableLiveData()
    override val bufferingProgressLD: MutableLiveData<Int> = MutableLiveData()
    override val seekCompleteLD: MutableLiveData<Boolean> = MutableLiveData()
    override val videoInfoLD: MutableLiveData<VideoInfo> = MutableLiveData()
    override val videoErrorLD: MutableLiveData<VideoInfo> = MutableLiveData()

    init {
        playerStateLD.value = PlayerState.IDLE
        impl.setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
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
    fun setDataSource(context: Context, uri: Uri) {
        impl.setDataSource(context, uri)
        playerStateLD.value = PlayerState.INITIALIZED
    }

    @Throws(IOException::class, IllegalArgumentException::class, SecurityException::class, java.lang.IllegalStateException::class)
    fun setDataSource(context: Context, uri: Uri, headers: Map<String?, String?>?) {
        impl.setDataSource(context, uri, headers)
        playerStateLD.value = PlayerState.INITIALIZED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class)
    fun setDataSource(context: Context, uri: Uri, headers: Map<String?, String?>?, cookies: List<HttpCookie>?) {
        impl.setDataSource(context, uri, headers, cookies)
        playerStateLD.value = PlayerState.INITIALIZED
    }

    @Throws(IOException::class, IllegalArgumentException::class, SecurityException::class, java.lang.IllegalStateException::class)
    fun setDataSource(path: String) {
        impl.setDataSource(path)
        playerStateLD.value = PlayerState.INITIALIZED
    }

    @Throws(IOException::class, IllegalArgumentException::class, java.lang.IllegalStateException::class)
    fun setDataSource(fd: FileDescriptor) {
        impl.setDataSource(fd)
        playerStateLD.value = PlayerState.INITIALIZED
    }

    @Throws(IOException::class, IllegalArgumentException::class,java.lang.IllegalStateException::class)
    fun setDataSource(fd: FileDescriptor, offset: Long, length: Long) {
        impl.setDataSource(fd, offset, length)
        playerStateLD.value = PlayerState.INITIALIZED
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Throws(IOException::class, IllegalArgumentException::class, java.lang.IllegalStateException::class)
    fun setDataSource(afd: AssetFileDescriptor) {
        impl.setDataSource(afd)
        playerStateLD.value = PlayerState.INITIALIZED
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(IllegalArgumentException::class, java.lang.IllegalStateException::class)
    fun setDataSource(dataSource: MediaDataSource) {
        impl.setDataSource(dataSource)
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
            impl.currentPosition.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    override val duration: Long
        get() = try {
            impl.duration.toLong()
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
            impl.prepare()
            playerStateLD.value = PlayerState.PREPARED
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                impl.seekTo(time, SEEK_CLOSEST)
            } else {
                impl.seekTo(time.toInt())
            }
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

    override fun onPrepared(mp: MediaPlayer?) {
        playerStateLD.value = PlayerState.PREPARED
    }

    override fun onCompletion(mp: MediaPlayer?) {
        playerStateLD.value = PlayerState.COMPLETED
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        bufferingProgressLD.value = percent
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        seekCompleteLD.value = true
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        videoErrorLD.value = VideoInfo(what, extra)
        playerStateLD.value = PlayerState.ERROR
        return false
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        videoInfoLD.value = VideoInfo(what, extra)
        return false
    }

    override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
        videoSizeLD.value = VideoSize(width, height)
    }


}