package org.salient.artplayer.conduction

/**
 * description: 播放状态
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
sealed class PlayerState(val code: Int) {
    object IDLE : PlayerState(1 shl 1)
    object PREPARING : PlayerState(1 shl 2)
    object PREPARED : PlayerState(1 shl 3)
    object PLAYING : PlayerState(1 shl 4)
    object PAUSED : PlayerState(1 shl 5)
    object STOPPED : PlayerState(1 shl 5)
    object COMPLETED : PlayerState(1 shl 6)
    object SEEK_COMPLETE : PlayerState(1 shl 7)
    object ERROR : PlayerState(1 shl 8)

}