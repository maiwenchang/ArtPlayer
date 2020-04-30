package org.salient.artplayer.player

import android.media.MediaPlayer
import android.view.Surface
import androidx.lifecycle.LiveData
import org.salient.artplayer.bean.VideoInfo
import org.salient.artplayer.bean.VideoSize
import org.salient.artplayer.conduction.PlayerState

/**
 * Created by Mai on 2018/7/10
 * *
 * Description: 视频播放器的抽象基类
 * *
 */
interface IMediaPlayer<T> {

    var impl: T
    val isPlaying: Boolean
    val currentPosition: Long
    val duration: Long
    val playerStateLD: LiveData<PlayerState> //播放状态
    val videoSizeLD: LiveData<VideoSize>
    val bufferingProgressLD: LiveData<Int>
    val videoInfoLD: LiveData<VideoInfo>
    val videoErrorLD: LiveData<VideoInfo>
    fun start()
    fun prepare()
    fun pause()
    fun stop()
    fun seekTo(time: Long)
    fun release()
    fun setVolume(volume: Float)
    fun setLooping(isLoop: Boolean)
    fun setSurface(surface: Surface?)

}