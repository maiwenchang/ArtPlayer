package org.salient.artvideoplayer.activity.tiny;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.VideoView;
import org.salient.artplayer.ui.ControlPanel;
import org.salient.artvideoplayer.BaseActivity;
import org.salient.artvideoplayer.DensityUtil;
import org.salient.artvideoplayer.R;

/**
 * Created by Mai on 2018/8/21
 * *
 * Description:
 * *
 */
public class TinyWindowActivity extends BaseActivity {

    private EditText width;
    private EditText height;

    private RadioButton left;
    private RadioButton top;

    private EditText marginLeft;
    private EditText marginRight;
    private EditText marginTop;
    private EditText marginBottom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiny);
        width = findViewById(R.id.width);
        height = findViewById(R.id.height);
        left = findViewById(R.id.left);
        top = findViewById(R.id.top);
        marginLeft = findViewById(R.id.marginLeft);
        marginRight = findViewById(R.id.marginRight);
        marginTop = findViewById(R.id.marginTop);
        marginBottom = findViewById(R.id.marginBottom);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideSoftInput();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                hideSoftInput();
                //set LayoutParams
                int windowWidth = DensityUtil.getWindowWidth(this);
                int windowHeight = DensityUtil.getWindowHeight(this);

                int width = Integer.valueOf(this.width.getText().toString());
                int height = Integer.valueOf(this.height.getText().toString());
                if (width > windowWidth) {
                    width = windowWidth;
                }
                if (height > windowHeight) {
                    height = windowHeight;
                }
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);

                int leftRight = Gravity.RIGHT;
                int topBottom = Gravity.BOTTOM;
                if (left.isChecked()) {
                    leftRight = Gravity.LEFT;
                }
                if (top.isChecked()) {
                    topBottom = Gravity.TOP;
                }
                layoutParams.gravity = leftRight | topBottom;

                Integer marginLeft = Integer.valueOf(this.marginLeft.getText().toString());
                Integer marginTop = Integer.valueOf(this.marginTop.getText().toString());
                Integer marginRight = Integer.valueOf(this.marginRight.getText().toString());
                Integer marginBottom = Integer.valueOf(this.marginBottom.getText().toString());

                if (marginLeft > windowWidth - width) {
                    marginLeft = windowWidth - width;
                }
                if (marginRight > windowWidth - width) {
                    marginRight = windowWidth - width;
                }
                if (marginTop > windowHeight - height) {
                    marginTop = windowHeight - height;
                }
                if (marginBottom > windowHeight - height) {
                    marginBottom = windowHeight - height;
                }

                layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);


                VideoView tinyVideoView = new VideoView(this);
                tinyVideoView.setUp("http://vfx.mtime.cn/Video/2018/06/06/mp4/180606101738263858.mp4", VideoView.WindowType.TINY);
                ControlPanel controlPanel = new ControlPanel(this);
                tinyVideoView.setControlPanel(controlPanel);
                tinyVideoView.start();
                tinyVideoView.startTinyWindow(layoutParams);
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
