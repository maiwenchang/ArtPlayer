package org.salient;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * > Created by Mai on 2018/7/10
 * *
 * > Description: 视频播放器的控制面板
 * *
 */
public class ControlPanel extends AbsControlPanel implements SeekBar.OnSeekBarChangeListener, CheckBox.OnCheckedChangeListener {

    private final String TAG = ControlPanel.class.getSimpleName();

    private CheckBox start;
    private CheckBox ivVolume;
    private SeekBar bottom_seek_progress;
    private View layout_bottom;
    private View layout_top;
    private TextView current;
    private TextView total;
    private ProgressBar loading;
    private ImageView back;
    private ImageView video_cover;

    public ControlPanel(Context context) {
        super(context);
    }

    public ControlPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getResourceId() {
        return R.layout.salient_layout_video_control_panel;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        start = findViewById(R.id.start);
        bottom_seek_progress = findViewById(R.id.bottom_seek_progress);
        layout_bottom = findViewById(R.id.layout_bottom);
        layout_top = findViewById(R.id.layout_top);
        current = findViewById(R.id.current);
        total = findViewById(R.id.total);
        ivVolume = findViewById(R.id.ivVolume);
        loading = findViewById(R.id.loading);
        back = findViewById(R.id.back);
        video_cover = findViewById(R.id.video_cover);

        back.setOnClickListener(this);
        bottom_seek_progress.setOnSeekBarChangeListener(this);
        ivVolume.setOnCheckedChangeListener(this);
        start.setOnCheckedChangeListener(this);
    }

    @Override
    public void onStateError() {

    }

    @Override
    public void onStateIdle() {
        hideUI(layout_bottom);
        showUI(video_cover);
        start.setChecked(false);
        if (MediaPlayerManager.instance().isMute) {
            ivVolume.setChecked(false);
        } else {
            ivVolume.setChecked(true);
        }
    }

    @Override
    public void onStatePreparing() {
        showUI(loading);
    }

    @Override
    public void onStatePrepared() {
        hideUI(loading);
    }

    @Override
    public void onStatePlaying() {
        start.setChecked(true);
        showUI(layout_bottom);
        hideUI(video_cover);
        if (mTarget != null) {
            if (mTarget.mWindowType == VideoView.WindowType.FULLSCREEN) {
                showUI(layout_top);
            } else {
                hideUI(layout_top);
            }
        }
    }

    @Override
    public void onStatePaused() {
        start.setChecked(false);
    }

    @Override
    public void onStatePlaybackCompleted() {
        start.setChecked(false);
        hideUI(layout_bottom);
        showUI(video_cover);
    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onBufferingUpdate(int progress) {
        if (progress != 0) bottom_seek_progress.setSecondaryProgress(progress);
    }

    @Override
    public void onInfo(int what, int extra) {

    }

    @Override
    public void onProgressUpdate(final int progress, final long position, final long duration) {
        post(new Runnable() {
            @Override
            public void run() {
                bottom_seek_progress.setProgress(progress);
                current.setText(Utils.stringForTime(position));
                total.setText(Utils.stringForTime(duration));
            }
        });
    }

    @Override
    public void onEnterFullScreen() {

    }

    @Override
    public void onExitFullScreen() {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStartTrackingTouch [" + this.hashCode() + "] ");
        MediaPlayerManager.instance().cancelProgressTimer();
        ViewParent vpdown = getParent();
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true);
            vpdown = vpdown.getParent();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStopTrackingTouch [" + this.hashCode() + "] ");
        MediaPlayerManager.instance().startProgressTimer();
        ViewParent vpup = getParent();
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }
        if (MediaPlayerManager.instance().getCurrentState() != MediaPlayerManager.PlayerState.PLAYING &&
                MediaPlayerManager.instance().getCurrentState() != MediaPlayerManager.PlayerState.PAUSED) return;
        long time = seekBar.getProgress() * MediaPlayerManager.instance().getDuration() / 100;
        MediaPlayerManager.instance().seekTo(time);
        Log.i(TAG, "seekTo " + time + " [" + this.hashCode() + "] ");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            long duration = MediaPlayerManager.instance().getDuration();
            current.setText(Utils.stringForTime(progress * duration / 100));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.ivVolume) {
            if (isChecked) {
                MediaPlayerManager.instance().mute(false);
            } else {
                MediaPlayerManager.instance().mute(true);
            }
        } else if (id == R.id.start) {
            //todo 抛出wifi提醒

//            if (!Utils.getCurrentFromDataSource(mTarget.dataSourceObjects, mTarget.currentUrlMapIndex).toString().startsWith("file") && !
//                    Utils.getCurrentFromDataSource(mTarget.dataSourceObjects, mTarget.currentUrlMapIndex).toString().startsWith("/") &&
//                    !Utils.isWifiConnected(getContext()) /*&& !WIFI_TIP_DIALOG_SHOWED*/) {
//
//                //showWifiDialog();
//
//                return;
//            }

            if (isChecked) {
                mTarget.start();
            } else {
                mTarget.pause();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.surface_container) {
            if (mTarget == null) return;
            if (mTarget.isCurrentPlay()
                    && mTarget.mWindowType == VideoView.WindowType.NORMAL
                    && MediaPlayerManager.instance().getCurrentState() == MediaPlayerManager.PlayerState.PLAYING) {
                mTarget.startWindowFullscreen(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        } else if (id == R.id.back) {
            if (mTarget == null) return;
            if (mTarget.mWindowType == VideoView.WindowType.FULLSCREEN) {
                MediaPlayerManager.instance().backPress(getContext());
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
