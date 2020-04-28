package org.salient.artplayer;

/**
 *  Created by Mai on 2018/7/10
 * *
 *  Description: 视频播放器状态回调
 * *
 */
public interface MediaStateListener {

    // all possible media player internal states
//    ERROR
//    IDLE
//    PREPARING
//    PREPARED
//    PLAYING
//    PAUSED
//    PLAYBACK_COMPLETED

    void onStateError();

    void onStateIdle();

    void onStatePreparing();

    void onStatePrepared();

    void onStatePlaying();

    void onStatePaused();

    void onStatePlaybackCompleted();

    void onSeekComplete();

    void onBufferingUpdate(int progress);

    void onInfo(int what, int extra);

    void onProgressUpdate(int progress, long position, long duration);

    void onEnterSecondScreen();

    void onExitSecondScreen();
}
