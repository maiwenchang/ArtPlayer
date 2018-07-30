package org.salient.artplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * > Created by Mai on 2018/7/10
 * *
 * > Description:视频播放控制面板的基类
 * *
 */
public abstract class AbsControlPanel extends FrameLayout implements MediaStateListener, View.OnClickListener, View.OnTouchListener {

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
//        if (mTarget == null) {
//            ViewParent viewParent = getParent();
//            if (viewParent != null) {
//                ViewGroup parent = (ViewGroup) viewParent;
//                if (parent instanceof VideoView) {
//                    VideoView videoView = (VideoView) parent;
//                    videoView.setControlPanel(this);
//                }
//            }
//        }
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
}
