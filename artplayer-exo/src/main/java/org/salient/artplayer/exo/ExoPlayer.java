package org.salient.artplayer.exo;

import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import org.salient.artplayer.AbsMediaPlayer;
import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.VideoView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mai on 2018/8/13
 * *
 * Description:
 * *
 */
public class ExoPlayer extends AbsMediaPlayer implements Player.EventListener, AnalyticsListener {

    private SimpleExoPlayer mediaPlayer;

    private Context mAppContext;

    private Timer mProgressTimer;
    private BufferedPercentageTask mBufferedPercentageTask;


    public ExoPlayer(@NonNull Context context) {
        this.mAppContext = context.getApplicationContext();
    }

    @Override
    public void start() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setPlayWhenReady(true);
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
            // 1. Create a default TrackSelector
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
            DefaultTrackSelector mTrackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            boolean preferExtensionDecoders = true;
            boolean useExtensionRenderers = true;//是否开启扩展
            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode = useExtensionRenderers
                    ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                    : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                    : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;

            DefaultRenderersFactory rendererFactory = new DefaultRenderersFactory(mAppContext, extensionRendererMode);
            DefaultLoadControl loadControl = new DefaultLoadControl();
            mediaPlayer = ExoPlayerFactory.newSimpleInstance(rendererFactory, mTrackSelector, loadControl, null);
            mediaPlayer.addListener(this);
            mediaPlayer.addAnalyticsListener(this);
            if (MediaPlayerManager.instance().isMute()) {
                mute(true);
            }
//            if (mSpeedPlaybackParameters != null) { // todo speed
//                mediaPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
//            }
            //设置播放Url
            String  url;
            Object dataSource = getDataSource();
            if (dataSource instanceof RawResourceDataSource) {//Android raw file
                RawResourceDataSource rawDataSource = (RawResourceDataSource) dataSource;
                url = rawDataSource.getUri() != null ? rawDataSource.getUri().toString() : "";
            } else {
                url = dataSource.toString();
            }
            MediaSource mediaSource = ExoSourceManager.newInstance(mAppContext, getHeaders()).getMediaSource(
                    url, false, false, MediaPlayerManager.instance().isLooping(), null
            );
            mediaPlayer.prepare(mediaSource);
            mediaPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            e.printStackTrace();
            MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.ERROR);
        }
    }

    @Override
    public void pause() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setPlayWhenReady(false);
                MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PAUSED);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPlaying() {
        try {
            if (mediaPlayer == null) return false;
            int state = mediaPlayer.getPlaybackState();
            if (state == Player.STATE_BUFFERING || state == Player.STATE_READY) {
                return mediaPlayer.getPlayWhenReady();
            }
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
            startProgressTimer();
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
                if (isLoop) {
                    mediaPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                } else {
                    mediaPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
        Log.d(getClass().getSimpleName(), "onTimelineChanged ; ");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d(getClass().getSimpleName(), "onTracksChanged ; ");
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        if (isLoading) {
            MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PREPARING);
        } else if (MediaPlayerManager.instance().getPlayerState() == MediaPlayerManager.PlayerState.PREPARING) {
            MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PREPARED);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        try {
            if (mediaPlayer == null) return;
            int state = mediaPlayer.getPlaybackState();
            switch (state) {
                case Player.STATE_IDLE:
                    MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.IDLE);
                    break;
                case Player.STATE_READY:
                    if (mediaPlayer.getPlayWhenReady()) {
                        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PLAYING);
                    }
                    break;
                case Player.STATE_BUFFERING:
                    startProgressTimer();
                    break;
                case Player.STATE_ENDED:
                    MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.PLAYBACK_COMPLETED);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    /////////////////////////////////////AudioRendererEventListener/////////////////////////////////////////////

    @Override
    public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onTimelineChanged(EventTime eventTime, int reason) {

    }

    @Override
    public void onPositionDiscontinuity(EventTime eventTime, int reason) {

    }

    @Override
    public void onSeekStarted(EventTime eventTime) {

    }

    @Override
    public void onSeekProcessed(EventTime eventTime) {

    }

    @Override
    public void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters) {

    }

    @Override
    public void onRepeatModeChanged(EventTime eventTime, int repeatMode) {

    }

    @Override
    public void onShuffleModeChanged(EventTime eventTime, boolean shuffleModeEnabled) {

    }

    @Override
    public void onLoadingChanged(EventTime eventTime, boolean isLoading) {

    }

    @Override
    public void onPlayerError(EventTime eventTime, ExoPlaybackException error) {
        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.ERROR);
    }

    @Override
    public void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadStarted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {

    }

    @Override
    public void onLoadCompleted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {

    }

    @Override
    public void onLoadCanceled(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {

    }

    @Override
    public void onLoadError(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {

    }

    @Override
    public void onDownstreamFormatChanged(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {

    }

    @Override
    public void onUpstreamDiscarded(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {

    }

    @Override
    public void onMediaPeriodCreated(EventTime eventTime) {

    }

    @Override
    public void onMediaPeriodReleased(EventTime eventTime) {

    }

    @Override
    public void onReadingStarted(EventTime eventTime) {

    }

    @Override
    public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {

    }

    @Override
    public void onViewportSizeChange(EventTime eventTime, int width, int height) {

    }

    @Override
    public void onNetworkTypeChanged(EventTime eventTime, @Nullable NetworkInfo networkInfo) {

    }

    @Override
    public void onMetadata(EventTime eventTime, Metadata metadata) {

    }

    @Override
    public void onDecoderEnabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {

    }

    @Override
    public void onDecoderInitialized(EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {

    }

    @Override
    public void onDecoderInputFormatChanged(EventTime eventTime, int trackType, Format format) {

    }

    @Override
    public void onDecoderDisabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {

    }

    @Override
    public void onAudioSessionId(EventTime eventTime, int audioSessionId) {

    }

    @Override
    public void onAudioUnderrun(EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

    }

    @Override
    public void onDroppedVideoFrames(EventTime eventTime, int droppedFrames, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        MediaPlayerManager.instance().onVideoSizeChanged(width, height);
    }

    @Override
    public void onRenderedFirstFrame(EventTime eventTime, Surface surface) {

    }

    @Override
    public void onDrmKeysLoaded(EventTime eventTime) {

    }

    @Override
    public void onDrmSessionManagerError(EventTime eventTime, Exception error) {

    }

    @Override
    public void onDrmKeysRestored(EventTime eventTime) {

    }

    @Override
    public void onDrmKeysRemoved(EventTime eventTime) {

    }

    /////////////////////////////////////AudioRendererEventListener  End /////////////////////////////////////////////

    public void startProgressTimer() {
        cancelProgressTimer();
        mProgressTimer = new Timer();
        mBufferedPercentageTask = new BufferedPercentageTask();
        mProgressTimer.schedule(mBufferedPercentageTask, 0, 300);
    }

    public void cancelProgressTimer() {
        if (mProgressTimer != null) {
            mProgressTimer.cancel();
        }
        if (mBufferedPercentageTask != null) {
            mBufferedPercentageTask.cancel();
        }
    }

    public class BufferedPercentageTask extends TimerTask {
        @Override
        public void run() {
            if (mediaPlayer == null) return;
            int percentage = mediaPlayer.getBufferedPercentage();
            if (percentage <= 100) {
                MediaPlayerManager.PlayerState playerState = MediaPlayerManager.instance().getPlayerState();
                if (playerState == MediaPlayerManager.PlayerState.PLAYING || playerState == MediaPlayerManager.PlayerState.PAUSED) {
                    if (MediaPlayerManager.instance().getCurrentControlPanel() != null) {
                        MediaPlayerManager.instance().getCurrentControlPanel().onBufferingUpdate(percentage);
                    }
                }
            } else {
                cancelProgressTimer();
            }
        }
    }
}
