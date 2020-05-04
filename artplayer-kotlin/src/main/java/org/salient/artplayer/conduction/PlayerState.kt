package org.salient.artplayer.conduction

/**
 * description: 播放状态
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
sealed class PlayerState {
    object ERROR : PlayerState()
    object IDLE : PlayerState()
    object PREPARING : PlayerState()
    object PREPARED : PlayerState()
    object PLAYING : PlayerState()
    object PAUSED : PlayerState()
    object STOP : PlayerState()
    object COMPLETED : PlayerState()
    object SEEK_COMPLETE : PlayerState()

}