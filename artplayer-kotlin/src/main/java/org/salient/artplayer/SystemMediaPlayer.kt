package org.salient.artplayer

import android.app.Service
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.view.Surface
import org.salient.artplayer.MediaPlayerManager.PlayerState

/**
 * Created by Mai on 2018/7/10
 * *
 * Description: 系统默认播放器
 * *
 */
class SystemMediaPlayer : AbsMediaPlayer(), OnPreparedListener, OnCompletionListener, OnBufferingUpdateListener, OnSeekCompleteListener, OnErrorListener, OnInfoListener, OnVideoSizeChangedListener {
    private var mediaPlayer: MediaPlayer? = null
    override fun start() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer!!.start()
                MediaPlayerManager.updateState(PlayerState.PLAYING)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun prepare() {
        try {
            MediaPlayerManager.updateState(PlayerState.PREPARING)
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.setOnPreparedListener(this)
            mediaPlayer!!.setOnCompletionListener(this)
            mediaPlayer!!.setOnBufferingUpdateListener(this)
            mediaPlayer!!.setScreenOnWhilePlaying(true)
            mediaPlayer!!.setOnSeekCompleteListener(this)
            mediaPlayer!!.setOnErrorListener(this)
            mediaPlayer!!.setOnInfoListener(this)
            mediaPlayer!!.setOnVideoSizeChangedListener(this)
            if (MediaPlayerManager.isMute) {
                mute(true)
            }
            if (MediaPlayerManager.isLooping) {
                setLooping(true)
            }
            val dataSource = dataSource
            if (dataSource is AssetFileDescriptor) { //Android assets file
                val fd = dataSource
                mediaPlayer!!.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
            } else if (dataSource != null && headers != null) { //url with headers
                val clazz = MediaPlayer::class.java
                val method = clazz.getDeclaredMethod("setDataSource", String::class.java, MutableMap::class.java)
                method.invoke(mediaPlayer, dataSource.toString(), headers)
            } else if (dataSource != null) {
                mediaPlayer!!.setDataSource(dataSource.toString())
            }
            mediaPlayer!!.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
            MediaPlayerManager.updateState(PlayerState.ERROR)
        }
    }

    override fun pause() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer!!.pause()
                MediaPlayerManager.updateState(PlayerState.PAUSED)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override val isPlaying: Boolean
        get() {
            try {
                return mediaPlayer!!.isPlaying
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    override fun seekTo(time: Long) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer!!.seekTo(time.toInt())
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun release() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer!!.release()
                MediaPlayerManager.updateState(PlayerState.IDLE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override val currentPosition: Long
        get() {
            try {
                return if (mediaPlayer != null) {
                    mediaPlayer!!.currentPosition.toLong()
                } else {
                    0
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0
        }

    override val duration: Long
        get() {
            try {
                return if (mediaPlayer != null) {
                    mediaPlayer!!.duration.toLong()
                } else {
                    0
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0
        }

    override fun setSurface(surface: Surface?) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer!!.setSurface(surface)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer!!.setVolume(leftVolume, rightVolume)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun OpenVolume() {
        try {
            if (mediaPlayer != null) {
                val currentFloor: VideoView = MediaPlayerManager.currentVideoView
                        ?: return
                val context = currentFloor.context ?: return
                val audioManager = context.getSystemService(Service.AUDIO_SERVICE) as AudioManager
                        ?: return
                val streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
                val volume = streamVolume * 1.000f / maxVolume
                mediaPlayer!!.setVolume(volume, volume)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun CloseVolume() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer!!.setVolume(0f, 0f)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun mute(isMute: Boolean) {
        if (isMute) {
            CloseVolume()
        } else {
            OpenVolume()
        }
    }

    override fun setLooping(isLoop: Boolean) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer!!.isLooping = isLoop
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        MediaPlayerManager.updateState(PlayerState.PREPARED)
        MediaPlayerManager.start()
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        MediaPlayerManager.updateState(PlayerState.PLAYBACK_COMPLETED)
    }

    override fun onBufferingUpdate(mediaPlayer: MediaPlayer, percent: Int) {
        MediaPlayerManager.currentControlPanel?.onBufferingUpdate(percent)
    }

    override fun onSeekComplete(mediaPlayer: MediaPlayer) {
        MediaPlayerManager.currentControlPanel?.onSeekComplete()
    }

    override fun onError(mediaPlayer: MediaPlayer, what: Int, extra: Int): Boolean {
        MediaPlayerManager.updateState(PlayerState.ERROR)
        return true
    }

    override fun onInfo(mediaPlayer: MediaPlayer, what: Int, extra: Int): Boolean {
        MediaPlayerManager.currentControlPanel?.onInfo(what, extra)
        return false
    }

    override fun onVideoSizeChanged(mediaPlayer: MediaPlayer, width: Int, height: Int) {
        MediaPlayerManager.onVideoSizeChanged(width, height)
    }
}