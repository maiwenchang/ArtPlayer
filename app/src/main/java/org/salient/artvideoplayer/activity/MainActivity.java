package org.salient.artvideoplayer.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.VideoView;
import org.salient.artvideoplayer.BaseActivity;
import org.salient.artvideoplayer.R;
import org.salient.artvideoplayer.activity.listview.ListViewActivity;
import org.salient.artvideoplayer.activity.recyclerview.RecyclerViewActivity;
import org.salient.controlpanel.ControlPanel;

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
                startActivity(new Intent(this, RecyclerViewActivity.class));
                break;
            case R.id.fullWindow:
                VideoView videoView = new VideoView(this);
                videoView.setUp("http://vfx.mtime.cn/Video/2018/06/29/mp4/180629124637890547.mp4");
                videoView.setControlPanel(new ControlPanel(this));
                videoView.start();
                videoView.startFullscreen(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                break;
            case R.id.tinyWindow:
                VideoView tinyVideoView = new VideoView(this);
                tinyVideoView.setUp("http://vfx.mtime.cn/Video/2018/06/06/mp4/180606101738263858.mp4", VideoView.WindowType.TINY);
                ControlPanel controlPanel = new ControlPanel(this);
                tinyVideoView.setControlPanel(controlPanel);
//                ImageView coverView = controlPanel.getCoverView();
//                Glide.with(controlPanel.getContext()).load("http://img5.mtime.cn/mg/2018/06/06/101658.92608147.jpg").into(coverView);
                tinyVideoView.start();
                tinyVideoView.startTinyWindow();
                break;
            case R.id.smartModeListView:
                startActivity(new Intent(this, ListViewActivity.class));
                break;
        }
    }
}
