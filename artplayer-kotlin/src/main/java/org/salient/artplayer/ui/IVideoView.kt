package org.salient.artplayer.ui

import android.view.TextureView
import org.salient.artplayer.audio.IAudioManager
import org.salient.artplayer.player.IMediaPlayer

/**
 * description: 视频播放容器抽象类
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
interface IVideoView : TextureView.SurfaceTextureListener {

    var mediaPlayer: IMediaPlayer<*>?
    var audioManager : IAudioManager
    val isPlaying: Boolean

    /**
     * 开始播放
     */
    fun start()

    /**
     * 播放
     */
    fun play()

    /**
     * 重播
     */
    fun replay()

    /**
     * 暂停
     */
    fun pause()

    /**
     * 停止
     */
    fun stop()

    /**
     * 释放资源
     */
    fun release()

}