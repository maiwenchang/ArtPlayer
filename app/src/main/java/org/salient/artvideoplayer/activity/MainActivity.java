package org.salient.artvideoplayer.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import org.salient.artplayer.MediaPlayerManagerOld;
import org.salient.artplayer.VideoViewOld;
import org.salient.artplayer.extend.Utils;
import org.salient.artplayer.player.SystemMediaPlayer;
import org.salient.artplayer.ui.VideoView;
import org.salient.artvideoplayer.BaseActivity;
import org.salient.artvideoplayer.R;

import java.io.IOException;

public class MainActivity extends BaseActivity {

    private VideoViewOld videoViewOld;
    private EditText edUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edUrl = findViewById(R.id.edUrl);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        try {
            VideoView videoView = findViewById(R.id.salientVideoView);
            SystemMediaPlayer systemMediaPlayer = new SystemMediaPlayer();
            systemMediaPlayer.getImpl().setDataSource(this, Uri.parse("http://vfx.mtime.cn/Video/2018/07/06/mp4/180706094003288023.mp4"));
            videoView.setMediaPlayer(systemMediaPlayer);

            findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.start();
                    //保持屏幕常量
                    Utils.INSTANCE.scanForActivity(MainActivity.this)
                            .getWindow()
                            .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
//        if (MediaPlayerManagerOld.INSTANCE.backPress()) {
//            return;
//        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftInput();
//        MediaPlayerManagerOld.INSTANCE.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MediaPlayerManagerOld.INSTANCE.releasePlayerAndView(this);
    }

    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.play:
//                String url = edUrl.getText().toString();
//                if (!TextUtils.isEmpty(url)) {
//                    videoView.setUp(url);
//                    videoView.start();
//                    TextView tvTitle = videoView.getControlPanel().findViewById(R.id.tvTitle);
//                    tvTitle.setText(url);
//                    ((ImageView) videoView.getControlPanel().findViewById(R.id.video_cover)).setImageResource(0);
//                }
//                break;
//            case R.id.fullWindow:
//                hideSoftInput();
//                VideoView videoView = new VideoView(this);
//                videoView.setUp("http://vfx.mtime.cn/Video/2018/06/29/mp4/180629124637890547.mp4");
//                videoView.setControlPanel(new ControlPanel(this));
//                videoView.start();
//                videoView.startFullscreen(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//                break;
//        }
    }

}
