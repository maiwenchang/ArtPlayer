package org.salient.artplayer.exo

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.TransferListener
import java.io.File

/**
 * description: 构建ExoPlayer的MediaSource
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-08 3:05 PM.
 */
class ExoSourceBuilder(private val context: Context, private val dataSource: String) {

    /**
     * 缓存地址
     */
    var cacheDir: File? = null

    /**
     * header
     */
    var headers: Map<String, String>? = null

    /**
     * 是否缓存
     */
    var cacheEnable: Boolean = true

    /**
     * 是否循环播放
     */
    var isLooping: Boolean = false

    /**
     * 最大缓存大小
     */
    var maxCacheSize: Long = ExoSourceManager.EXO_CACHE_DEFAULT_MAX_SIZE

    /**
     * A listener of data transfer events.
     */
    var transferListener: TransferListener? = DefaultBandwidthMeter.Builder(context).build()

    fun build(): MediaSource {
        val contentUri = Uri.parse(dataSource)
        val contentType = ExoSourceManager.inferContentType(dataSource)
        val mediaSource: MediaSource
        val cache = ExoSourceManager.getCacheInstance(context, cacheDir, maxCacheSize)
        val dataSourceFactoryCache = cache?.let {
            ExoSourceManager.getCacheDataSourceFactory(context, headers, transferListener, cache)
        } ?: ExoSourceManager.getDefaultDataSourceFactory(context, headers, transferListener)
        mediaSource = when (contentType) {
            C.TYPE_SS -> {
                val httpDataSourceFactory = ExoSourceManager.getHttpDataSourceFactory(context, headers, transferListener)
                val defaultDataSourceFactory = DefaultDataSourceFactory(context, transferListener, httpDataSourceFactory)
                SsMediaSource.Factory(
                        DefaultSsChunkSource.Factory(dataSourceFactoryCache),
                        defaultDataSourceFactory).createMediaSource(contentUri)
            }
            C.TYPE_DASH -> {
                val httpDataSourceFactory = ExoSourceManager.getHttpDataSourceFactory(context, headers, transferListener)
                val defaultDataSourceFactory = DefaultDataSourceFactory(context, transferListener, httpDataSourceFactory)
                DashMediaSource.Factory(DefaultDashChunkSource.Factory(dataSourceFactoryCache),
                        defaultDataSourceFactory).createMediaSource(contentUri)
            }
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactoryCache).createMediaSource(contentUri)
            ExoSourceManager.EXO_MEDIA_TYPE_RTMP -> {
                ProgressiveMediaSource.Factory(RtmpDataSourceFactory(transferListener), DefaultExtractorsFactory())
                        .createMediaSource(contentUri)
            }
            C.TYPE_OTHER -> {
                ProgressiveMediaSource.Factory(dataSourceFactoryCache, DefaultExtractorsFactory())
                        .createMediaSource(contentUri)
            }
            else -> {
                ProgressiveMediaSource.Factory(dataSourceFactoryCache, DefaultExtractorsFactory())
                        .createMediaSource(contentUri)
            }
        }
        return if (isLooping) {
            LoopingMediaSource(mediaSource)
        } else mediaSource
    }


}