package org.salient.artvideoplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.salient.artplayer.AbsMediaPlayer;
import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.SystemMediaPlayer;
import org.salient.artplayer.exo.ExoPlayer;
import org.salient.artplayer.ijk.IjkPlayer;
import org.salient.artvideoplayer.BaseApplication;
import org.salient.artvideoplayer.bean.MovieData;
import org.salient.artvideoplayer.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mai on 2018/7/17
 * *
 * Description:
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
        if (MediaPlayerManager.instance().backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftInput();
        MediaPlayerManager.instance().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMenuState();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideSoftInput();
    }

    private Menu mMenu;

    /**
     * 刷新标题栏菜单状态
     */
    private void refreshMenuState(){
        if (mMenu != null) {
            AbsMediaPlayer mediaPlayer = MediaPlayerManager.instance().getMediaPlayer();
            if (mediaPlayer instanceof SystemMediaPlayer) {
                mMenu.getItem(1).getSubMenu().getItem(0).setChecked(true);
                mMenu.getItem(0).setTitle("Using: MediaPlayer");
            } else if (mediaPlayer instanceof IjkPlayer) {
                mMenu.getItem(1).getSubMenu().getItem(1).setChecked(true);
                mMenu.getItem(0).setTitle("Using: IjkPlayer");
            } else if (mediaPlayer instanceof ExoPlayer) {
                mMenu.getItem(1).getSubMenu().getItem(2).setChecked(true);
                mMenu.getItem(0).setTitle("Using: ExoPlayer");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bar_setting, menu);
        mMenu = menu;
        refreshMenuState();
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (mMenu == null) return super.onMenuOpened(featureId, menu);
        refreshMenuState();
        return super.onMenuOpened(featureId, mMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.isChecked()) return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menu_MediaPlayer:
                mMenu.getItem(0).setTitle("Using: MediaPlayer");
                MediaPlayerManager.instance().releasePlayerAndView(this);
                MediaPlayerManager.instance().setMediaPlayer(new SystemMediaPlayer());
                break;
            case R.id.menu_IjkPlayer:
                mMenu.getItem(0).setTitle("Using: IjkPlayer");
                MediaPlayerManager.instance().releasePlayerAndView(this);
                MediaPlayerManager.instance().setMediaPlayer(new IjkPlayer());
                break;
            case R.id.menu_ExoPlayer:
                mMenu.getItem(0).setTitle("Using: ExoPlayer");
                MediaPlayerManager.instance().releasePlayerAndView(this);
                MediaPlayerManager.instance().setMediaPlayer(new ExoPlayer(this));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
