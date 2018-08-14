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
    private float currentWidth;
    private float currentHeight;
    private float baseValue;

    private VideoGestureListener() {
    }

    public VideoGestureListener(AbsControlPanel controlPanel) {
        mControlPanel = controlPanel;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        VideoView target = mControlPanel.getTarget();
        if (target == null) return false;
        firstTouch = true;
        baseValue = 0;
        currentX = target.getX();
        currentY = target.getY();
        currentWidth = target.getWidth();
        currentHeight = target.getHeight();
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        mControlPanel.performClick();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        VideoView target = mControlPanel.getTarget();
        if (target == null) return false;
        if (target.getWindowType() == VideoView.WindowType.TINY) {//小窗
            if (e2.getPointerCount() == 1) {//单指移动
                return moveWindow(target, e1, e2);
            } else if (e2.getPointerCount() == 2) {//双指缩放
                return zoomWindow(target, e1, e2);
            }
        } else if (target.getWindowType() == VideoView.WindowType.FULLSCREEN) {

        }
        return false;
    }

    /**
     * Move the window according to the finger position
     * 根据手指移动窗口
     */
    private boolean moveWindow(VideoView target, MotionEvent e1, MotionEvent e2) {
        ViewGroup viewParent = (ViewGroup) target.getParent();
        int parentWidth = viewParent.getWidth();
        int parentHeight = viewParent.getHeight();
        switch (e2.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float x = currentX + e2.getRawX() - e1.getRawX();
                float y = currentY + e2.getRawY() - e1.getRawY();
                if (x < 0) {
                    x = 0;
                }
                if (x > parentWidth - target.getWidth()) {
                    x = parentWidth - target.getWidth();
                }
                if (y < 0) {
                    y = 0;
                }
                if (y > parentHeight - target.getHeight()) {
                    y = parentHeight - target.getHeight();
                }
                target.setY(y);
                target.setX(x);
                break;
            case MotionEvent.ACTION_UP:
                revisePosition(target);
                break;
        }
        return true;
    }

    /**
     * Zoom window according to two fingers
     * 根据两个手指缩放窗口
     */
    private boolean zoomWindow(VideoView target, MotionEvent e1, MotionEvent e2) {
        if (e2.getPointerCount() == 2 && e2.getAction() == MotionEvent.ACTION_MOVE) {
            float x = e2.getX(0) - e2.getX(1);
            float y = e2.getY(0) - e2.getY(1);
            float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
            if (baseValue == 0) {
                baseValue = value;
            } else if (Math.abs(value - baseValue) >= 2) {
                float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                if (Math.abs(scale) > 0.05) {
                    ViewGroup.LayoutParams layoutParams = target.getLayoutParams();
                    float height = currentHeight * scale;
                    float width = currentWidth * scale;
                    float WH = width / height;
                    if (width < 400 * WH) {
                        width = 400 * WH;
                    }
                    ViewGroup viewParent = (ViewGroup) target.getParent();
                    int parentWidth = viewParent.getWidth();
                    if (width > parentWidth) {
                        width = parentWidth;
                    }
                    if (height < 400 / WH) {
                        height = 400 / WH;
                    }
                    int parentHeight = viewParent.getHeight();
                    if (height > parentHeight) {
                        height = parentHeight;
                    }
                    int parentWH = parentWidth / parentHeight;
                    if (WH > parentWH) {
                        height = width / WH;
                    } else {
                        width = height * WH;
                    }
                    layoutParams.width = (int) width;
                    layoutParams.height = (int) height;
                    target.requestLayout();
                }
            }
        } else if (e1.getAction() == MotionEvent.ACTION_UP || e2.getAction() == MotionEvent.ACTION_UP || e2.getAction() == MotionEvent.ACTION_POINTER_UP) {
            revisePosition(target);
        }
        return true;
    }

    /**
     * revise position
     * 修正位置
     */
    private void revisePosition(VideoView target) {
        float X = target.getX();
        float Y = target.getY();
        if (X < 0) {
            X = 0;
        }
        ViewGroup viewParent = (ViewGroup) target.getParent();
        int parentWidth = viewParent.getWidth();
        if (X > parentWidth - target.getWidth()) {
            X = parentWidth - target.getWidth();
        }
        if (Y < 0) {
            Y = 0;
        }
        int parentHeight = viewParent.getHeight();
        if (Y > parentHeight - target.getHeight()) {
            Y = parentHeight - target.getHeight();
        }
        target.setY(Y);
        target.setX(X);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(getClass().getSimpleName(), "onSingleTapUp");
        return super.onSingleTapUp(e);
    }
}
