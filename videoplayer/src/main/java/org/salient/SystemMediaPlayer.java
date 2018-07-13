package org.salient;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * > Created by Mai on 2018/7/10
 * *
 * > Description: 系统默认播放器
 * *
 */
public class SystemMediaPlayer extends AbsMediaPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnVideoSizeChangedListener {

    private MediaPlayer mediaPlayer;

    @Override
    public void start() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.start();
                MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PLAYING);
                mute(MediaPlayerManager.instance().isMute);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepare() {
        try {
            MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PREPARING);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (dataSourceObjects.length > 1) {
                mediaPlayer.setLooping((boolean) dataSourceObjects[1]);
            }
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            Class<MediaPlayer> clazz = MediaPlayer.class;
            Method method = clazz.getDeclaredMethod("setDataSource", String.class, Map.class);
            if (dataSourceObjects.length > 2) {
                method.invoke(mediaPlayer, currentDataSource.toString(), dataSourceObjects[2]);
            } else {
                method.invoke(mediaPlayer, currentDataSource.toString(), null);
            }
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PAUSED);
        }
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long time) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo((int) time);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.IDLE);
        }
    }

    @Override
    public long getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public long getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    @Override
    public void setSurface(Surface surface) {
        if (mediaPlayer != null) {
            mediaPlayer.setSurface(surface);
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    public void OpenVolume() {
        try {
            if (mediaPlayer != null) {
                VideoView currentFloor = VideoLayerManager.getCurrentFloor();
                if (currentFloor == null) return;
                Context context = currentFloor.getContext();
                if (context == null) return;
                AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
                if (audioManager == null) return;
                float streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float volume = streamVolume * 1.000f / maxVolume;
                mediaPlayer.setVolume(volume, volume);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CloseVolume() {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(0, 0);
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
    public void onPrepared(MediaPlayer mediaPlayer) {
        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PREPARED);
        start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PLAYBACK_COMPLETED);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, final int percent) {
        if (VideoLayerManager.getCurrentControlPanel() != null) {
            VideoLayerManager.getCurrentControlPanel().onBufferingUpdate(percent);
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        if (VideoLayerManager.getCurrentControlPanel() != null) {
            VideoLayerManager.getCurrentControlPanel().onSeekComplete();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, final int what, final int extra) {
        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.ERROR);
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, final int what, final int extra) {
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            if (MediaPlayerManager.instance().getCurrentState() == MediaPlayerManager.PlayerState.PREPARING
                    || MediaPlayerManager.instance().getCurrentState() == MediaPlayerManager.PlayerState.PREPARING_CHANGING_URL) {
                MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PREPARED);
            }
        } else {
            if (VideoLayerManager.getCurrentControlPanel() != null) {
                VideoLayerManager.getCurrentControlPanel().onInfo(what, extra);
            }
        }
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        MediaPlayerManager.instance().currentVideoWidth = width;
        MediaPlayerManager.instance().currentVideoHeight = height;

        MediaPlayerManager.instance().onVideoSizeChanged();

    }
}
