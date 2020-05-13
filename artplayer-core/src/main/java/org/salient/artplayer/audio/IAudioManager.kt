package org.salient.artplayer.audio

import android.media.AudioManager

/**
 * description: 音频管理
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
interface IAudioManager {

    /**
     * 焦点变化监听
     */
    var onAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener

    /**
     * 静音
     */
    fun mute()

    /**
     * 设置音量
     * @param volume 0 ~ maxVolume
     */
    fun setVolume(volume: Int)

    /**
     * 获取最大音量
     */
    fun getMaxVolume(): Float

    /**
     * 申请音频焦点
     */
    fun requestAudioFocus()

    /**
     * 放弃音频焦点
     */
    fun abandonAudioFocus()

}