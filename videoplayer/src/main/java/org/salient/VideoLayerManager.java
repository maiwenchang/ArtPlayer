package org.salient;

/**
 * > Created by Mai on 2018/57/10
 * *
 * > Description: VideoView布局管理
 * *
 */
public class VideoLayerManager {

    private static VideoView firstFloor;
    private static VideoView secondFloor;

    public static VideoView getFirstFloor() {
        return firstFloor;
    }

    public static void setFirstFloor(VideoView firstFloor) {
        VideoLayerManager.firstFloor = firstFloor;
    }

    public static VideoView getSecondFloor() {
        return secondFloor;
    }

    public static void setSecondFloor(VideoView secondFloor) {
        VideoLayerManager.secondFloor = secondFloor;
    }

    /**
     * 获取当前正在播放的VideoView
     *
     * @return VideoView
     */
    public static VideoView getCurrentFloor() {
        if (getSecondFloor() != null) {
            return getSecondFloor();
        }
        return getFirstFloor();
    }

    /**
     * 是否正在播放的VideoView
     *
     * @return VideoView
     */
    public static boolean isCurrentFloor(VideoView videoView) {
        return getCurrentFloor() != null
                && getCurrentFloor() == videoView;
    }

    public static AbsControlPanel getCurrentControlPanel() {
        VideoView currentFloor = getCurrentFloor();
        AbsControlPanel controlPanel = null;
        if (currentFloor != null) {
            controlPanel = currentFloor.getControlPanel();
        }
        return controlPanel;
    }

    /**
     * 结束全部播放
     */
    public static void completeAll() {
        if (secondFloor != null) {
            secondFloor.completeVideo();
            secondFloor = null;
        }
        if (firstFloor != null) {
            firstFloor.completeVideo();
            firstFloor = null;
        }
    }

}
