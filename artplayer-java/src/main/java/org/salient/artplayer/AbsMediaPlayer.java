package org.salient.artplayer;

import android.view.Surface;

import java.util.Map;

/**
 *  Created by Mai on 2018/7/10
 * *
 *  Description: 视频播放器的抽象基类
 * *
 */
public abstract class AbsMediaPlayer {

    private Object dataSource;//正在播放的当前url或uri

    private Map<String, String> mHeaders;

    public abstract void start();

    public abstract void prepare();

    public abstract void pause();

    public abstract boolean isPlaying();

    public abstract void seekTo(long time);

    public abstract void release();

    public abstract long getCurrentPosition();

    public abstract long getDuration();

    public abstract void setSurface(Surface surface);

    public abstract void setVolume(float leftVolume, float rightVolume);

    public abstract void mute(boolean isMute);

    public abstract void setLooping(boolean isLoop);

    public Object getDataSource() {
        return dataSource;
    }

    /**
     * 设置播放地址
     *
     * @param dataSource 播放地址
     */
    public void setDataSource(Object dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    /**
     * 设置请求头
     *
     * @param headers 播放地址请求头
     */
    public void setHeaders(Map<String, String> headers) {
        this.mHeaders = mHeaders;
    }
}
