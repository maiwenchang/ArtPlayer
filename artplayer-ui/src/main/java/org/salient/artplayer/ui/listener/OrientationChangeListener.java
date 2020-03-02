package org.salient.artplayer.ui.listener;

import android.content.pm.ActivityInfo;

import org.salient.artplayer.AbsControlPanel;
import org.salient.artplayer.OrientationEventManager;
import org.salient.artplayer.Utils;
import org.salient.artplayer.VideoView;

/**
 * Created by Mai on 2018/11/14
 * *
 * Description: 处理重力感应横竖屏切换事件
 * *
 */
public class OrientationChangeListener implements OrientationEventManager.OnOrientationChangeListener {

    @Override
    public void onOrientationLandscape(VideoView videoView) {
        //横屏(屏幕左边朝上)
        if (videoView == null) return;
        AbsControlPanel controlPanel = videoView.getControlPanel();
        if (controlPanel == null) return;
        controlPanel.enterFullScreen(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    @Override
    public void onOrientationReverseLandscape(VideoView videoView) {
        //反向横屏(屏幕右边朝上)
        if (videoView == null) return;
        AbsControlPanel controlPanel = videoView.getControlPanel();
        if (controlPanel == null) return;
        controlPanel.enterFullScreen(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        Utils.setRequestedOrientation(videoView.getContext(), ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    @Override
    public void onOrientationPortrait(VideoView videoView) {
        //竖屏
        if (videoView.getWindowType() == VideoView.WindowType.FULLSCREEN) {
            videoView.exitFullscreen();
        }
    }

}
