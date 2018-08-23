package org.salient.artplayer.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.salient.artplayer.AbsControlPanel;
import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.Utils;
import org.salient.artplayer.VideoView;

/**
 * > Created by Mai on 2018/7/10
 * *
 * > Description: 视频播放器的控制面板，旨在展示与视频播放相关的核心功能；
 * <p>
 * *
 */
public class ControlPanel extends AbsControlPanel implements SeekBar.OnSeekBarChangeListener {

    private final String TAG = ControlPanel.class.getSimpleName();

    private final long autoDismissTime = 3000;
    private int mWhat;
    private int mExtra;
    protected GestureDetector mGestureDetector;

    private CheckBox start;
    private CheckBox ivVolume;
    private SeekBar bottom_seek_progress;
    private View layout_bottom;
    private View layout_top;
    private TextView current;
    private TextView total;
    private ProgressBar loading;
    private ImageView ivLeft;
    private ImageView video_cover;
    private ImageView ivRight;
    private LinearLayout llAlert;
    private TextView tvAlert;
    private TextView tvConfirm;
    private TextView tvTitle;
    private LinearLayout llOperation;

    private Runnable mDismissTask = new Runnable() {
        @Override
        public void run() {
            if (MediaPlayerManager.instance().getCurrentVideoView() == mTarget && MediaPlayerManager.instance().isPlaying()) {
                hideUI(layout_bottom, layout_top, start);
            }
        }
    };

    public ControlPanel(Context context) {
        super(context);
    }

