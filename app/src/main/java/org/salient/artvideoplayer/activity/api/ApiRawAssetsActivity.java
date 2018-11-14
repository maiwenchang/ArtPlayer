package org.salient.artvideoplayer.activity.api;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.VideoView;
import org.salient.artplayer.exo.ExoPlayer;
import org.salient.artplayer.ui.ControlPanel;
import org.salient.artvideoplayer.BaseActivity;
import org.salient.artvideoplayer.R;

import java.io.IOException;

/**
 * Created by Mai on 2018/11/12
 * *
 * Description: 播放 Raw / Assets 音视频文件
 * 详细介绍参考: https://www.jianshu.com/p/37fef37706d3
 *
 * *
 */
public class ApiRawAssetsActivity extends BaseActivity {

    private VideoView videoView;
    private EditText edAssetsUrl;
    private EditText edRawUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_raw_assets);
        edAssetsUrl = findViewById(R.id.edAssetsUrl);
        edRawUrl = findViewById(R.id.edRawUrl);

        // note : usage sample
        videoView = findViewById(R.id.salientVideoView);
        //optional: set ControlPanel
        final ControlPanel controlPanel = new ControlPanel(this);
        videoView.setControlPanel(controlPanel);

        edRawUrl.setText(getResources().getResourceName(R.raw.raw_video));

       edAssetsUrl.setText("assets_video.mp4");

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playRaw:
                //set Raw Resource File uri
                String name = edRawUrl.getText().toString();
                int raw = getResources().getIdentifier(name, "raw", getPackageName()); // R.raw.raw_video

                if (MediaPlayerManager.instance().getMediaPlayer() instanceof ExoPlayer) {
                    // ExoPlayer
                    DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(raw));
                    RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(this);
                    try {
                        rawResourceDataSource.open(dataSpec);
                    } catch (RawResourceDataSource.RawResourceDataSourceException e) {
                        e.printStackTrace();
                    }
                    videoView.setDataSourceObject(rawResourceDataSource);
                } else {
                    //android.media.MediaPlayer & IjkPlayer
                    AssetFileDescriptor afd = getResources().openRawResourceFd(raw);
                    videoView.setDataSourceObject(afd);
                }

                videoView.start();
                break;
            case R.id.playAssets:
                //set Assets Resource File uri
                String fileName = edAssetsUrl.getText().toString(); // "assets_video.mp4"
                if (MediaPlayerManager.instance().getMediaPlayer() instanceof ExoPlayer) {
                    // ExoPlayer
                    videoView.setDataSourceObject("file:///android_asset/" + fileName);
                } else {
                    //android.media.MediaPlayer & IjkPlayer
                    AssetManager am = getAssets();
                    try {
                        AssetFileDescriptor afd2 = am.openFd(fileName);
                        videoView.setDataSourceObject(afd2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                videoView.start();
                break;
        }


    }
}
