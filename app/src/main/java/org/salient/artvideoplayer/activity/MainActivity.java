package org.salient.artvideoplayer.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.salient.artvideoplayer.bean.VideoBean;
import org.salient.controlpanel.ControlPanel;
import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.VideoView;
import org.salient.artvideoplayer.R;

public class MainActivity extends BaseActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.salientVideoView);

        final ControlPanel controlPanel = new ControlPanel(this);
        videoView.setControlPanel(controlPanel);

        videoView.setUp("http://vfx.mtime.cn/Video/2018/06/27/mp4/180627094726195356.mp4");
        //videoView.start();

        Glide.with(MainActivity.this)
                .load("http://img5.mtime.cn/mg/2018/06/27/094527.12278962.jpg")
                .into(controlPanel.getCoverView());
    }

    @Override
    public void onBackPressed() {
        if (MediaPlayerManager.instance().backPress(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerManager.instance().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerManager.instance().releasePlayerAndView(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.smartModeRecyclerView:
                startActivity(new Intent(this, SmartModeActivity.class));
                break;
            case R.id.fullWindow:
                VideoView videoView = new VideoView(this);
                videoView.setUp("http://vfx.mtime.cn/Video/2018/06/29/mp4/180629124637890547.mp4");
                videoView.setControlPanel(new ControlPanel(this));
                videoView.start();
                MediaPlayerManager.instance().startFullscreen(videoView, ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                break;
            case R.id.tinyWindow:
                VideoView tinyVideoView = new VideoView(this);
                tinyVideoView.setUp("http://vfx.mtime.cn/Video/2018/06/06/mp4/180606101738263858.mp4", VideoView.WindowType.TINY);
                ControlPanel controlPanel = new ControlPanel(this);
                tinyVideoView.setControlPanel(controlPanel);
                ImageView coverView = controlPanel.getCoverView();
                Glide.with(controlPanel.getContext()).load("http://img5.mtime.cn/mg/2018/06/06/101658.92608147.jpg").into(coverView);
                tinyVideoView.start();
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(16 * 50, 9 * 50);
                layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                layoutParams.setMargins(0, 0, 30, 100);
                MediaPlayerManager.instance().startTinyWindow(tinyVideoView, layoutParams);
                break;
        }
    }
}
