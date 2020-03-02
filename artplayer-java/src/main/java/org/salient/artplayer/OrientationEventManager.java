package org.salient.artplayer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.view.OrientationEventListener;

/**
 * Created by Mai on 2018/11/14
 * *
 * Description: 重力感应方向管理器
 * *
 */
public class OrientationEventManager {

    private int currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private long orientationListenerDelayTime = 0;
    private int isRotate;//0 代表方向锁定，1 代表没方向锁定

    private OnOrientationChangeListener mOrientationChangeListener;

    /**
     * 加速度传感器监听
     */
    private OrientationEventListener orientationEventListener;

    public void orientationDisable() {
        if (orientationEventListener != null) {
            orientationEventListener.disable();
            orientationEventListener = null;
        }
    }

    public void orientationEnable(final Context context, OnOrientationChangeListener orientationChangeListener) {
        this.mOrientationChangeListener = orientationChangeListener;
        orientationEventListener = new OrientationEventListener(context, 5) { // 加速度传感器监听，用于自动旋转屏幕
            @Override
            public void onOrientationChanged(int orientation) {
                //Log.d(getClass().getSimpleName(), "onOrientationChanged: " + orientation);
                try {
                    //系统是否开启方向锁定
                    isRotate = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                if (isRotate == 0) return;//方向被锁定，直接返回
                if ((orientation >= 300 || orientation <= 30) && System.currentTimeMillis() - orientationListenerDelayTime > 1000) {
                    //屏幕顶部朝上
                    onOrientationPortrait();
                    orientationListenerDelayTime = System.currentTimeMillis();
                } else if (orientation >= 260 && orientation <= 280
                        && System.currentTimeMillis() - orientationListenerDelayTime > 1000) {
                    //屏幕左边朝上
                    onOrientationLandscape();
                    orientationListenerDelayTime = System.currentTimeMillis();
                } else if (orientation >= 70 && orientation <= 90
                        && System.currentTimeMillis() - orientationListenerDelayTime > 1000) {
                    //屏幕右边朝上
                    onOrientationReverseLandscape();
                    orientationListenerDelayTime = System.currentTimeMillis();
                }
            }
        };
        currentOrientation = Utils.getRequestedOrientation(context);
        orientationEventListener.enable();
    }


    /**
     * 横屏(屏幕左边朝上)
     */
    private void onOrientationLandscape() {
        VideoView videoView = MediaPlayerManager.instance().getCurrentVideoView();
        if (videoView != null) {
            if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) return;
            if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    && (videoView.getWindowType() == VideoView.WindowType.FULLSCREEN)) {
                currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                return;
            }
            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
            if (videoView.getWindowType() != VideoView.WindowType.FULLSCREEN) {
                if (mOrientationChangeListener != null) {
                    mOrientationChangeListener.onOrientationLandscape(videoView);
                }
            }
        }

    }


    /**
     * 反向横屏(屏幕右边朝上)
     */
    private void onOrientationReverseLandscape() {
        VideoView videoView = MediaPlayerManager.instance().getCurrentVideoView();
        if (videoView != null) {
            if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) return;
            if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    && (videoView.getWindowType() == VideoView.WindowType.FULLSCREEN)) {
                currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                return;
            }
            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
            if (videoView.getWindowType() != VideoView.WindowType.FULLSCREEN) {
                if (mOrientationChangeListener != null) {
                    mOrientationChangeListener.onOrientationReverseLandscape(videoView);
                }
            }
        }

    }


    /**
     * 竖屏
     */
    private void onOrientationPortrait() {
        VideoView videoView = MediaPlayerManager.instance().getCurrentVideoView();
        if (videoView != null) {
            if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                return;
            }
            if ((currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    || currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                    && !(videoView.getWindowType() == VideoView.WindowType.FULLSCREEN)) {
                currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                return;
            }
            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

            if (mOrientationChangeListener != null) {
                mOrientationChangeListener.onOrientationPortrait(videoView);
            }
        }

    }

    /**
     *  设置重力感应横竖屏管理
     */
    public void setOnOrientationChangeListener(OrientationEventManager.OnOrientationChangeListener orientationChangeListener) {
        mOrientationChangeListener = orientationChangeListener;
    }

    public interface OnOrientationChangeListener {

        void onOrientationLandscape(VideoView videoView);

        void onOrientationReverseLandscape(VideoView videoView);

        void onOrientationPortrait(VideoView videoView);

    }
}
