package org.salient.artplayer.ui

import android.media.AudioManager
import android.media.MediaPlayer
import android.view.TextureView
import org.salient.artplayer.audio.IAudioManager
import org.salient.artplayer.bean.VideoBean
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.player.IMediaPlayer

/**
 * description:
 *
 * @author 麦文昌(A01031)
 *
 * @date 2020-02-14 15:24.
 */
interface IVideoView : TextureView.SurfaceTextureListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnVideoSizeChangedListener {

    var videoBean: VideoBean?
    var playingState : PlayerState
    var mediaPlayer: IMediaPlayer<*>?
    var audioManager : IAudioManager

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