package org.salient.artplayer.conduction

/**
 * description: 播放状态
 * 参考：@see <a>https://developer.android.google.cn/reference/android/media/MediaPlayer#state-diagram</a>
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
sealed class PlayerState(val code: Int) {

    object IDLE :
            PlayerState(1 shl 1)

    object INITIALIZED :
            PlayerState(1 shl 2)

    object PREPARING :
            PlayerState(1 shl 3)

    object PREPARED :
            PlayerState(1 shl 4)

    object STARTED :
            PlayerState(1 shl 5)

    object PAUSED :
            PlayerState(1 shl 6)

    object STOPPED :
            PlayerState(1 shl 7)

    object COMPLETED :
            PlayerState(1 shl 8)

    object ERROR :
            PlayerState(1 shl 9)

    object END :
            PlayerState(1 shl 10)

}