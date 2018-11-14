package org.salient.artplayer;

import android.content.Context;
import android.content.pm.ActivityInfo;

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
        //new VideoView
        VideoView newVideoView = new VideoView(videoView.getContext());
        //set parent
        newVideoView.setParentVideoView(videoView);
        //optional: set ControlPanel
        newVideoView.setControlPanel(videoView.getControlPanel());
        newVideoView.setUp(videoView.getDataSourceObject(), VideoView.WindowType.FULLSCREEN, videoView.getData());
        //start fullscreen
        newVideoView.startFullscreen(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    @Override
    public void onOrientationReverseLandscape(VideoView videoView) {
        //反向横屏(屏幕右边朝上)
        //new VideoView
        VideoView newVideoView = new VideoView(videoView.getContext());
        //set parent
        newVideoView.setParentVideoView(videoView);
        newVideoView.setUp(videoView.getDataSourceObject(), VideoView.WindowType.FULLSCREEN, videoView.getData());
        //optional: set ControlPanel
        newVideoView.setControlPanel(videoView.getControlPanel());
        //start fullscreen
        newVideoView.startFullscreen(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        Utils.setRequestedOrientation(videoView.getContext(),ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    @Override
    public void onOrientationPortrait(VideoView videoView) {
        //竖屏
        VideoView parentVideoView = videoView.getParentVideoView();
        if (parentVideoView != null) {
            parentVideoView.setControlPanel(videoView.getControlPanel());
        }
        videoView.exitFullscreen();
    }

}
