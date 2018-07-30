package org.salient.artplayer;

import android.content.res.AssetFileDescriptor;
import android.view.Surface;

import java.util.Map;

/**
 * > Created by Mai on 2018/7/10
 * *
 * > Description: 视频播放器的抽象基类
 * *
 */
public abstract class AbsMediaPlayer {

    protected Object dataSource;//正在播放的当前url或uri

    protected Map<String, String> mHeaders;

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

    public Object getDataSource() {
        return dataSource;
    }

    /**
     * 设置播放地址
     * @param path 播放地址
     * @param headers 播放地址请求头
     */
    public void setDataSource(String path, Map<String, String> headers) {
        this.dataSource = path;
        this.mHeaders = headers;
    }

    /**
     * 用于播放raw和asset里面的视频文件
     */
    public void setDataSource(AssetFileDescriptor fd){
        this.dataSource = fd;
    }
}
