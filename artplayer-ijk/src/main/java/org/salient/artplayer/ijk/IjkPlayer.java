package org.salient.artplayer.ijk;

import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.view.Surface;

import org.salient.artplayer.AbsMediaPlayer;
import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.VideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * Created by Mai on 2018/8/10
 * *
 * Description: IjkMediaPlayer for ArtVideoPlayer
 * *
 */
public class IjkPlayer extends AbsMediaPlayer implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnVideoSizeChangedListener {

    private tv.danmaku.ijk.media.player.IjkMediaPlayer mediaPlayer;

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
            mediaPlayer = new tv.danmaku.ijk.media.player.IjkMediaPlayer();
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
            if (dataSource instanceof AssetFileDescriptor) {//Android raw/assets file
                AssetFileDescriptor fd = (AssetFileDescriptor) dataSource;
                RawDataSourceProvider sourceProvider = new RawDataSourceProvider(fd);
                mediaPlayer.setDataSource(sourceProvider);
            } else if (dataSource instanceof RawDataSourceProvider) {// IMediaDataSource
                mediaPlayer.setDataSource((IMediaDataSource) dataSource);
            } else if (dataSource != null && getHeaders() != null) {//url with headers
                mediaPlayer.setDataSource(dataSource.toString(), getHeaders());
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
                mediaPlayer = null;
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
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PREPARED);
        MediaPlayerManager.instance().start();
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PLAYBACK_COMPLETED);
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        if (MediaPlayerManager.instance().getCurrentControlPanel() != null) {
            MediaPlayerManager.instance().getCurrentControlPanel().onBufferingUpdate(percent);
        }
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        if (MediaPlayerManager.instance().getCurrentControlPanel() != null) {
            MediaPlayerManager.instance().getCurrentControlPanel().onSeekComplete();
        }
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.ERROR);
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
        if (MediaPlayerManager.instance().getCurrentControlPanel() != null) {
            MediaPlayerManager.instance().getCurrentControlPanel().onInfo(what, extra);
        }
        return false;
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
        int videoWidth = iMediaPlayer.getVideoWidth();
        int videoHeight = iMediaPlayer.getVideoHeight();
        if (videoWidth != 0 && videoHeight != 0) {
            MediaPlayerManager.instance().onVideoSizeChanged(videoWidth, videoHeight);
        }
    }

    // +++++++++++++++++++++++++++++ ijk only ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void setSpeed(float speed) {
        mediaPlayer.setSpeed(speed);
    }


    public long getTcpSpeed() {
        return mediaPlayer.getTcpSpeed();
    }

    public void setEnableMediaCodec(boolean isEnable) {
        int value = isEnable ? 1 : 0;
        mediaPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value);//开启硬解码
        mediaPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value);
        mediaPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value);
    }

    // +++++++++++++++++++++++++++++ ijk only ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
}
