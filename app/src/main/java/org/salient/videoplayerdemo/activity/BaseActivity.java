package org.salient.videoplayerdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.salient.MediaPlayerManager;
import org.salient.videoplayerdemo.BaseApplication;
import org.salient.videoplayerdemo.bean.MovieData;
import org.salient.videoplayerdemo.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * > Created by Mai on 2018/7/17
 * *
 * > Description:
 * *
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected MovieData mMovieData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieData = BaseApplication.getMovieData();
    }

    public List<VideoBean> getAllComing() {
        List<VideoBean> list = new ArrayList<>();
        for (MovieData.MoviecomingsBean moviecomingsBean : mMovieData.getMoviecomings()) {
            List<VideoBean> videos = moviecomingsBean.getVideos();
            if (videos != null && videos.size() > 0) {
                list.add(videos.get(0));
            }
        }
        return list;
    }

    public List<VideoBean> getAllAttention() {
        List<VideoBean> list = new ArrayList<>();
        for (MovieData.AttentionBean attentionBean : mMovieData.getAttention()) {
            List<VideoBean> videos = attentionBean.getVideos();
            if (videos != null && videos.size() > 0) {
                list.add(videos.get(0));
            }
        }
        return list;
    }

    public VideoBean getRandomVideo(){
        if (mMovieData != null) {
            List<VideoBean> allAttention = getAllAttention();
            return allAttention.get(getRandomInt(0, allAttention.size()));
        }
        return null;
    }

    public int getRandomInt(int min, int max) {
        int i = (int) ((System.currentTimeMillis() % max));
        if (i < min) {
            i = i + min;
        }
        Log.d("BaseActivity", "#getRandomInt():" + i);
        return i;
    }

    @Override
    public void onBackPressed() {
        if (MediaPlayerManager.instance().backPress(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerManager.instance().pause();
    }

}
