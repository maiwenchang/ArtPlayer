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

//    private VideoView firstFloor;
//    private VideoView secondFloor;
//
//    public static VideoLayerManager instance() {
//        return VideoLayerManager.ManagerHolder.INSTANCE;
//    }
//
//    //内部类实现单例模式
//    private static class ManagerHolder {
//        private static final VideoLayerManager INSTANCE = new VideoLayerManager();
//    }
//
//    public Object getCurrentData() {
//        if (getCurrentFloor() != null) {
//            return getCurrentFloor().getData();
//        }
//        return null;
//    }
//
//    public VideoView getFirstFloor() {
//        return firstFloor;
//    }
//
//    public void setFirstFloor(VideoView firstFloor) {
//        this.firstFloor = firstFloor;
//    }
//
//    public VideoView getSecondFloor() {
//        return secondFloor;
//    }
//
//    public void setSecondFloor(VideoView secondFloor) {
//        this.secondFloor = secondFloor;
//    }
//
//    public void setCurrentFloor(VideoView currentFloor){
//        if (getSecondFloor() != null) {
//            setSecondFloor(currentFloor);
//        } else {
//            setFirstFloor(currentFloor);
//        }
//    }
//
//    /**
//     * 获取当前正在播放的VideoView
//     *
//     * @return VideoView
//     */
//    public VideoView getCurrentFloor() {
//        if (getSecondFloor() != null) {
//            return getSecondFloor();
//        }
//        return getFirstFloor();
//    }
//
//    /**
//     * 判断VideoView与CurrentFloor是否同一个对象
//     *
//     * @return VideoView
//     */
//    public boolean isCurrentView(@NonNull VideoView videoView) {
//        return getCurrentFloor() != null && videoView == getCurrentFloor();
//    }
//
//    public AbsControlPanel getCurrentControlPanel() {
//        VideoView currentFloor = getCurrentFloor();
//        AbsControlPanel controlPanel = null;
//        if (currentFloor != null) {
//            controlPanel = currentFloor.getControlPanel();
//        }
//        return controlPanel;
//    }
//
//    /**
//     * 结束全部播放
//     */
//    public void completeAll() {
//        if (secondFloor != null) {
//            secondFloor.completeVideo();
//            secondFloor = null;
//        }
//        if (firstFloor != null) {
//            firstFloor.completeVideo();
//            firstFloor = null;
//        }
//    }

}
