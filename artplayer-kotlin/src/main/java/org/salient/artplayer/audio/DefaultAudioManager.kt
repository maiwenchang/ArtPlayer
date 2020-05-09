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
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
open class DefaultAudioManager(context: Context, mediaPlayer: IMediaPlayer<*>?) : IAudioManager {

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
        abandonAudioFocus()
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(onAudioFocusChangeListener)
                .build()
                .also {
                    audioManager.requestAudioFocus(it)
                }
    }

    override fun abandonAudioFocus() {
        audioFocusRequest?.let {
            audioManager.abandonAudioFocusRequest(it)
        }
    }
}