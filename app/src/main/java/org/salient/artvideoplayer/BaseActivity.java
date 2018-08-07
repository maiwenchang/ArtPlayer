package org.salient.artvideoplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artvideoplayer.BaseApplication;
import org.salient.artvideoplayer.bean.MovieData;
import org.salient.artvideoplayer.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * > Created by Mai on 2018/7/17
 * *
 * > Description:
 * *
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public List<VideoBean> getAllComing() {
        List<VideoBean> list = new ArrayList<>();
        MovieData mMovieData = BaseApplication.getMovieData();
        if (mMovieData != null) {
            for (MovieData.MoviecomingsBean moviecomingsBean : mMovieData.getMoviecomings()) {
                List<VideoBean> videos = moviecomingsBean.getVideos();
                if (videos != null && videos.size() > 0) {
                    list.add(videos.get(0));
                }
            }
        }
        return list;
    }

    public List<VideoBean> getAllAttention() {
        List<VideoBean> list = new ArrayList<>();
        MovieData mMovieData = BaseApplication.getMovieData();
        if (mMovieData != null) {
            for (MovieData.AttentionBean attentionBean : mMovieData.getAttention()) {
                List<VideoBean> videos = attentionBean.getVideos();
                if (videos != null && videos.size() > 0) {
                    list.add(videos.get(0));
                }
            }
        }
        return list;
    }

    public VideoBean getRandomVideo() {
        MovieData mMovieData = BaseApplication.getMovieData();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerManager.instance().releasePlayerAndView(this);
    }


    //显示软键盘
    public void showSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    //收起软键盘
    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
