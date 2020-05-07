package org.salient.artplayer.conduction

/**
 * description: 缩放模式
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
sealed class ScaleType {
    
    object DEFAULT : ScaleType()

    object SCALE_ORIGINAL : ScaleType()

    object SCALE_16_9 : ScaleType()

    object SCALE_4_3 : ScaleType()

    object SCALE_MATCH_PARENT : ScaleType()

    object SCALE_CENTER_CROP : ScaleType()
}