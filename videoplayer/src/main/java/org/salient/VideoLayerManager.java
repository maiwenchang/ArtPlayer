package org.salient;

import android.support.annotation.NonNull;

import java.util.Comparator;

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
     * 判断VideoView 与 正在播放的多媒体资源是否匹配;
     * 匹配规则可以通过{@link VideoView#setComparator(Comparator)} 设置;
     * 默认比较{@link VideoView#dataSourceObject} 和 {@link AbsMediaPlayer#currentDataSource}
     * See{@link VideoView#mComparator }
     *
     * @return VideoView
     */
    public boolean isCurrentPlaying(@NonNull VideoView videoView) {
        return getCurrentFloor() != null && videoView.equals(getCurrentFloor());
    }

    /**
     * 判断VideoView与CurrentFloor是否同一个对象
     *
     * @return VideoView
     */
    public boolean isCurrentView(@NonNull VideoView videoView) {
        return getCurrentFloor() != null && videoView == getCurrentFloor();
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