    public ControlPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlPanel(Context context, AttributeSet attrs, int defStyleAttr) {
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
        ivLeft = findViewById(R.id.ivLeft);
        video_cover = findViewById(R.id.video_cover);
        llAlert = findViewById(R.id.llAlert);
        tvAlert = findViewById(R.id.tvAlert);
        tvConfirm = findViewById(R.id.tvConfirm);
        ivRight = findViewById(R.id.ivRight);
        tvTitle = findViewById(R.id.tvTitle);
        llOperation = findViewById(R.id.llOperation);

        ivRight.setOnClickListener(this);
        ivLeft.setOnClickListener(this);
        bottom_seek_progress.setOnSeekBarChangeListener(this);
        ivVolume.setOnClickListener(this);
        start.setOnClickListener(this);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTarget == null) return;
                if (!mTarget.isCurrentPlaying()) {
                    return;
                }
                if (MediaPlayerManager.instance().getPlayerState() == MediaPlayerManager.PlayerState.PLAYING) {
                    cancelDismissTask();
                    if (layout_bottom.getVisibility() != VISIBLE) {
                        showUI(layout_bottom, layout_top, start);
                    } else {
                        hideUI(layout_top, layout_bottom, start);
                    }
                    startDismissTask();
                }
            }
        });
        final VideoGestureListener videoGestureListener = new VideoGestureListener(this);
        mGestureDetector = new GestureDetector(getContext(), videoGestureListener);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mGestureDetector.onTouchEvent(event))
                    return true;
                return videoGestureListener.onTouch(v, event);
            }
        });
    }

    @Override
    public void onStateError() {
        hideUI(start, layout_top, layout_bottom, loading);
        showUI(llAlert);
        //MediaPlayerManager.instance().releaseMediaPlayer();
        tvAlert.setText("oops~~ unknown error");
        tvConfirm.setText("retry");
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTarget != null) {
                    hideUI(llAlert);
                    mTarget.start();
                }
            }
        });
    }

    @Override
    public void onStateIdle() {
        hideUI(layout_bottom, layout_top, loading, llAlert);
        showUI(video_cover, start);
        start.setChecked(false);
        //if (mTarget!=null && mTarget.get)
        SynchronizeViewState();
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
        showUI(layout_bottom, layout_top);
        hideUI(video_cover, loading, llOperation);
        startDismissTask();
    }

    @Override
    public void onStatePaused() {
        start.setChecked(false);
        showUI(start, layout_bottom);
        hideUI(video_cover, loading, llOperation);
    }

    @Override
    public void onStatePlaybackCompleted() {
        start.setChecked(false);
        hideUI(layout_bottom, loading);
        showUI(start);
        if (mTarget.getWindowType() == VideoView.WindowType.FULLSCREEN || mTarget.getWindowType() == VideoView.WindowType.TINY) {
            showUI(layout_top);
        }
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
        mWhat = what;
        mExtra = extra;
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
    public void onEnterSecondScreen() {
        if (mTarget != null && mTarget.getWindowType() == VideoView.WindowType.FULLSCREEN) {
            hideUI(ivRight);
        }
        showUI(ivLeft);
        SynchronizeViewState();
    }

    @Override
    public void onExitSecondScreen() {
        if (mTarget != null && mTarget.getWindowType() != VideoView.WindowType.TINY) {
            ivLeft.setVisibility(GONE);
        }
        showUI(ivRight);
        SynchronizeViewState();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStartTrackingTouch [" + this.hashCode() + "] ");
        MediaPlayerManager.instance().cancelProgressTimer();
        cancelDismissTask();
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
        startDismissTask();
        ViewParent vpup = getParent();
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }
        if (MediaPlayerManager.instance().getPlayerState() != MediaPlayerManager.PlayerState.PLAYING &&
                MediaPlayerManager.instance().getPlayerState() != MediaPlayerManager.PlayerState.PAUSED)
            return;
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

    //显示WiFi状态提醒
    public void showWifiAlert() {
        hideUI(start, layout_bottom, layout_top, loading);
        showUI(llAlert);
        tvAlert.setText("Is in non-WIFI");
        tvConfirm.setText("continue");
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTarget != null) {
                    hideUI(llAlert);
                    mTarget.start();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        cancelDismissTask();
        int id = v.getId();
        if (id == R.id.ivLeft) {
            if (mTarget == null) return;
            if (mTarget.getWindowType() == VideoView.WindowType.FULLSCREEN) {
                mTarget.exitFullscreen();
            } else if (mTarget.getWindowType() == VideoView.WindowType.TINY) {
                mTarget.exitTinyWindow();
            }
        } else if (id == R.id.ivRight) {
            if (mTarget == null) return;
            if (mTarget.getWindowType() != VideoView.WindowType.FULLSCREEN) {
                //new VideoView
                VideoView videoView = new VideoView(getContext());
                //set parent
                videoView.setParentVideoView(mTarget);
                videoView.setUp(mTarget.getDataSourceObject(), VideoView.WindowType.FULLSCREEN, mTarget.getData());
                videoView.setControlPanel(new ControlPanel(getContext()));
                //start fullscreen
                videoView.startFullscreen(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                //MediaPlayerManager.instance().startFullscreen(videoView, ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }

        } else if (id == R.id.ivVolume) {
            if (ivVolume.isChecked()) {
                MediaPlayerManager.instance().setMute(false);
            } else {
                MediaPlayerManager.instance().setMute(true);
            }

        } else if (id == R.id.start) {
            if (mTarget == null) {
                return;
            }
            if (start.isChecked()) {
                if (mTarget.isCurrentPlaying() && MediaPlayerManager.instance().isPlaying()) {
                    return;
                }
                if (!Utils.isNetConnected(getContext())) {
                    onStateError();
                    return;
                }
                if (!Utils.isWifiConnected(getContext())) {
                    showWifiAlert();
                    return;
                }
                mTarget.start();
            } else {
                mTarget.pause();
            }

        }
        startDismissTask();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    //同步跟MediaPlayer状态无关的视图
    public void SynchronizeViewState() {
        if (MediaPlayerManager.instance().isMute()) {
            ivVolume.setChecked(false);
        } else {
            ivVolume.setChecked(true);
        }
        if (mTarget != null && mTarget.getParentVideoView() != null && mTarget.getParentVideoView().getControlPanel() != null) {
            TextView title = mTarget.getParentVideoView().getControlPanel().findViewById(R.id.tvTitle);
            tvTitle.setText(title.getText() == null ? "" : title.getText());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelDismissTask();
    }

    private void startDismissTask() {
        cancelDismissTask();
        postDelayed(mDismissTask, autoDismissTime);
    }

    private void cancelDismissTask() {
        Handler handler = getHandler();
        if (handler != null && mDismissTask != null) {
            handler.removeCallbacks(mDismissTask);
        }
    }

}
