package org.salient.artvideoplayer

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import org.salient.artplayer.MediaPlayerManager.blockBackPress
import org.salient.artplayer.exo.ExoMediaPlayer
import org.salient.artplayer.ijk.IjkPlayer
import org.salient.artplayer.player.SystemMediaPlayer
import org.salient.artvideoplayer.bean.VideoBean
import java.util.*

/**
 * description: Activity基类
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-20 09:06 AM.
 */
abstract class BaseActivity : AppCompatActivity() {

    val allComing: List<VideoBean>
        get() {
            val list: MutableList<VideoBean> = ArrayList()
            val mMovieData = BaseApplication.movieData
            if (mMovieData != null) {
                val moviecomings = mMovieData.moviecomings ?: emptyList()
                for (moviecomingsBean in moviecomings) {
                    val videos = moviecomingsBean.videos
                    if (videos != null && videos.size > 0) {
                        list.add(videos[0])
                    }
                }
            }
            return list
        }

    val allAttention: List<VideoBean>
        get() {
            val list: MutableList<VideoBean> = ArrayList()
            val mMovieData = BaseApplication.movieData
            if (mMovieData != null) {
                val attentions = mMovieData.attention ?: emptyList()
                for (attentionBean in attentions) {
                    val videos = attentionBean.videos
                    if (videos != null && videos.size > 0) {
                        list.add(videos[0])
                    }
                }
            }
            return list
        }

    val randomVideo: VideoBean?
        get() {
            val mMovieData = BaseApplication.movieData
            if (mMovieData != null) {
                val allAttention = allAttention
                return allAttention[getRandomInt(0, allAttention.size)]
            }
            return null
        }

    fun getRandomInt(min: Int, max: Int): Int {
        var i = (System.currentTimeMillis() % max).toInt()
        if (i < min) {
            i = i + min
        }
        Log.d("BaseActivity", "#getRandomInt():$i")
        return i
    }

    override fun onBackPressed() {
        if (blockBackPress(this)) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        hideSoftInput()
    }

    //显示软键盘
    fun showSoftInput(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        if (imm != null) {
            view.requestFocus()
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    //收起软键盘
    fun hideSoftInput() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideSoftInput()
    }

}