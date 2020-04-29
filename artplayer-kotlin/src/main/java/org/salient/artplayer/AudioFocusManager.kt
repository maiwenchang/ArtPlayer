package org.salient.artplayer

import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.util.Log

/**
 * Created by Mai on 2018/7/23
 * *
 * Description: 声音焦点变化管理类
 * *
 */
class AudioFocusManager : OnAudioFocusChangeListener {
    private val TAG = javaClass.simpleName
    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> Log.d(TAG, "AUDIOFOCUS_GAIN [" + this.hashCode() + "]")
            AudioManager.AUDIOFOCUS_LOSS -> {
                MediaPlayerManager.pause()
                Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]")
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]")
                MediaPlayerManager.pause()
            }
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> Log.d(TAG, "AUDIOFOCUS_GAIN_TRANSIENT [" + this.hashCode() + "]")
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK [" + this.hashCode() + "]")
        }
    }
}