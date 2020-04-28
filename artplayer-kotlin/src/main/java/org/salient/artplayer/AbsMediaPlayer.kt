package org.salient.artplayer

import android.view.Surface

/**
 * Created by Mai on 2018/7/10
 * *
 * Description: 视频播放器的抽象基类
 * *
 */
abstract class AbsMediaPlayer {
    /**
     * 设置播放地址
     *
     * @param dataSource 播放地址
     */
    var dataSource //正在播放的当前url或uri
            : Any? = null
    var headers: Map<String, String>? = null
        private set

    abstract fun start()
    abstract fun prepare()
    abstract fun pause()
    abstract val isPlaying: Boolean
    abstract fun seekTo(time: Long)
    abstract fun release()
    abstract val currentPosition: Long
    abstract val duration: Long
    abstract fun setSurface(surface: Surface?)
    abstract fun setVolume(leftVolume: Float, rightVolume: Float)
    abstract fun mute(isMute: Boolean)
    abstract fun setLooping(isLoop: Boolean)

    /**
     * 设置请求头
     *
     * @param headers 播放地址请求头
     */
    fun setHeaders(headers: Map<String?, String?>?) {
        this.headers = this.headers
    }
}