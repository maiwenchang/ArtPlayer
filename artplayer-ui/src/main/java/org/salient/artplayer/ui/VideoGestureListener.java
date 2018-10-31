package org.salient.artplayer.ui;

import android.app.Activity;
import android.app.Service;
import android.media.AudioManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
 * Created by Mai on 2018/8/7
 * *
 * Description:
 * *
 */
public class VideoGestureListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    private AbsControlPanel mControlPanel;
    private float currentX;
    private float currentY;
    private float currentWidth;
    private float currentHeight;
    private float baseValue; //缩放时，两指间初始距离
    private int mVolume = -1;//当前声音
    private int mMaxVolume;//最大声音
    private float mBrightness = -1f;//当前亮度
    private AudioManager mAudioManager;
    public ProgressBar pbOperation;//调节音量
    public ImageView imgOperation;//
    public LinearLayout llOperation;

    private boolean firstTouch;//按住屏幕不放的第一次点击，则为true
    private boolean mChangePosition;//判断是改变进度条则为true，否则为false
    private boolean mChangeBrightness;//判断是不是改变亮度的操作
    private boolean mChangeVolume;//判断是不是改变音量的操作
    private SeekBar seekBar;
    private int preDuration;//手势调整进度条的滑动距离
    private LinearLayout llProgressTime;//展示手势滑动进度条的图层
    private TextView tvProgressTime;//展示手势滑动改变多少的进度条

    private VideoGestureListener() {
    }

    public VideoGestureListener(AbsControlPanel controlPanel) {
        mControlPanel = controlPanel;
        llOperation = mControlPanel.findViewById(R.id.llOperation);
        pbOperation = mControlPanel.findViewById(R.id.pbOperation);
        imgOperation = mControlPanel.findViewById(R.id.imgOperation);
        mAudioManager = (AudioManager) mControlPanel.getContext().getSystemService(Service.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBar = mControlPanel.findViewById(R.id.bottom_seek_progress);
        llProgressTime = mControlPanel.findViewById(R.id.llProgressTime);
        tvProgressTime = mControlPanel.findViewById(R.id.tvProgressTime);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        VideoView target = mControlPanel.getTarget();
        if (target == null) return false;
        baseValue = 0;
        currentX = target.getX();
        currentY = target.getY();
        currentWidth = target.getWidth();
        currentHeight = target.getHeight();

        //取消隐藏音量和亮度的图层的操作
        llOperation.getHandler().removeCallbacks(runnable);
        firstTouch = true;
        mChangePosition = false;
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        mControlPanel.performClick();
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return super.onFling(e1, e2, velocityX, velocityY);
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
        } else if (target.getWindowType() == VideoView.WindowType.FULLSCREEN) {//全屏
            if (e2.getPointerCount() == 1) {//单指移动
                float mOldX = e1.getX(), mOldY = e1.getY();
                int x = (int) e2.getRawX();
                int y = (int) e2.getRawY();
                if (firstTouch) {
                    mChangePosition = Math.abs(distanceX) >= Math.abs(distanceY);
                    if (!mChangePosition) {
                        if (mOldX > currentWidth * 2.0 / 3) {
                            //右边三分之一区域滑动
                            mChangeVolume = true;
                        } else if ((mOldX < currentWidth / 3.0)) {
                            //左边三分之一区域滑动
                            mChangeBrightness = true;
                        }
                    }
                    firstTouch = false;
                }
                if (mChangePosition) {
                    onSeekProgressControl(x - mOldX);
                } else if (mChangeBrightness) {
                    onBrightnessSlide((mOldY - y) * 2 / currentHeight);
                } else if (mChangeVolume) {
                    onVolumeSlide(((mOldY - y) * 2 / currentHeight));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 滑动改变播放的快进快退
     *
     * @param seekDistance
     */
    private void onSeekProgressControl(float seekDistance) {
        if (MediaPlayerManager.instance().getPlayerState() != MediaPlayerManager.PlayerState.PLAYING &&
                MediaPlayerManager.instance().getPlayerState() != MediaPlayerManager.PlayerState.PAUSED)
            return;
        preDuration = seekBar.getProgress() + (int) ((seekDistance / currentWidth) * 30);
        if (preDuration > 100) {
            preDuration = 100;
        } else if (preDuration < 0) {
            preDuration = 0;
        }
        long time = preDuration * MediaPlayerManager.instance().getDuration() / 100;
        if (llProgressTime.getVisibility() == View.GONE) {
            llProgressTime.setVisibility(View.VISIBLE);
        }
        tvProgressTime.setText(Utils.stringForTime(time) + "/" + Utils.stringForTime(MediaPlayerManager.instance().getDuration()));
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        pbOperation.setMax(mMaxVolume * 100);
        if (mVolume == -1) {
            if (mVolume < 0)
                mVolume = 0;
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            // 显示
            imgOperation.setImageResource(R.drawable.salient_volume);
            llOperation.setVisibility(View.VISIBLE);
        }
        float index = (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) index, 0);

        // 变更进度条
        pbOperation.setProgress((int) (index * 100));
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        pbOperation.setMax((int) (1f * 100));
        if (mBrightness < 0) {
            mBrightness = ((Activity) mControlPanel.getContext()).getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            imgOperation.setImageResource(R.drawable.salient_brightness);
            llOperation.setVisibility(View.VISIBLE);
        }

        WindowManager.LayoutParams lpa = ((Activity) mControlPanel.getContext()).getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        ((Activity) mControlPanel.getContext()).getWindow().setAttributes(lpa);

        // 变更进度条
        pbOperation.setProgress((int) (lpa.screenBrightness * 100));
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
        }
        return true;
    }

    /**
     * Zoom window according to two fingers
     * @param e1 The first down motion event that started the scrolling.
     * @param e2 The move motion event that triggered the current onScroll.
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

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            llOperation.setVisibility(View.GONE);
            llProgressTime.setVisibility(View.GONE);
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            //音量变量清空，延迟隐藏声音控件
            mVolume = -1;
            llOperation.postDelayed(runnable, 500);
            //亮度变量清空
            mBrightness = -1f;

            if (mChangePosition) {
                if (seekBar != null) {
                    seekBar.setProgress(preDuration);
                    mControlPanel.onStopTrackingTouch(seekBar);
                }
            }

            //拖动或缩放窗口后修正位置
            if (v instanceof AbsControlPanel) {
                AbsControlPanel absControlPanel = (AbsControlPanel) v;
                VideoView target = absControlPanel.getTarget();
                if (target != null) {
                    VideoView.WindowType windowType = target.getWindowType();
                    if (windowType == VideoView.WindowType.TINY) {
                        revisePosition(target);
                    }
                }
            }
        }


        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        //双击播放或暂停
        if (mControlPanel.getTarget().isCurrentPlaying()) {
            if (MediaPlayerManager.instance().isPlaying()) {
                mControlPanel.getTarget().pause();
            } else {
                mControlPanel.getTarget().start();
            }
        }
        return true;
    }

}
