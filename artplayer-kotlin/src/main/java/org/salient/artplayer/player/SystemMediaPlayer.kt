package org.salient.artplayer.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.view.Surface
import android.view.SurfaceHolder
import androidx.lifecycle.MutableLiveData
import org.salient.artplayer.bean.VideoInfo
import org.salient.artplayer.bean.VideoSize
import org.salient.artplayer.conduction.PlayerState

/**
 * description: 系统视频播放器的封装
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
class SystemMediaPlayer : IMediaPlayer<MediaPlayer>, OnPreparedListener,
        OnCompletionListener,
        OnBufferingUpdateListener,
        OnSeekCompleteListener,
        OnErrorListener,
        OnInfoListener,
        OnVideoSizeChangedListener {

    override var impl: MediaPlayer = MediaPlayer()

    override val playerStateLD: MutableLiveData<PlayerState> = MutableLiveData()
    override val videoSizeLD: MutableLiveData<VideoSize> = MutableLiveData()
    override val bufferingProgressLD: MutableLiveData<Int> = MutableLiveData()
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

    override fun prepare() {
        try {
            impl.prepareAsync()
            playerStateLD.value = PlayerState.PREPARING
        } catch (e: Exception) {
            e.printStackTrace()
            playerStateLD.value = PlayerState.ERROR
        }
    }

    override fun start() {
        try {
            impl.start()
            playerStateLD.value = PlayerState.PLAYING
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
            impl.seekTo(time.toInt())
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun release() {
        try {
            impl.release()
            playerStateLD.value = PlayerState.IDLE
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
        playerStateLD.value = PlayerState.SEEK_COMPLETE
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        videoErrorLD.value = VideoInfo(what, extra)
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