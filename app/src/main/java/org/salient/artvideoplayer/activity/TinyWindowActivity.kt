package org.salient.artvideoplayer.activity

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_tiny.*
import org.salient.artplayer.MediaPlayerManager
import org.salient.artplayer.player.SystemMediaPlayer
import org.salient.artplayer.ui.TinyVideoView
import org.salient.artvideoplayer.BaseActivity
import org.salient.artvideoplayer.DensityUtil.getWindowHeight
import org.salient.artvideoplayer.DensityUtil.getWindowWidth
import org.salient.artvideoplayer.R

/**
 * description: 小窗播放demo
 *
 * @author Maiwenchang
 * email: cv.stronger@gmail.com
 * date: 2020-05-20 09:06 AM.
 */
class TinyWindowActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tiny)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideSoftInput()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.start -> {
                hideSoftInput()
                //set LayoutParams
                val windowWidth = getWindowWidth(this)
                val windowHeight = getWindowHeight(this)
                var width = Integer.valueOf(width.text.toString())
                var height = Integer.valueOf(height.text.toString())
                if (width > windowWidth) {
                    width = windowWidth
                }
                if (height > windowHeight) {
                    height = windowHeight
                }
                val layoutParams = FrameLayout.LayoutParams(width, height)
                var leftRight = Gravity.END
                if (left.isChecked) {
                    leftRight = Gravity.START
                }
                var topBottom = Gravity.BOTTOM
                if (top.isChecked) {
                    topBottom = Gravity.TOP
                }
                layoutParams.gravity = leftRight or topBottom
                var marginLeft = Integer.valueOf(marginLeft.text.toString())
                var marginTop = Integer.valueOf(marginTop.text.toString())
                var marginRight = Integer.valueOf(marginRight.text.toString())
                var marginBottom = Integer.valueOf(marginBottom.text.toString())
                if (marginLeft > windowWidth - width) {
                    marginLeft = windowWidth - width
                }
                if (marginRight > windowWidth - width) {
                    marginRight = windowWidth - width
                }
                if (marginTop > windowHeight - height) {
                    marginTop = windowHeight - height
                }
                if (marginBottom > windowHeight - height) {
                    marginBottom = windowHeight - height
                }
                layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom)
                val tinyVideoView = TinyVideoView(context = this, params = layoutParams).apply {
                    mediaPlayer = SystemMediaPlayer().apply {
                        val uri = Uri.parse(randomVideo?.url)
                        setDataSource(this@TinyWindowActivity, uri)
                    }
                    isMovable = cb_isMovable.isChecked
                    isScalable = cb_isScalable.isChecked
                }
                tinyVideoView.prepare()
                MediaPlayerManager.startTinyWindow(this@TinyWindowActivity, tinyVideoView)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}