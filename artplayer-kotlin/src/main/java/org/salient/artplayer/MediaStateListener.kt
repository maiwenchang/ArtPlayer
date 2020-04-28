package org.salient.artplayer

/**
 * Created by Mai on 2018/7/10
 * *
 * Description: 视频播放器状态回调
 * *
 */
interface MediaStateListener {
    // all possible media player internal states
//    ERROR
//    IDLE
//    PREPARING
//    PREPARED
//    PLAYING
//    PAUSED
//    PLAYBACK_COMPLETED
    fun onStateError()

    fun onStateIdle()
    fun onStatePreparing()
    fun onStatePrepared()
    fun onStatePlaying()
    fun onStatePaused()
    fun onStatePlaybackCompleted()
    fun onSeekComplete()
    fun onBufferingUpdate(progress: Int)
    fun onInfo(what: Int, extra: Int)
    fun onProgressUpdate(progress: Int, position: Long, duration: Long)
    fun onEnterSecondScreen()
    fun onExitSecondScreen()
}