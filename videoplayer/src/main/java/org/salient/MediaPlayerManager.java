package org.salient;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * > Created by Mai on 2018/7/10
 * *
 * > Description: 视频播放器管理类
 * *
 */
public class MediaPlayerManager implements TextureView.SurfaceTextureListener {

    private PlayerState mCurrentState = PlayerState.IDLE;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;
    private final String TAG = MediaPlayerManager.class.getSimpleName();
    private final int FULL_SCREEN_NORMAL_DELAY = 300;
    public ResizeTextureView textureView;
    public SurfaceTexture surfaceTexture;
    public Surface surface;
    public int currentVideoWidth = 0;
    public int currentVideoHeight = 0;
    public long mClickFullScreenTime = 0;
    public boolean isMute = false;
    private AbsMediaPlayer mediaPlayer;
    //private int videoRotation = 0;
    private Timer mProgressTimer;
    private ProgressTimerTask mProgressTimerTask;

    private MediaPlayerManager() {
        if (mediaPlayer == null) {
            mediaPlayer = new SystemMediaPlayer();
            onAudioFocusChangeListener = new AudioFocusManager();
        }
    }

    public static MediaPlayerManager instance() {
        return ManagerHolder.INSTANCE;
    }

    public PlayerState getCurrentState() {
        return mCurrentState;
    }

    //正在播放的url或者uri
    public Object getCurrentDataSource() {
        return instance().mediaPlayer.getCurrentDataSource();
    }

    public void setCurrentDataSource(Object currentDataSource) {
        instance().mediaPlayer.setCurrentDataSource(currentDataSource);
    }

    public long getCurrentPosition() {
        return instance().mediaPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return instance().mediaPlayer.getDuration();
    }

    public void seekTo(long time) {
        instance().mediaPlayer.seekTo(time);
    }

    public void pause() {
        if (isPlaying()) {
            instance().mediaPlayer.pause();
        }
    }

    public void start() {
        instance().mediaPlayer.start();
    }

    public boolean isPlaying() {
        return mCurrentState == PlayerState.PLAYING && instance().mediaPlayer.isPlaying();
    }

    public void releaseAllVideos() {
        if ((System.currentTimeMillis() - mClickFullScreenTime) > FULL_SCREEN_NORMAL_DELAY) {
            Log.d(TAG, "releaseAllVideos");
            VideoLayerManager.instance().completeAll();
            MediaPlayerManager.instance().releaseMediaPlayer();
        }
    }

    public void releaseMediaPlayer() {
        mediaPlayer.release();
    }

    public void prepare() {
        releaseMediaPlayer();
        currentVideoWidth = 0;
        currentVideoHeight = 0;
        mediaPlayer.prepare();
        if (surfaceTexture != null) {
            if (surface != null) {
                surface.release();
            }
            surface = new Surface(surfaceTexture);
            mediaPlayer.setSurface(surface);
        }
    }

    public void abandonAudioFocus(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }

