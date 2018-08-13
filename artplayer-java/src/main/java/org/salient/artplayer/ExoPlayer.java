package org.salient.artplayer;

import android.view.Surface;

/**
 * Created by Mai on 2018/8/13
 * *
 * Description:
 * *
 */
public class ExoPlayer extends AbsMediaPlayer{

    @Override
    public void start() {

    }

    @Override
    public void prepare() {

    }

    @Override
    public void pause() {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void seekTo(long time) {

    }

    @Override
    public void release() {

    }

    @Override
    public long getCurrentPosition() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public void setSurface(Surface surface) {

    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {

    }

    @Override
    public void mute(boolean isMute) {

    }

    @Override
    public void setLooping(boolean isLoop) {

    }
}
