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
import org.salient.artvideoplayer.bean.VideoBean
import java.util.*

/**
 * Created by Mai on 2018/7/17
 * *
 * Description:
 * *
 */
abstract class BaseActivity : AppCompatActivity() {

    val allComing: List<VideoBean>
        get() {
            val list: MutableList<VideoBean> = ArrayList()
            val mMovieData = BaseApplication.getMovieData()
            if (mMovieData != null) {
                for (moviecomingsBean in mMovieData.moviecomings) {
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
            val mMovieData = BaseApplication.getMovieData()
            if (mMovieData != null) {
                for (attentionBean in mMovieData.attention) {
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
            val mMovieData = BaseApplication.getMovieData()
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

    protected var mMenu: Menu? = null
    /**
     * 刷新标题栏菜单状态
     */
    abstract fun refreshMenuState()

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_bar_setting, menu)
        mMenu = menu
        refreshMenuState()
        return true
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (mMenu == null) return super.onMenuOpened(featureId, menu)
        refreshMenuState()
        return super.onMenuOpened(featureId, mMenu!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        if (item.isChecked) return super.onOptionsItemSelected(item)
        val id = item.itemId
        return super.onOptionsItemSelected(item)
    }
}