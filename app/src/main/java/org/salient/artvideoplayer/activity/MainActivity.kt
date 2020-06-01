package org.salient.artvideoplayer.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.content_main.*
import org.salient.artplayer.MediaPlayerManager
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.exo.ExoMediaPlayer
import org.salient.artplayer.exo.ExoSourceBuilder
import org.salient.artplayer.ijk.IjkPlayer
import org.salient.artplayer.player.IMediaPlayer
import org.salient.artplayer.player.SystemMediaPlayer
import org.salient.artplayer.ui.FullscreenVideoView
import org.salient.artplayer.ui.TinyVideoView
import org.salient.artvideoplayer.BaseActivity
import org.salient.artvideoplayer.R
import java.io.IOException

/**
 * description: 首页
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-20 09:06 AM.
 */
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        setupMediaPlayer(SystemMediaPlayer())

        btn_start.setOnClickListener {
            //开始播放
            if (artVideoView.playerState == PlayerState.INITIALIZED) {
                artVideoView.prepare()
            } else if (artVideoView.playerState == PlayerState.STOPPED) {
                artVideoView.prepare()
            } else if (!artVideoView.isPlaying) {
                artVideoView.start()
            }
        }

        btn_pause.setOnClickListener {
            artVideoView.pause()
        }

        btn_stop.setOnClickListener {
            artVideoView.stop()
        }

        btn_fullscreen.setOnClickListener {
            //开启全屏
            val fullScreenVideoView = FullscreenVideoView(this, origin = artVideoView)
            fullScreenVideoView.mediaPlayer = artVideoView.mediaPlayer
            MediaPlayerManager.startFullscreen(this, fullScreenVideoView)

            fullScreenVideoView.setOnClickListener {
                if (!fullScreenVideoView.isPlaying) {
                    fullScreenVideoView.start()
                }
            }
        }

        btn_tiny.setOnClickListener {
            //开启小窗
            val tinyVideoView = TinyVideoView(this, origin = artVideoView)
            tinyVideoView.mediaPlayer = artVideoView.mediaPlayer
            MediaPlayerManager.startTinyWindow(this, tinyVideoView)

            tinyVideoView.setOnClickListener {
                if (!tinyVideoView.isPlaying) {
                    tinyVideoView.start()
                }
            }
        }

    }

    private fun setupMediaPlayer(mediaPlayer: IMediaPlayer<*>) {
        artVideoView.mediaPlayer?.release()
        artVideoView.mediaPlayer = mediaPlayer
        when (mediaPlayer) {
            is SystemMediaPlayer -> {
                mediaPlayer.setDataSource(this, Uri.parse("http://vfx.mtime.cn/Video/2018/07/06/mp4/180706094003288023.mp4"))
            }
            is IjkPlayer -> {
                mediaPlayer.setDataSource(this, Uri.parse("http://vfx.mtime.cn/Video/2018/07/06/mp4/180706094003288023.mp4"))
            }
            is ExoMediaPlayer -> {
                val mediaSource = ExoSourceBuilder(this, "http://vfx.mtime.cn/Video/2018/07/06/mp4/180706094003288023.mp4")
                        .apply {
                            this.isLooping = false
                            this.cacheEnable = true
                        }
                        .build()
                mediaPlayer.mediaSource = mediaSource
            }
        }


    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.play -> {
                val url = edUrl!!.text.toString()
                SystemMediaPlayer().also {
                    try {
                        it.impl.setDataSource(this, Uri.parse(url))
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }.let {
                    artVideoView.mediaPlayer = it
                }
                artVideoView.prepare()
            }
            R.id.fullWindow -> {
//                hideSoftInput()
//
                startActivity(Intent(this, FullscreenActivity::class.java))

            }
            R.id.tinyWindow -> {
                startActivity(Intent(this, TinyWindowActivity::class.java))
            }
        }
    }

    protected var mMenu: Menu? = null

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.isChecked) return super.onOptionsItemSelected(item)
        val id = item.itemId
        when (id) {
            R.id.menu_MediaPlayer -> {
                mMenu?.getItem(0)?.title = "Using: MediaPlayer"
                setupMediaPlayer(SystemMediaPlayer())
            }
            R.id.menu_IjkPlayer -> {
                mMenu?.getItem(0)?.title = "Using: IjkPlayer"
                setupMediaPlayer(IjkPlayer())
            }
            R.id.menu_ExoPlayer -> {
                mMenu?.getItem(0)?.title = "Using: ExoPlayer"
                setupMediaPlayer(ExoMediaPlayer(this))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 刷新标题栏菜单状态
     */
    private fun refreshMenuState() {
        mMenu?.also {
            when (artVideoView.mediaPlayer) {
                is SystemMediaPlayer -> {
                    it.getItem(1).getSubMenu().getItem(0).setChecked(true);
                    it.getItem(0).setTitle("Using: MediaPlayer");
                }
                is IjkPlayer -> {
                    it.getItem(1).getSubMenu().getItem(1).setChecked(true);
                    it.getItem(0).setTitle("Using: IjkPlayer");
                }
                is ExoMediaPlayer -> {
                    it.getItem(1).getSubMenu().getItem(2).setChecked(true);
                    it.getItem(0).setTitle("Using: ExoPlayer");
                }
            }
        }
    }
}