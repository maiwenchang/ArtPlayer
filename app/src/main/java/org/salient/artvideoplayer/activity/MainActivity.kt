package org.salient.artvideoplayer.activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.content_main.*
import org.salient.artplayer.MediaPlayerManager
import org.salient.artplayer.VideoViewOld
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.player.SystemPlayer
import org.salient.artplayer.ui.FullscreenVideoView
import org.salient.artplayer.ui.TinyVideoView
import org.salient.artplayer.ui.VideoView
import org.salient.artvideoplayer.BaseActivity
import org.salient.artvideoplayer.R
import java.io.IOException

class MainActivity : BaseActivity() {
    private val videoViewOld: VideoViewOld? = null
    private var edUrl: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edUrl = findViewById(R.id.edUrl)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val videoView = findViewById<VideoView>(R.id.salientVideoView)
        val systemMediaPlayer = SystemPlayer()
        try {
            systemMediaPlayer.setDataSource(this, Uri.parse("http://vfx.mtime.cn/Video/2018/07/06/mp4/180706094003288023.mp4"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        videoView.mediaPlayer = systemMediaPlayer
        btn_start.setOnClickListener {
            //开始播放
            val state = systemMediaPlayer.playerStateLD.value
            if (state == PlayerState.INITIALIZED) {
                videoView.prepare()
            } else if (!videoView.isPlaying) {
                videoView.start()
            }
        }

        btn_pause.setOnClickListener {
            videoView.pause()
        }

        btn_stop.setOnClickListener {
            videoView.stop()
        }

        btn_fullscreen.setOnClickListener {
            //开启全屏
            val fullScreenVideoView = FullscreenVideoView(this, origin = videoView)
            fullScreenVideoView.mediaPlayer = systemMediaPlayer
            MediaPlayerManager.startFullscreen(this, fullScreenVideoView)

            fullScreenVideoView.setOnClickListener {
                if (!fullScreenVideoView.isPlaying) {
                    fullScreenVideoView.start()
                }
            }
        }

        btn_tiny.setOnClickListener {
            //开启小窗
            val tinyVideoView = TinyVideoView(this,origin = videoView)
            tinyVideoView.mediaPlayer = systemMediaPlayer
            MediaPlayerManager.startTinyWindow(this, tinyVideoView)

            tinyVideoView.setOnClickListener {
                if (!tinyVideoView.isPlaying) {
                    tinyVideoView.start()
                }
            }
        }

        //设置重力监听
//        MediaPlayerManager.INSTANCE.setOnOrientationChangeListener(new OrientationChangeListener());
//
//        // note : usage sample
//        videoView = findViewById(R.id.salientVideoView);
//        //optional: set ControlPanel
//        final ControlPanel controlPanel = new ControlPanel(this);
//        videoView.setControlPanel(controlPanel);
//        //optional: set title
//        TextView tvTitle = controlPanel.findViewById(R.id.tvTitle);
//        tvTitle.setText("西虹市首富 百变首富预告");
//        //required: set url
//        videoView.setUp("http://vfx.mtime.cn/Video/2018/07/06/mp4/180706094003288023.mp4");
//        //videoView.start();
//        //optional: set cover
//        Glide.with(MainActivity.this)
//                .load("http://img5.mtime.cn/mg/2018/07/06/093947.51483272.jpg")
//                .into((ImageView) controlPanel.findViewById(R.id.video_cover));
    }

    override fun onBackPressed() {
        if (MediaPlayerManager.blockBackPress(this)) {
            return
        }
        super.onBackPressed()
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
                    salientVideoView.mediaPlayer = it
                }
                salientVideoView.prepare()
            }
            R.id.fullWindow -> {
                hideSoftInput()
                val fullScreenVideoView = FullscreenVideoView(this)
                val systemMediaPlayer = SystemPlayer()
                try {
                    systemMediaPlayer.setDataSource(this, Uri.parse("http://vfx.mtime.cn/Video/2018/06/29/mp4/180629124637890547.mp4"))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                fullScreenVideoView.mediaPlayer = systemMediaPlayer
                //开始播放
                fullScreenVideoView.prepare()

                MediaPlayerManager.startFullscreen(this, fullScreenVideoView)

            }
            R.id.tinyWindow -> {
                hideSoftInput()
                val tinyVideoView = TinyVideoView(this)
                val systemMediaPlayer = SystemPlayer()
                try {
                    systemMediaPlayer.setDataSource(this, Uri.parse("http://vfx.mtime.cn/Video/2018/06/29/mp4/180629124637890547.mp4"))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                tinyVideoView.mediaPlayer = systemMediaPlayer
                tinyVideoView.prepare()

                MediaPlayerManager.startTinyWindow(this, tinyVideoView)
            }
        }
    }
}