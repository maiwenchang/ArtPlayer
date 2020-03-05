package org.salient.artvideoplayer.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.VideoView;
import org.salient.artplayer.ui.ControlPanel;
import org.salient.artplayer.ui.listener.OrientationChangeListener;
import org.salient.artvideoplayer.BaseActivity;
import org.salient.artvideoplayer.R;
import org.salient.artvideoplayer.activity.api.ApiActivity;
import org.salient.artvideoplayer.activity.extension.ExtensionActivity;
import org.salient.artvideoplayer.activity.list.ListActivity;
import org.salient.artvideoplayer.activity.tiny.TinyWindowActivity;
import org.salient.artvideoplayer.activity.orientation.OrientationActivity;

public class MainActivity extends BaseActivity {

    private VideoView videoView;
    private EditText edUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edUrl = findViewById(R.id.edUrl);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //设置重力监听
        MediaPlayerManager.instance().setOnOrientationChangeListener(new OrientationChangeListener());

        // note : usage sample
        videoView = findViewById(R.id.salientVideoView);
        //optional: set ControlPanel
        final ControlPanel controlPanel = new ControlPanel(this);
        videoView.setControlPanel(controlPanel);
        //optional: set title
        TextView tvTitle = controlPanel.findViewById(R.id.tvTitle);
        tvTitle.setText("西虹市首富 百变首富预告");
        //required: set url
        videoView.setUp("http://vfx.mtime.cn/Video/2018/07/06/mp4/180706094003288023.mp4");
        //videoView.start();
        //optional: set cover
        Glide.with(MainActivity.this)
                .load("http://img5.mtime.cn/mg/2018/07/06/093947.51483272.jpg")
                .into((ImageView) controlPanel.findViewById(R.id.video_cover));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (MediaPlayerManager.instance().backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftInput();
        MediaPlayerManager.instance().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerManager.instance().releasePlayerAndView(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                String url = edUrl.getText().toString();
                if (!TextUtils.isEmpty(url)) {
                    videoView.setUp(url);
                    videoView.start();
                    TextView tvTitle = videoView.getControlPanel().findViewById(R.id.tvTitle);
                    tvTitle.setText(url);
                    ((ImageView) videoView.getControlPanel().findViewById(R.id.video_cover)).setImageResource(0);
                }
                break;
            case R.id.api:
                startActivity(new Intent(this, ApiActivity.class));
                break;
            case R.id.list:
                startActivity(new Intent(this, ListActivity.class));
                break;
            case R.id.fullWindow:
                hideSoftInput();
                VideoView videoView = new VideoView(this);
                videoView.setUp("http://vfx.mtime.cn/Video/2018/06/29/mp4/180629124637890547.mp4");
                videoView.setControlPanel(new ControlPanel(this));
                videoView.start();
                videoView.startFullscreen(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                break;
            case R.id.tinyWindow:
                startActivity(new Intent(this, TinyWindowActivity.class));
                break;
            case R.id.extension:
                startActivity(new Intent(this, ExtensionActivity.class));
                break;
            case R.id.orientation:
                startActivity(new Intent(this, OrientationActivity.class));
                break;
        }
    }

}
