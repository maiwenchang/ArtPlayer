package org.salient.artplayer.ui

import android.view.ViewGroup

/**
 * description: 视频容器-小窗模式抽象
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-22 9:06 AM.
 */
interface IFullscreenVideoView {

    /**
     * 源视图
     */
    val origin: VideoView?

    /**
     * 布局参数
     */
    val params: ViewGroup.LayoutParams?


}