    public void setOnAudioFocusChangeListener(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    public void setAudioFocusManager(AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener) {
        this.onAudioFocusChangeListener = onAudioFocusChangeListener;
    }

    public void updateState(PlayerState playerState) {
        Log.i(TAG, "updateState [" + playerState.name() + "] ");
        mCurrentState = playerState;
        switch (mCurrentState) {
            case PLAYING:
            case PAUSED:
                startProgressTimer();
                break;
            case ERROR:
            case IDLE:
            case PLAYBACK_COMPLETED:
                cancelProgressTimer();
                break;
        }
        VideoView currentFloor = VideoLayerManager.instance().getCurrentFloor();
        if (currentFloor != null && VideoLayerManager.instance().isCurrentPlaying(currentFloor)) {
            AbsControlPanel controlPanel = currentFloor.getControlPanel();
            if (controlPanel != null) {
                controlPanel.notifyStateChange();//通知当前的控制面板改变布局
            }
        }
    }

    public void onVideoSizeChanged() {
        Log.i(TAG, "onVideoSizeChanged " + " [" + this.hashCode() + "] ");
        if (textureView != null) {
//            if (videoRotation != 0) {
//                textureView.setRotation(videoRotation);
//            }
            textureView.setVideoSize(currentVideoWidth, currentVideoHeight);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        if (VideoLayerManager.instance().getCurrentFloor() == null) return;
        Log.i(TAG, "onSurfaceTextureAvailable [" + VideoLayerManager.instance().getCurrentFloor().hashCode() + "] ");
        if (MediaPlayerManager.instance().surfaceTexture == null) {
            MediaPlayerManager.instance().surfaceTexture = surfaceTexture;
            prepare();
        } else {
            textureView.setSurfaceTexture(MediaPlayerManager.instance().surfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.i(TAG, "onSurfaceTextureSizeChanged [" + VideoLayerManager.instance().getCurrentFloor().hashCode() + "] ");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        Log.i(TAG, "onSurfaceTextureDestroyed [" + VideoLayerManager.instance().getCurrentFloor().hashCode() + "] ");
        return MediaPlayerManager.instance().surfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        //Log.i("testt", "onSurfaceTextureUpdated [" + VideoLayerManager.instance().getCurrentFloor().hashCode() + "] ");
    }

    public void setMediaPlayer(AbsMediaPlayer mediaPlayer) {
        MediaPlayerManager.instance().mediaPlayer = mediaPlayer;
    }

    /**
     * 设置静音
     *
     * @param mute boolean
     */
    public void setMute(boolean mute) {
        this.isMute = mute;
    }

    public void mute(boolean mute) {
        this.isMute = mute;
        instance().mediaPlayer.mute(mute);
    }

    /**
     * 直接开启全屏(单个视频)
     *
     * @param context Context
     * @param clazz   VideoView.class
     * @param url     视频Url
     * @param headers 标题
     */
    public void startFullscreen(Context context, Class<AbsControlPanel> clazz, String url, int screenRotation, Object... headers) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        //map.put(URL_KEY_DEFAULT, url);
        Object[] dataSourceObjects = new Object[1];
        dataSourceObjects[0] = map;
        startFullscreen(context, clazz, dataSourceObjects, 0, screenRotation, headers);
    }

    /**
     * 直接开启全屏
     *
     * @param context           Context
     * @param clazz             VideoView.class
     * @param dataSourceObjects 视频dataSourceObjects
     * @param headers           标题
     */
    public void startFullscreen(Context context, Class<AbsControlPanel> clazz, Object[] dataSourceObjects, int screenRotation, Object... headers) {
        Utils.hideSupportActionBar(context);
        Utils.setRequestedOrientation(context, screenRotation);
        ViewGroup vp = (Utils.scanForActivity(context)).findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.salient_video_fullscreen_id);
        if (old != null) {
            vp.removeView(old);
        }
        try {
            //初始化一个VideoView
            VideoView fullScreenVideoView = new VideoView(context);
            fullScreenVideoView.setId(R.id.salient_video_fullscreen_id);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            vp.addView(fullScreenVideoView, lp);
            fullScreenVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            fullScreenVideoView.setUp(dataSourceObjects, VideoView.WindowType.FULLSCREEN, headers);

            fullScreenVideoView.addTextureView();

            //添加控制面板
            //参数类型
            Class<?>[] params = {Context.class};
            //参数值
            Object[] values = {context};
            Constructor<? extends AbsControlPanel> constructor = clazz.getDeclaredConstructor(params);
            AbsControlPanel absControlPanel = constructor.newInstance(values);
            fullScreenVideoView.setControlPanel(absControlPanel);

            MediaPlayerManager.instance().mClickFullScreenTime = System.currentTimeMillis();
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拦截返回键
     */
    public boolean backPress(Context context) {
        Log.i(TAG, "backPress");
        if ((System.currentTimeMillis() - mClickFullScreenTime) < FULL_SCREEN_NORMAL_DELAY) {
            return false;
        }

        if (VideoLayerManager.instance().getSecondFloor() != null) {//退出全屏，返回常规窗口
            mClickFullScreenTime = System.currentTimeMillis();
            if (true/*Utils.dataSourceObjectsContainsUri(VideoLayerManager.instance().getFirstFloor().getDataSourceObject(), instance().getCurrentDataSource())*/) {
                VideoLayerManager.instance().getFirstFloor().closeWindowFullScreen();
            } else {
                quitFullscreenOrTinyWindow(context);
            }
            return true;
        } else if (VideoLayerManager.instance().getFirstFloor() != null &&
                (VideoLayerManager.instance().getFirstFloor().mWindowType == VideoView.WindowType.FULLSCREEN ||
                        VideoLayerManager.instance().getFirstFloor().mWindowType == VideoView.WindowType.TINY)) {//退出全屏（直接开启的全屏，只有一层，没有常规窗口）,或退出小屏
            mClickFullScreenTime = System.currentTimeMillis();
            quitFullscreenOrTinyWindow(context);
            return true;
        }
        return false;
    }

    /**
     * 直接退出全屏和小窗
     * <p>
     * 常规窗口和当前窗口（全屏或小屏）播的不是一个视频
     *
     * @param context Context
     */
    public void quitFullscreenOrTinyWindow(Context context) {
        instance().clearFloatScreen(context);
        instance().releaseMediaPlayer();
        VideoLayerManager.instance().completeAll();
    }

    public void clearFloatScreen(Context context) {
        Utils.setRequestedOrientation(context, VideoLayerManager.instance().getCurrentFloor().getScreenOrientation());
        Utils.showSupportActionBar(context);
        ViewGroup vp = (Utils.scanForActivity(context))//.getWindow().getDecorView();
                .findViewById(Window.ID_ANDROID_CONTENT);
        VideoView fullScreenWindow = vp.findViewById(R.id.salient_video_fullscreen_id);
        VideoView tinyWindow = vp.findViewById(R.id.salient_video_tiny_id);

        if (fullScreenWindow != null) {
            vp.removeView(fullScreenWindow);
            if (fullScreenWindow.getTextureViewContainer() != null)
                fullScreenWindow.getTextureViewContainer().removeView(MediaPlayerManager.instance().textureView);
        }
        if (tinyWindow != null) {
            vp.removeView(tinyWindow);
            if (tinyWindow.getTextureViewContainer() != null)
                tinyWindow.getTextureViewContainer().removeView(MediaPlayerManager.instance().textureView);
        }
        VideoLayerManager.instance().setSecondFloor(null);
    }

    public void startProgressTimer() {
        Log.i(TAG, "startProgressTimer: " + " [" + this.hashCode() + "] ");
        cancelProgressTimer();
        mProgressTimer = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        mProgressTimer.schedule(mProgressTimerTask, 0, 300);
    }

    public void cancelProgressTimer() {
        if (mProgressTimer != null) {
            mProgressTimer.cancel();
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
        }
    }

    public long getCurrentPositionWhenPlaying() {
        long position = 0;
        if (mCurrentState == PlayerState.PLAYING || mCurrentState == PlayerState.PAUSED) {
            try {
                position = getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return position;
    }

    // all possible MediaPlayer states
    public enum PlayerState {
        ERROR,
        IDLE,
        PREPARING,
        PREPARED,
        PLAYING,
        PAUSED,
        PLAYBACK_COMPLETED
    }

    //内部类实现单例模式
    private static class ManagerHolder {
        private static final MediaPlayerManager INSTANCE = new MediaPlayerManager();
    }

    public class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mCurrentState == PlayerState.PLAYING || mCurrentState == PlayerState.PAUSED) {
                long position = getCurrentPositionWhenPlaying();
                long duration = getDuration();
                int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
                AbsControlPanel currentControlPanel = VideoLayerManager.instance().getCurrentControlPanel();
                if (currentControlPanel != null) {
                    currentControlPanel.onProgressUpdate(progress, position, duration);
                }
            }
        }
    }
}
