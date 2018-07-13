package org.salient.videoplayerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.salient.MediaPlayerManager;
import org.salient.VideoView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "http://video.pearvideo.com/mp4/short/20170414/cont-1064146-10369519-ld.mp4";
        VideoView videoView = findViewById(R.id.salientVideoView);
        videoView.setUp(url, VideoView.WindowType.NORMAL);
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
}
