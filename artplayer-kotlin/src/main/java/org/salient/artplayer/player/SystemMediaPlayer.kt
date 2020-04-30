package org.salient.artplayer.player

import android.app.Service
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.view.Surface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.salient.artplayer.audio.DefaultAudioManager
import org.salient.artplayer.bean.VideoInfo
import org.salient.artplayer.bean.VideoSize

/**
 * Created by Mai on 2018/7/10
 * *
 * Description: 系统默认播放器
 * *
 */
class SystemMediaPlayer : IMediaPlayer<MediaPlayer> {
    override var impl: MediaPlayer = MediaPlayer()

    override val videoSizeLD: MutableLiveData<VideoSize> = MutableLiveData()
    override val bufferingProgressLD: MutableLiveData<Int> = MutableLiveData()
    override val videoInfoLD: MutableLiveData<VideoInfo> = MutableLiveData()
    override val videoErrorLD: MutableLiveData<VideoInfo> = MutableLiveData()

    init {
        impl.setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
        impl.setScreenOnWhilePlaying(true)
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
//            MediaPlayerManagerOld.updateState(PlayerState.ERROR)
        }
    }

    override fun start() {
        try {
            impl.start()
//            MediaPlayerManagerOld.updateState(PlayerState.PLAYING)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun pause() {
        try {
            impl.pause()
//            MediaPlayerManagerOld.updateState(PlayerState.PAUSED)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        try {
            impl.stop()
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
//            MediaPlayerManagerOld.updateState(PlayerState.IDLE)
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


}