package org.salient.artplayer;

import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *  Created by Mai on 2018/7/10
 * *
 *  Description: 系统默认播放器
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
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            if (MediaPlayerManager.instance().isMute()) {
                mute(true);
            }
            if (MediaPlayerManager.instance().isLooping()) {
                setLooping(true);
            }
            Object dataSource = getDataSource();
            if (dataSource instanceof AssetFileDescriptor) {//Android assets file
                AssetFileDescriptor fd = (AssetFileDescriptor) dataSource;
                mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            } else if (dataSource != null && getHeaders() != null) {//url with headers
                Class<MediaPlayer> clazz = MediaPlayer.class;
                Method method = clazz.getDeclaredMethod("setDataSource", String.class, Map.class);
                method.invoke(mediaPlayer, dataSource.toString(), getHeaders());
            } else if (dataSource != null) {
                mediaPlayer.setDataSource(dataSource.toString());
            }
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.ERROR);
        }
    }

    @Override
    public void pause() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PAUSED);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPlaying() {
        try {
            return mediaPlayer.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.IDLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getCurrentPosition() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getCurrentPosition();
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getDuration();
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setSurface(Surface surface) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setSurface(surface);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(leftVolume, rightVolume);
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
                mediaPlayer.setVolume(volume, volume);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CloseVolume() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(0, 0);
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
                mediaPlayer.setLooping(isLoop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PREPARED);
        MediaPlayerManager.instance().start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PLAYBACK_COMPLETED);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, final int percent) {
        if (MediaPlayerManager.instance().getCurrentControlPanel() != null) {
            MediaPlayerManager.instance().getCurrentControlPanel().onBufferingUpdate(percent);
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        if (MediaPlayerManager.instance().getCurrentControlPanel() != null) {
            MediaPlayerManager.instance().getCurrentControlPanel().onSeekComplete();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, final int what, final int extra) {
        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.ERROR);
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, final int what, final int extra) {
        if (MediaPlayerManager.instance().getCurrentControlPanel() != null) {
            MediaPlayerManager.instance().getCurrentControlPanel().onInfo(what, extra);
        }
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        MediaPlayerManager.instance().onVideoSizeChanged(width, height);
    }
}
