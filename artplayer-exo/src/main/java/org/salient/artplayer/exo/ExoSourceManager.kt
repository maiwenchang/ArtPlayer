package org.salient.artplayer.exo

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import java.io.File

/**
 * description: EXO视频播放器的缓存管理类
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
object ExoSourceManager {

    private const val TAG = "ExoSourceManager"
    /**
     * 默认最大缓存 512M
     */
    const val EXO_CACHE_DEFAULT_MAX_SIZE = 512 * 1024 * 1024.toLong()
    const val EXO_MEDIA_TYPE_RTMP = 4

    /**
     * 获取DataSourceFactory
     */
    fun getCacheDataSourceFactory(context: Context, headers: Map<String, String>?, transferListener: TransferListener?, cache: Cache): DataSource.Factory {
        return CacheDataSourceFactory(cache, getDefaultDataSourceFactory(context, headers, transferListener), CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    /**
     * 获取DefaultDataSourceFactory
     */
    fun getDefaultDataSourceFactory(context: Context, headers: Map<String, String>?, transferListener: TransferListener?): DataSource.Factory {
        return DefaultDataSourceFactory(context, transferListener, getHttpDataSourceFactory(context, headers, transferListener))
    }

    fun getHttpDataSourceFactory(context: Context, headers: Map<String, String>?, transferListener: TransferListener?): DataSource.Factory {
        val dataSourceFactory = DefaultHttpDataSourceFactory(
                Util.getUserAgent(context, TAG),
                transferListener)
        headers?.forEach { key, value ->
            dataSourceFactory.defaultRequestProperties[key] = value
        }
        return dataSourceFactory
    }


    //@C.ContentType
    fun inferContentType(fileName: String): Int {
        Util.toLowerInvariant(fileName).let {
            return if (it.endsWith(".mpd")) {
                C.TYPE_DASH
            } else if (it.endsWith(".m3u8")) {
                C.TYPE_HLS
            } else if (it.endsWith(".ism") || it.endsWith(".isml")
                    || it.endsWith(".ism/manifest") || it.endsWith(".isml/manifest")) {
                C.TYPE_SS
            } else if (it.startsWith("rtmp:")) {
                EXO_MEDIA_TYPE_RTMP
            } else {
                C.TYPE_OTHER
            }
        }

    }

    /**
     * 获取本地缓存
     */
    @Synchronized
    fun getCacheInstance(context: Context, cacheDir: File?, maxCacheSize: Long = EXO_CACHE_DEFAULT_MAX_SIZE): Cache? {
        val dirs = if (cacheDir != null) {
            cacheDir.absolutePath
        } else {
            context.cacheDir.absolutePath
        }
        val path = dirs + File.separator + "exo"
        val isLocked = SimpleCache.isCacheFolderLocked(File(path))
        if (!isLocked) {
            return SimpleCache(File(path), LeastRecentlyUsedCacheEvictor(maxCacheSize), ExoDatabaseProvider(context))
        } else {
            return null
        }
    }

    /**
     * 释放缓存
     */
    fun release(mCache: Cache? = null) {
        try {
            mCache?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 清理缓存
     * Cache需要release之后才能clear
     */
    fun clearCache(context: Context, cacheDir: File?, url: String?) {
        try {
            val cache = getCacheInstance(context, cacheDir)
            if (!TextUtils.isEmpty(url)) {
                if (cache != null) {
                    CacheUtil.remove(cache, CacheUtil.generateKey(Uri.parse(url)))
                }
            } else {
                if (cache != null) {
                    for (key in cache.keys) {
                        CacheUtil.remove(cache, key)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 是否缓存成功
     */
    fun isCached(context: Context, cacheDir: File?, url: String?): Boolean {
        return resolveCacheState(getCacheInstance(context, cacheDir), url)
    }

    /**
     * 根据缓存块判断是否缓存成功
     *
     * @param cache
     */
    private fun resolveCacheState(cache: Cache?, url: String?): Boolean {
        var isCache = false
        if (url?.isNotEmpty() == true) {
            val key = CacheUtil.generateKey(Uri.parse(url))
            if (key.isNotEmpty()) {
                val cachedSpans = cache!!.getCachedSpans(key)
                if (cachedSpans.size == 0) {
                    isCache = false
                } else {
                    val contentLength = ContentMetadata.getContentLength(cache.getContentMetadata(key))
                    var currentLength: Long = 0
                    for (cachedSpan in cachedSpans) {
                        currentLength += cache.getCachedLength(key, cachedSpan.position, cachedSpan.length)
                    }
                    isCache = currentLength >= contentLength
                }
            } else {
                isCache = false
            }
        }
        return isCache
    }
}