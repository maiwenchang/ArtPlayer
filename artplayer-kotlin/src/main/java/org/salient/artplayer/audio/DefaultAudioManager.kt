package org.salient.artplayer.audio

import android.app.Service
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import org.salient.artplayer.player.IMediaPlayer

/**
 * description: 音频管理
 *
 * @author 麦文昌(A01031)
 *
 * @date 2020-02-14 15:24.
 */
class DefaultAudioManager(context: Context, mediaPlayer: IMediaPlayer<*>?) : IAudioManager {

    companion object {
        /**
         * 获取当前系统音量
         */
        fun getCurrentVolume(context: Context): Float {
            val audioManager = context.getSystemService(Service.AUDIO_SERVICE) as AudioManager
            val streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
            return streamVolume * 1.000f / maxVolume
        }
    }

    private val audioManager: AudioManager = context.getSystemService(Service.AUDIO_SERVICE) as AudioManager
    private val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
    private var audioFocusRequest: AudioFocusRequest? = null

    override var onAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener = DefaultAudioFocusChangeListener(context, this, mediaPlayer)

    override fun mute() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND)
    }

    override fun setVolume(volume: Int) {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val level = volume % maxVolume
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level.toInt(), AudioManager.FLAG_PLAY_SOUND)
    }

    override fun getMaxVolume(): Float {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
    }

    override fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(onAudioFocusChangeListener)
                    .build()
                    .also {
                        audioManager.requestAudioFocus(it)
                    }
        } else {
            audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
    }

    override fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let {
                audioManager.abandonAudioFocusRequest(it)
            }
        } else {
            audioManager.abandonAudioFocus(onAudioFocusChangeListener)
        }
    }
}