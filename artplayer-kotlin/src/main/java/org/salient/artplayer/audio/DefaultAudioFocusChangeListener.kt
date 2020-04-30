package org.salient.artplayer.audio

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.util.Log
import org.salient.artplayer.MediaPlayerManagerOld
import org.salient.artplayer.player.IMediaPlayer

/**
 * Created by Mai on 2018/7/23
 * *
 * Description: 声音焦点变化管理类
 * *
 */
class DefaultAudioFocusChangeListener(
        private val context: Context,
        private val audioManager: IAudioManager,
        private val mediaPlayer: IMediaPlayer<*>?
) : OnAudioFocusChangeListener {
    private var playOnAudioFocus = true;
    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // 重新获得焦点，恢复正常音量，恢复播放
                if (playOnAudioFocus && mediaPlayer?.isPlaying != true) {
                    mediaPlayer?.start();
                } else if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer.setVolume(DefaultAudioManager.getCurrentVolume(context));
                }
                playOnAudioFocus = false;
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                //短暂失去焦点，无须停止播放，只适当降低播放器音量
                val duckVolume = DefaultAudioManager.getCurrentVolume(context) * 0.8f
                mediaPlayer?.setVolume(duckVolume);
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                //暂时失去焦点，暂停
                if (mediaPlayer?.isPlaying == true) {
                    playOnAudioFocus = true;
                    mediaPlayer.pause()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                //失去焦点，停止播放，如播放其他多媒体
                audioManager.abandonAudioFocus()
                playOnAudioFocus = false;
                mediaPlayer?.stop()
            }
        }
    }
}