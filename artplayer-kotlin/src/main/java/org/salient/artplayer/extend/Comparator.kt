package org.salient.artplayer.extend

import org.salient.artplayer.VideoViewOld

/**
 * Created by Mai on 2018/7/26
 * *
 * Description:
 * *
 */
interface Comparator {
    fun compare(videoViewOld: VideoViewOld?): Boolean
}