package org.salient.videoplayerdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

import org.salient.MediaPlayerManager;
import org.salient.VideoView;
import org.salient.videoplayerdemo.ControlPanel;
import org.salient.videoplayerdemo.R;
import org.salient.videoplayerdemo.bean.VideoBean;

public class MainActivity extends BaseActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.salientVideoView);
        final ControlPanel controlPanel = new ControlPanel(this);
        videoView.setControlPanel(controlPanel);
        videoView.post(new Runnable() {
            @Override
            public void run() {
                if (mMovieData != null) {
                    VideoBean videoBean = getRandomVideo();
                    videoView.setUp(videoBean.getUrl());
                    Glide.with(MainActivity.this)
                            .load(videoBean.getImage())
                            .into(controlPanel.getCoverView());
                }
            }
        });
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
        MediaPlayerManager.instance().releaseAllVideos();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.smartMode:
                startActivity(new Intent(this, SmartModeActivity.class));
                break;
        }
    }
}
