package org.salient.artplayer.bean

/**
 * description:
 *
 * @author 麦文昌(A01031)
 *
 * @date 2020-02-14 15:24.
 */
class VideoBean {

    var url: String? = null

    var headers: Map<String, String>? = null //当前视频地址的请求头

    var data: Any? = null //video data like id, title, cover picture...

    var dataSourceObject: Any? = null// video dataSource (Http url or Android assets file) would be posted to MediaPlayer.

}