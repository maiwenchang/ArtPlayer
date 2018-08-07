package org.salient.artplayer;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by Mai on 2018/8/7
 * *
 * Description:
 * *
 */
public class VideoGestureListener extends GestureDetector.SimpleOnGestureListener {

    private AbsControlPanel mControlPanel;

    private boolean firstTouch;
    private boolean mChangeXY;
    private boolean mChangePosition;
    private boolean mChangeBrightness;
    private boolean mChangeVolume;
    private float currentX;
    private float currentY;

    private VideoGestureListener() {
    }

    public VideoGestureListener(AbsControlPanel controlPanel) {
        mControlPanel = controlPanel;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(this.getClass().getSimpleName(), "" + e.getAction());
        VideoView target = mControlPanel.getTarget();
        if (target == null) return false;
        firstTouch = true;
        currentX = target.getX();
        currentY = target.getY();
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        mControlPanel.performClick();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //Log.d(this.getClass().getSimpleName(), "e2: " + e2.getAction() + ", distanceX: " + distanceX + ", distanceY: " + distanceY);
        VideoView target = mControlPanel.getTarget();
        if (target == null) return false;
        if (target.getWindowType() == VideoView.WindowType.TINY) {//小窗
            return moveWindow(target, e1, e2);
        } else if (target.getWindowType() == VideoView.WindowType.FULLSCREEN) {

        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        return false;
    }

    private boolean moveWindow(VideoView videoView, MotionEvent e1, MotionEvent e2) {
        switch (e2.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float x = currentX + e2.getRawX() - e1.getRawX();
                float y = currentY + e2.getRawY() - e1.getRawY();
                if (x < 0) {
                    x = 0;
                }
                ViewGroup viewParent = (ViewGroup) videoView.getParent();
                int parentWidth = viewParent.getWidth();
                if (x > parentWidth - videoView.getWidth()) {
                    x = parentWidth - videoView.getWidth();
                }
                if (y < 0) {
                    y = 0;
                }
                int parentHeight = viewParent.getHeight();
                if (y > parentHeight - videoView.getHeight()) {
                    y = parentHeight - videoView.getHeight();
                }
                videoView.setY(y);
                videoView.setX(x);
                break;
        }
        return true;
    }

}
