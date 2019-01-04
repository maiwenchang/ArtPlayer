package org.salient.artvideoplayer.bean;

/**
 * Created by Mai on 2018/7/17
 * *
 * Description:
 * *
 */
public class VideoBean {

    /**
     * hightUrl :
     * image : http://img5.mtime.cn/mg/2018/06/27/094527.12278962.jpg
     * length : 61
     * title : 摩天营救 定档预告片
     * url : http://vfx.mtime.cn/Video/2018/06/27/mp4/180627094726195356.mp4
     * videoId : 71043
     */

    private String hightUrl;
    private String image;
    private int length;
    private String title;
    private String url;
    private int videoId;
    private int listPosition;

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public String getHightUrl() {
        return hightUrl;
    }

    public void setHightUrl(String hightUrl) {
        this.hightUrl = hightUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }
}
