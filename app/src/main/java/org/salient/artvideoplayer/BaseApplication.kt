package org.salient.artvideoplayer

import android.app.Application
import android.text.TextUtils
import com.google.gson.Gson
import org.salient.artvideoplayer.bean.MovieData
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.Executors

/**
 * Created by Mai on 2018/7/17
 * *
 * Description:
 * *
 */
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Executors.newSingleThreadExecutor().submit { jsonString = readAssetsFile("video.json") }
    }

    /**
     * 读取assets中的文件
     *
     * @param path File Path
     * @return File Content String
     */
    fun readAssetsFile(path: String?): String {
        var result = ""
        try { // read file content from file
            val sb = StringBuilder("")
            val reader = InputStreamReader(resources.assets.open(path!!))
            val br = BufferedReader(reader)
            var str: String?
            while (br.readLine().also { str = it } != null) {
                sb.append(str)
            }
            result = sb.toString()
            br.close()
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    companion object {
        private var jsonString = ""
        val movieData: MovieData?
            get() = if (TextUtils.isEmpty(jsonString)) {
                null
            } else {
                Gson().fromJson(jsonString, MovieData::class.java)
            }
    }
}