package org.salient.artplayer;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.view.Surface;

import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * Created by Mai on 2018/8/13
 * *
 * Description:
 * *
 */
public class ExoPlayer extends AbsMediaPlayer {

    private SimpleExoPlayer mediaPlayer;

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
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setVideoSurface(surface);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume((leftVolume + rightVolume) / 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void OpenVolume() {
        try {
            if (mediaPlayer != null) {
                VideoView currentFloor = MediaPlayerManager.instance().getCurrentVideoView();
                if (currentFloor == null) return;
                Context context = currentFloor.getContext();
                if (context == null) return;
                AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
                if (audioManager == null) return;
                float streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float volume = streamVolume * 1.000f / maxVolume;
                mediaPlayer.setVolume(volume);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CloseVolume() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mute(boolean isMute) {
        if (isMute) {
            CloseVolume();
        } else {
            OpenVolume();
        }
    }

    @Override
    public void setLooping(boolean isLoop) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.getPlaybackLooper();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
