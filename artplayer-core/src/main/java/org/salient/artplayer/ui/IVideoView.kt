package org.salient.artplayer.ui

import android.graphics.Bitmap
import android.view.TextureView
import android.widget.ImageView
import androidx.lifecycle.LifecycleObserver
import org.salient.artplayer.audio.IAudioManager
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.player.IMediaPlayer

/**
 * description: 视频播放容器抽象类
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
interface IVideoView : TextureView.SurfaceTextureListener, LifecycleObserver {

    /**
     * 封面
     */
    val cover: ImageView

    /**
     * 播放器内核
     */
    var mediaPlayer: IMediaPlayer<*>?

    /**
     * 是否正在播放
     */
    val isPlaying: Boolean

    /**
     * 当前位置
     */
    val currentPosition: Long

    /**
     * 视频时长
     */
    val duration: Long

    /**
     * 视频高度
     */
    val videoHeight: Int

    /**
     * 视频宽度
     */
    val videoWidth: Int

    /**
     * 音频管理器
     */
    var audioManager : IAudioManager

    /**
     * 播放器状态
     */
    val playerState: PlayerState

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

    /**
     * 获取当前视图Bitmap
     */
    fun getBitmap(): Bitmap?
}