package org.salient.artplayer.ui

import android.view.TextureView
import androidx.lifecycle.LifecycleObserver
import org.salient.artplayer.audio.IAudioManager
import org.salient.artplayer.player.IMediaPlayer

/**
 * description: 视频播放容器抽象类
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
interface IVideoView : TextureView.SurfaceTextureListener, LifecycleObserver {

    var mediaPlayer: IMediaPlayer<*>?
    val isPlaying: Boolean
    var audioManager : IAudioManager

    /**
     * 初始化播放
     */
    fun prepare()

    /**
     * 播放
     */
    fun start()

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

    /**
     * 重置
     */
    fun reset()

    /**
     * 跳转
     */
    fun seekTo(time: Long)

    /**
     * 设置音量
     *
     */
    fun setVolume(volume: Int)

    /**
     * 绑定视图
     */
    fun attach()
}