package org.salient.artvideoplayer.activity.api;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import org.salient.artplayer.VideoView;
import org.salient.artplayer.ui.ControlPanel;
import org.salient.artvideoplayer.BaseActivity;
import org.salient.artvideoplayer.R;

import java.io.IOException;

/**
 * Created by Mai on 2018/11/12
 * *
 * Description:
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
                int raw = getResources().getIdentifier(name, "raw", getPackageName());
                AssetFileDescriptor afd = getResources().openRawResourceFd(raw);
                videoView.setDataSourceObject(afd);
                videoView.start();
                break;
            case R.id.playAssets:
                //set Assets Resource File uri
                AssetManager am = getAssets();
                try {
                    String fileName = edAssetsUrl.getText().toString();
                    AssetFileDescriptor assetFileDescriptor = am.openFd(fileName);
                    videoView.setDataSourceObject(assetFileDescriptor);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                videoView.start();
                break;

            //set Raw Resource File uri
//               videoView.setUp(edRawUrl.getText().toString());
//               videoView.start();
        }


    }
}
