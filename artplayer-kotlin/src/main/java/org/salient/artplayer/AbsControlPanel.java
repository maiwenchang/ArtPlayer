package org.salient.artplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

/**
 *  Created by Mai on 2018/7/10
 * *
 *  Description:视频播放控制面板的基类
 * *
 */
public abstract class AbsControlPanel extends FrameLayout implements MediaStateListener, View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener{

    protected VideoView mTarget;

    public AbsControlPanel(Context context) {
        super(context);
        init(context);
    }

    public AbsControlPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AbsControlPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected abstract int getResourceId();

    protected void init(Context context) {
        View.inflate(context, getResourceId(), this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void notifyStateChange() {
        switch (MediaPlayerManager.instance().getPlayerState()) {
            case ERROR:
                onStateError();
                break;
            case IDLE:
                onStateIdle();
                break;
            case PAUSED:
                onStatePaused();
                break;
            case PLAYING:
                onStatePlaying();
                break;
            case PREPARED:
                onStatePrepared();
                break;
            case PREPARING:
                onStatePreparing();
                break;
            case PLAYBACK_COMPLETED:
                onStatePlaybackCompleted();
                break;
        }
    }

    public void setTarget(VideoView target) {
        this.mTarget = target;
    }

    public VideoView getTarget() {
        return mTarget;
    }

    public void hideUI(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(INVISIBLE);
            }
        }
    }

    public void showUI(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public void onStateError() {

    }

    @Override
    public void onStateIdle() {

    }

    @Override
    public void onStatePreparing() {

    }

    @Override
    public void onStatePrepared() {

    }

    @Override
    public void onStatePlaying() {

    }

    @Override
    public void onStatePaused() {

    }

    @Override
    public void onStatePlaybackCompleted() {

    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onBufferingUpdate(int progress) {

    }

    @Override
    public void onInfo(int what, int extra) {

    }

    @Override
    public void onProgressUpdate(int progress, long position, long duration) {

    }

    @Override
    public void onEnterSecondScreen() {

    }

    @Override
    public void onExitSecondScreen() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * 进入全屏
     * @param screenOrientation 屏幕方向
     */
    public abstract void enterFullScreen(int screenOrientation);
}
