package org.salient.artplayer.player

import android.view.Surface
import android.view.SurfaceHolder
import androidx.lifecycle.LiveData
import org.salient.artplayer.bean.VideoInfo
import org.salient.artplayer.bean.VideoSize
import org.salient.artplayer.conduction.PlayerState

/**
 * description: 视频播放器的抽象基类
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
interface IMediaPlayer<T> {

    var impl: T
    val isPlaying: Boolean
    val currentPosition: Long
    val duration: Long
    val videoHeight: Int
    val videoWidth: Int
    val playerState:PlayerState
    val playerStateLD: LiveData<PlayerState>
    val videoSizeLD: LiveData<VideoSize>
    val bufferingProgressLD: LiveData<Int>
    val videoInfoLD: LiveData<VideoInfo>
    val videoErrorLD: LiveData<VideoInfo>
    fun start()
    fun prepare()
    fun prepareAsync()
    fun pause()
    fun stop()
    fun seekTo(time: Long)
    fun reset()
    fun release()
    fun setVolume(volume: Float)
    fun setLooping(isLoop: Boolean)
    fun setSurface(surface: Surface?)
    fun setDisplay(surfaceHolder: SurfaceHolder)

}