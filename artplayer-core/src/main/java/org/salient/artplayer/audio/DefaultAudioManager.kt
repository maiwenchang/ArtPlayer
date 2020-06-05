package org.salient.artplayer.audio

import android.app.Service
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import org.salient.artplayer.player.IMediaPlayer
import java.lang.ref.WeakReference

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

    override var onAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener = DefaultAudioFocusChangeListener(WeakReference(context), this, mediaPlayer)

    override fun mute() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND)
    }

    override fun setVolume(volume: Int) {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val level = volume % maxVolume
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level.toInt(), AudioManager.FLAG_PLAY_SOUND)
    }

    override fun getVolume(): Int {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    override fun getMaxVolume(): Int {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    override fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(false)
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