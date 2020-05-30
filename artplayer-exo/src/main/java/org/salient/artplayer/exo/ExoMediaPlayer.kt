package org.salient.artplayer.exo

import android.content.Context
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.DefaultRenderersFactory.ExtensionRendererMode
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import org.salient.artplayer.bean.VideoInfo
import org.salient.artplayer.bean.VideoSize
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.player.IMediaPlayer
import java.io.IOException

/**
 * description: EXO视频播放器的封装
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
class ExoMediaPlayer(context: Context) : IMediaPlayer<SimpleExoPlayer>, Player.EventListener, AnalyticsListener {

    var mediaSource: com.google.android.exoplayer2.source.MediaSource? = null
        set(value) {
            field = value
            value?.let {
                playerStateLD.value = PlayerState.INITIALIZED
            }
        }

    override lateinit var impl: SimpleExoPlayer
    override var playWhenReady: Boolean = true
    override val playerStateLD: MutableLiveData<PlayerState> = MutableLiveData()
    override val videoSizeLD: MutableLiveData<VideoSize> = MutableLiveData()
    override val bufferingProgressLD: MutableLiveData<Int> = MutableLiveData()
    override val seekCompleteLD: MutableLiveData<Boolean> = MutableLiveData()
    override val videoInfoLD: MutableLiveData<VideoInfo> = MutableLiveData()
    override val videoErrorLD: MutableLiveData<VideoInfo> = MutableLiveData()

    init {
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory()
        val mTrackSelector = DefaultTrackSelector(context, videoTrackSelectionFactory)
        //是否开启扩展
        @ExtensionRendererMode val extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
        val rendererFactory = DefaultRenderersFactory(context).setExtensionRendererMode(extensionRendererMode)
        val loadControl = DefaultLoadControl()
        val builder = SimpleExoPlayer.Builder(context, rendererFactory)
        impl = builder.setTrackSelector(mTrackSelector)
                .setLoadControl(loadControl)
                .setBandwidthMeter(DefaultBandwidthMeter.Builder(context).build())
                .build()
        playerStateLD.value = PlayerState.IDLE
        impl.setAudioAttributes(AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build(), false)
        impl.addListener(this)
        impl.addAnalyticsListener(this)
    }

    override val isPlaying: Boolean
        get() {
            try {
                return impl.isPlaying
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    override val currentPosition: Long
        get() = try {
            impl.currentPosition
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    override val duration: Long
        get() = try {
            impl.duration
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    override val videoHeight: Int
        get() = try {
            impl.videoFormat?.height ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    override val videoWidth: Int
        get() = try {
            impl.videoFormat?.width ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    override val playerState: PlayerState
        get() = playerStateLD.value ?: PlayerState.IDLE

    override fun prepare() {
        prepareAsync()
    }

    override fun prepareAsync() {
        mediaSource?.let {
            playerStateLD.value = PlayerState.PREPARING
            impl.prepare(it)
            impl.playWhenReady = playWhenReady
        }
    }

    override fun start() {
        try {
            impl.playWhenReady = true
            playerStateLD.value = PlayerState.STARTED
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun pause() {
        try {
            impl.playWhenReady = false
            playerStateLD.value = PlayerState.PAUSED
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        try {
            impl.stop()
            playerStateLD.value = PlayerState.STOPPED
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun seekTo(time: Long) {
        try {
            impl.seekTo(time)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun release() {
        try {
            impl.release()
            playerStateLD.value = PlayerState.END
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun reset() {
        try {
            impl.stop()
            impl.seekTo(0)
            playerStateLD.value = PlayerState.IDLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setVolume(volume: Float) {
        try {
            impl.setVolume(volume)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setLooping(isLoop: Boolean) {
        try {
            impl.repeatMode = if (isLoop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setSurface(surface: Surface?) {
        try {
            impl.setVideoSurface(surface)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setDisplay(surfaceHolder: SurfaceHolder) {
        try {
            impl.setVideoSurfaceHolder(surfaceHolder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        Log.d(javaClass.name, "onLoadingChanged() called with: isLoading = $isLoading")
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.d(javaClass.name, "onPlayerStateChanged() called with: playWhenReady = $playWhenReady, playbackState = $playbackState")
        try {
            when (playbackState) {
                Player.STATE_IDLE -> playerStateLD.value = PlayerState.IDLE
                Player.STATE_READY -> if (playerState == PlayerState.PREPARING) {
                    playerStateLD.value = PlayerState.PREPARED
                }
                Player.STATE_BUFFERING -> bufferingProgressLD.value = impl.bufferedPercentage
                Player.STATE_ENDED -> playerStateLD.value = PlayerState.COMPLETED
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: ExoPlaybackException) {
        videoErrorLD.value = VideoInfo(error.type, error.type)
        playerStateLD.value = PlayerState.ERROR
    }

    override fun onVideoSizeChanged(eventTime: AnalyticsListener.EventTime, width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
        videoSizeLD.value = VideoSize(width, height)
    }

    override fun onSeekProcessed() {
        seekCompleteLD.value = true
    }

    override fun onLoadStarted(eventTime: AnalyticsListener.EventTime, loadEventInfo: MediaSourceEventListener.LoadEventInfo, mediaLoadData: MediaSourceEventListener.MediaLoadData) {
        Log.d(javaClass.name, "onLoadStarted() called with: eventTime = $eventTime, loadEventInfo = $loadEventInfo, mediaLoadData = $mediaLoadData")
    }

    override fun onLoadCompleted(eventTime: AnalyticsListener.EventTime, loadEventInfo: MediaSourceEventListener.LoadEventInfo, mediaLoadData: MediaSourceEventListener.MediaLoadData) {
        Log.d(javaClass.name, "onLoadCompleted() called with: eventTime = $eventTime, loadEventInfo = $loadEventInfo, mediaLoadData = $mediaLoadData")
    }

    override fun onLoadCanceled(eventTime: AnalyticsListener.EventTime, loadEventInfo: MediaSourceEventListener.LoadEventInfo, mediaLoadData: MediaSourceEventListener.MediaLoadData) {
        Log.d(javaClass.name, "onLoadCanceled() called with: eventTime = $eventTime, loadEventInfo = $loadEventInfo, mediaLoadData = $mediaLoadData")
    }

    override fun onLoadError(eventTime: AnalyticsListener.EventTime, loadEventInfo: MediaSourceEventListener.LoadEventInfo, mediaLoadData: MediaSourceEventListener.MediaLoadData, error: IOException, wasCanceled: Boolean) {
        Log.d(javaClass.name, "onLoadError() called with: eventTime = $eventTime, loadEventInfo = $loadEventInfo, mediaLoadData = $mediaLoadData, error = $error, wasCanceled = $wasCanceled")
    }

    override fun onReadingStarted(eventTime: AnalyticsListener.EventTime) {
        Log.d(javaClass.name, "onReadingStarted() called with: eventTime = $eventTime")
    }

}