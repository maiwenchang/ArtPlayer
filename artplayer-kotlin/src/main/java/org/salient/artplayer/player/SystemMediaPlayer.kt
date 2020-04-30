package org.salient.artplayer.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.view.Surface
import androidx.lifecycle.MutableLiveData
import org.salient.artplayer.bean.VideoInfo
import org.salient.artplayer.bean.VideoSize
import org.salient.artplayer.conduction.PlayerState

/**
 * Created by Mai on 2018/7/10
 * *
 * Description: 系统默认播放器
 * *
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

    override fun prepare() {
        try {
//            MediaPlayerManagerOld.updateState(PlayerState.PREPARING)
//            if (dataSource is AssetFileDescriptor) { //Android assets file
//                val fd = dataSource
//                mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
//            } else if (dataSource != null && headers != null) { //url with headers
//                val clazz = MediaPlayer::class.java
//                val method = clazz.getDeclaredMethod("setDataSource", String::class.java, MutableMap::class.java)
//                method.invoke(mediaPlayer, dataSource.toString(), headers)
//            } else if (dataSource != null) {
//                mediaPlayer.setDataSource(dataSource.toString())
//            }

            impl.prepareAsync()
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
            playerStateLD.value = PlayerState.STOP
        } catch (e: Exception) {
        }
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

    override val currentPosition: Long
        get() = try {
            impl.currentPosition.toLong()
        } catch (e: Exception) {
            0
        }

    override val duration: Long
        get() = try {
            impl.duration.toLong()
        } catch (e: Exception) {
            0
        }

    override fun setSurface(surface: Surface?) {
        try {
            impl.setSurface(surface)
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