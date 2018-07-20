package org.salient;

import android.support.annotation.NonNull;

/**
 * > Created by Mai on 2018/57/10
 * *
 * > Description: VideoView布局管理
 * *
 */
public class VideoLayerManager {

    private Object currentData;

    private VideoView firstFloor;
    private VideoView secondFloor;

    public static VideoLayerManager instance() {
        return VideoLayerManager.ManagerHolder.INSTANCE;
    }

    //内部类实现单例模式
    private static class ManagerHolder {
        private static final VideoLayerManager INSTANCE = new VideoLayerManager();
    }

    public <T> T getCurrentData() {
        try {
            return (T) currentData;
        } catch (Exception e) {
            throw new ClassCastException();
        }
    }

    public <T> void setCurrentData(T currentData) {
        this.currentData = currentData;
    }

    public VideoView getFirstFloor() {
        return firstFloor;
    }

    public void setFirstFloor(VideoView firstFloor) {
        this.firstFloor = firstFloor;
        if (firstFloor != null) {
            this.currentData = firstFloor.getData();
        }
    }

    public VideoView getSecondFloor() {
        return secondFloor;
    }

    public void setSecondFloor(VideoView secondFloor) {
        this.secondFloor = secondFloor;

    }

    /**
     * 获取当前正在播放的VideoView
     *
     * @return VideoView
     */
    public VideoView getCurrentFloor() {
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
    public boolean isCurrentPlaying(@NonNull VideoView videoView) {
        return getCurrentFloor() != null && getCurrentFloor().equals(videoView);
    }

    public AbsControlPanel getCurrentControlPanel() {
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
    public void completeAll() {
        if (secondFloor != null) {
            secondFloor.completeVideo();
            secondFloor = null;
        }
        if (firstFloor != null) {
            firstFloor.completeVideo();
            firstFloor = null;
        }
        if (currentData != null) {
            currentData = null;
        }
    }

}
