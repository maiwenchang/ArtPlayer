package org.salient;

import android.view.Surface;

/**
 * > Created by Mai on 2018/7/10
 * *
 * > Description: 视频播放器的抽象基类
 * *
 */
public abstract class AbsMediaPlayer {

    protected Object currentDataSource;//正在播放的当前url或uri

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

    public Object getCurrentDataSource() {
        return currentDataSource;
    }

    public void setCurrentDataSource(Object currentDataSource) {
        this.currentDataSource = currentDataSource;
    }
}
