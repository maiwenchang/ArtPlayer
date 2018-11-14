package org.salient.artvideoplayer.activity.api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.ScaleType;
import org.salient.artplayer.VideoView;
import org.salient.artvideoplayer.BaseActivity;
import org.salient.artvideoplayer.R;
import org.salient.artplayer.ui.ControlPanel;

/**
 * Created by Mai on 2018/8/8
 * *
 * Description:
 * *
 */
public class ApiCommonActivity extends BaseActivity {

    private VideoView videoView;
    private EditText edUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_common);

        edUrl = findViewById(R.id.edUrl);

        // note : usage sample
        videoView = findViewById(R.id.salientVideoView);
        //optional: set ControlPanel
        final ControlPanel controlPanel = new ControlPanel(this);
        videoView.setControlPanel(controlPanel);
        //required: set url
        videoView.setUp("http://vfx.mtime.cn/Video/2018/06/01/mp4/180601113115887894.mp4");
        videoView.start();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scale_default:
                MediaPlayerManager.instance().setScreenScale(ScaleType.DEFAULT);
                break;
            case R.id.scale_16_9:
                MediaPlayerManager.instance().setScreenScale(ScaleType.SCALE_16_9);
                break;
            case R.id.scale_4_3:
                MediaPlayerManager.instance().setScreenScale(ScaleType.SCALE_4_3);
                break;
            case R.id.scale_center_crop:
                MediaPlayerManager.instance().setScreenScale(ScaleType.SCALE_CENTER_CROP);
                break;
            case R.id.scale_match_parent:
                MediaPlayerManager.instance().setScreenScale(ScaleType.SCALE_MATCH_PARENT);
                break;
            case R.id.scale_original:
                MediaPlayerManager.instance().setScreenScale(ScaleType.SCALE_ORIGINAL);
                break;
            case R.id.play:
                String url = edUrl.getText().toString();
                if (!TextUtils.isEmpty(url)) {
                    videoView.setUp(url);
                    videoView.start();
                    TextView tvTitle = videoView.getControlPanel().findViewById(R.id.tvTitle);
                    tvTitle.setText(url);
                }
                break;
        }
    }

    /**
     * 实现重力感应则在对应生命周期下，增加以下实现方法
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
