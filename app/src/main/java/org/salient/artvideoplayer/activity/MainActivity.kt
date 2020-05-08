package org.salient.artvideoplayer.activity

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_api.*
import kotlinx.android.synthetic.main.content_main.*
import org.salient.artplayer.MediaPlayerManager
import org.salient.artplayer.VideoViewOld
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.exo.ExoPlayer
import org.salient.artplayer.exo.ExoSourceBuilder
import org.salient.artplayer.ijk.IjkPlayer
import org.salient.artplayer.player.IMediaPlayer
import org.salient.artplayer.player.SystemPlayer
import org.salient.artplayer.ui.FullscreenVideoView
import org.salient.artplayer.ui.TinyVideoView
import org.salient.artplayer.ui.VideoView
import org.salient.artvideoplayer.BaseActivity
import org.salient.artvideoplayer.R
import java.io.IOException

class MainActivity : BaseActivity() {
    private var mediaPlayer: IMediaPlayer<*>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        setupMediaPlayer(SystemPlayer())

        btn_start.setOnClickListener {
            //开始播放
            if (artVideoView.playerState == PlayerState.INITIALIZED || artVideoView.playerState == PlayerState.STOPPED) {
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
            fullScreenVideoView.mediaPlayer = mediaPlayer
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
            tinyVideoView.mediaPlayer = mediaPlayer
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
            is SystemPlayer -> {
                mediaPlayer.setDataSource(this, Uri.parse("http://vfx.mtime.cn/Video/2018/07/06/mp4/180706094003288023.mp4"))
            }
            is IjkPlayer -> {
                mediaPlayer.setDataSource(this, Uri.parse("http://vfx.mtime.cn/Video/2018/07/06/mp4/180706094003288023.mp4"))
            }
            is ExoPlayer -> {
                val mediaSource = ExoSourceBuilder(this, "http://vfx.mtime.cn/Video/2018/07/06/mp4/180706094003288023.mp4")
                        .apply {
                            this.isLooping = false
                            this.cacheEnable = true
                            this.preview = true
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
                SystemPlayer().also {
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
                hideSoftInput()
                val fullScreenVideoView = FullscreenVideoView(this)
                val systemMediaPlayer = SystemPlayer()
                systemMediaPlayer.setDataSource(this, Uri.parse("http://vfx.mtime.cn/Video/2018/06/29/mp4/180629124637890547.mp4"))
                fullScreenVideoView.mediaPlayer = systemMediaPlayer
                //开始播放
                fullScreenVideoView.prepare()

                MediaPlayerManager.startFullscreen(this, fullScreenVideoView)

            }
            R.id.tinyWindow -> {
                hideSoftInput()
                val tinyVideoView = TinyVideoView(this)
                val systemMediaPlayer = SystemPlayer()
                systemMediaPlayer.setDataSource(this, Uri.parse("http://vfx.mtime.cn/Video/2018/06/29/mp4/180629124637890547.mp4"))
                tinyVideoView.mediaPlayer = systemMediaPlayer
                tinyVideoView.prepare()

                MediaPlayerManager.startTinyWindow(this, tinyVideoView)
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.isChecked) return super.onOptionsItemSelected(item)
        val id = item.itemId
        when (id) {
            R.id.menu_MediaPlayer -> {
                mMenu?.getItem(0)?.title = "Using: SystemPlayer"
                setupMediaPlayer(SystemPlayer())
            }
            R.id.menu_IjkPlayer -> {
                mMenu?.getItem(0)?.title = "Using: IjkPlayer"
                setupMediaPlayer(IjkPlayer())
            }
            R.id.menu_ExoPlayer -> {
                mMenu?.getItem(0)?.title = "Using: ExoPlayer"
                setupMediaPlayer(ExoPlayer(this))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 刷新标题栏菜单状态
     */
    override fun refreshMenuState() {
        mMenu?.also {
            when (mediaPlayer) {
                is SystemPlayer -> {
                    it.getItem(1).getSubMenu().getItem(0).setChecked(true);
                    it.getItem(0).setTitle("Using: SystemPlayer");
                }
                is IjkPlayer -> {
                    it.getItem(1).getSubMenu().getItem(1).setChecked(true);
                    it.getItem(0).setTitle("Using: IjkPlayer");
                }
                is ExoPlayer -> {
                    it.getItem(1).getSubMenu().getItem(2).setChecked(true);
                    it.getItem(0).setTitle("Using: ExoPlayer");
                }
            }
        }
    }
}