package org.salient.artvideoplayer.bean

/**
 * Created by Mai on 2018/7/17
 * *
 * Description:
 * *
 */
class MovieData {
    var attention: List<AttentionBean>? = null
    var moviecomings: List<MoviecomingsBean>? = null

    class AttentionBean {
        /**
         * actor1 : 道恩·强森
         * actor2 : 内芙·坎贝尔
         * director : 罗森·马歇尔·瑟伯
         * id : 234573
         * image : http://img5.mtime.cn/mt/2018/07/11/151451.75772708_1280X720X2.jpg
         * isFilter : false
         * isTicket : false
         * isVideo : true
         * locationName : 美国
         * rDay : 20
         * rMonth : 7
         * rYear : 2018
         * releaseDate : 7月20日上映
         * title : 摩天营救
         * type : 动作 / 冒险 / 剧情
         * videoCount : 3
         * videos : [{"hightUrl":"","image":"http://img5.mtime.cn/mg/2018/06/27/094527.12278962.jpg","length":61,"title":"摩天营救 定档预告片","url":"http://vfx.mtime.cn/Video/2018/06/27/mp4/180627094726195356.mp4","videoId":71043},{"hightUrl":"","image":"http://img5.mtime.cn/mg/2018/02/05/144143.61155408.jpg","length":159,"title":"摩天营救 中文版预告片","url":"http://vfx.mtime.cn/Video/2018/02/05/mp4/180205170620160029.mp4","videoId":69545},{"hightUrl":"","image":"http://img5.mtime.cn/mg/2018/02/03/085022.69184529.jpg","length":123,"title":"摩天营救 先导预告片","url":"http://vfx.mtime.cn/Video/2018/02/03/mp4/180203084924515223.mp4","videoId":69520}]
         * wantedCount : 853
         */
        var actor1: String? = null
        var actor2: String? = null
        var director: String? = null
        var id = 0
        var image: String? = null
        var isIsFilter = false
            private set
        var isIsTicket = false
            private set
        var isIsVideo = false
            private set
        var locationName: String? = null
        var rDay = 0
        var rMonth = 0
        var rYear = 0
        var releaseDate: String? = null
        var title: String? = null
        var type: String? = null
        var videoCount = 0
        var wantedCount = 0
        var videos: List<VideoBean>? = null

        fun setIsFilter(isFilter: Boolean) {
            isIsFilter = isFilter
        }

        fun setIsTicket(isTicket: Boolean) {
            isIsTicket = isTicket
        }

        fun setIsVideo(isVideo: Boolean) {
            isIsVideo = isVideo
        }

    }

    class MoviecomingsBean {
        /**
         * actor1 : 方城淞
         * actor2 :
         * director : 高建国
         * id : 258415
         * image : http://img5.mtime.cn/mt/2018/06/20/113940.34629012_1280X720X2.jpg
         * isFilter : false
         * isTicket : false
         * isVideo : false
         * locationName : 中国
         * rDay : 19
         * rMonth : 7
         * rYear : 2018
         * releaseDate : 7月19日上映
         * title : 八只鸡
         * type :
         * videoCount : 0
         * videos : []
         * wantedCount : 25
         */
        var actor1: String? = null
        var actor2: String? = null
        var director: String? = null
        var id = 0
        var image: String? = null
        var isIsFilter = false
            private set
        var isIsTicket = false
            private set
        var isIsVideo = false
            private set
        var locationName: String? = null
        var rDay = 0
        var rMonth = 0
        var rYear = 0
        var releaseDate: String? = null
        var title: String? = null
        var type: String? = null
        var videoCount = 0
        var wantedCount = 0
        var videos: List<VideoBean>? = null

        fun setIsFilter(isFilter: Boolean) {
            isIsFilter = isFilter
        }

        fun setIsTicket(isTicket: Boolean) {
            isIsTicket = isTicket
        }

        fun setIsVideo(isVideo: Boolean) {
            isIsVideo = isVideo
        }

    }
}