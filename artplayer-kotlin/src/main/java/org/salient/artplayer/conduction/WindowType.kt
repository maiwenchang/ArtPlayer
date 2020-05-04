package org.salient.artplayer.conduction

/**
 * description: 窗口模式
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
sealed class WindowType {

    object NORMAL : WindowType()

    object LIST : WindowType()

    object FULLSCREEN : WindowType()

    object TINY : WindowType()

}