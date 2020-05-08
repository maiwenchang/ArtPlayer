package org.salient.artplayer.ijk

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import tv.danmaku.ijk.media.player.misc.IMediaDataSource
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * description: 构建Raw播放DataSource帮助类
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-04 10:06 AM.
 */
class RawDataSourceProvider(private var mDescriptor: AssetFileDescriptor) : IMediaDataSource {
    private var mMediaBytes: ByteArray? = null
    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        val bytes = mMediaBytes ?: return 0
        if (position >= bytes.size) {
            return -1
        }
        var length: Int
        if (position + size < bytes.size) {
            length = size
        } else {
            length = (bytes.size - position).toInt()
            if (length > buffer.size) length = buffer.size
        }
        System.arraycopy(bytes, position.toInt(), buffer, offset, length)
        return length
    }

    @Throws(IOException::class)
    override fun getSize(): Long {
        val length = mDescriptor.length
        if (mMediaBytes == null) {
            val inputStream: InputStream = mDescriptor.createInputStream()
            mMediaBytes = readBytes(inputStream)
        }
        return length
    }

    @Throws(IOException::class)
    override fun close() {
        mDescriptor.close()
        mMediaBytes = null
    }

    @Throws(IOException::class)
    private fun readBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    companion object {
        fun create(context: Context, uri: Uri): RawDataSourceProvider? {
            try {
                val fileDescriptor = context.contentResolver.openAssetFileDescriptor(uri, "r")
                        ?: return null
                return RawDataSourceProvider(fileDescriptor)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return null
        }
    }

}