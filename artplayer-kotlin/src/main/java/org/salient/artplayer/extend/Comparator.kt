package org.salient.artplayer.extend

import org.salient.artplayer.VideoViewOld

/**
 * description: 比较器
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
interface Comparator {
    fun compare(videoViewOld: VideoViewOld?): Boolean
}