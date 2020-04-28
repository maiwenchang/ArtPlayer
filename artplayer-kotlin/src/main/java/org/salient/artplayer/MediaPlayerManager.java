package org.salient.artplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  Created by Mai on 2018/7/10
 * *
 *  Description: 视频播放器管理类
 * *
 */
public class MediaPlayerManager implements TextureView.SurfaceTextureListener {

    private final String TAG = getClass().getSimpleName();
    private final int CLICK_EVENT_SPAN = 300;
    //surface
    private ResizeTextureView textureView;
    private SurfaceTexture surfaceTexture;
    private Surface surface;

    private PlayerState mPlayerState = PlayerState.IDLE;
    private Object mCurrentData;
    private long mClickTime = 0;
    private Timer mProgressTimer;
    private ProgressTimerTask mProgressTimerTask;

    // settable by client 可设置的属性
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;//音频焦点事件监听
    private OrientationEventManager.OnOrientationChangeListener mOnOrientationChangeListener;//重力感应事件监听
    private OrientationEventManager mOrientationEventManager;//重力感应事件管理
    private AbsMediaPlayer mediaPlayer;//播放器内核
    private boolean isMute = false; //是否静音
    private boolean isLooping = false; //是否循环播放

    private MediaPlayerManager() {
        if (mediaPlayer == null) {
            mediaPlayer = new SystemMediaPlayer();
            mOrientationEventManager = new OrientationEventManager();
        }
    }

    public static MediaPlayerManager instance() {
        return ManagerHolder.INSTANCE;
    }

    public PlayerState getPlayerState() {
        return mPlayerState;
    }

    //正在播放的url或者uri
    public Object getDataSource() {
        return mediaPlayer.getDataSource();
    }

    public void setDataSource(Object dataSource, Map<String, String> headers) {
        mediaPlayer.setDataSource(dataSource);
        mediaPlayer.setHeaders(headers);
    }

    public long getDuration() {
        return mediaPlayer.getDuration();
    }

    public void seekTo(long time) {
        mediaPlayer.seekTo(time);
    }

    public void pause() {
        if (isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void start() {
        mediaPlayer.start();
    }

    public boolean isMute() {
        return isMute;
    }

    /**
     * 设置静音
     *
     * @param isMute boolean
     */
    public void setMute(boolean isMute) {
        this.isMute = isMute;
        mediaPlayer.mute(isMute);
    }


    /**
     * 设置音量
     *
     * @param leftVolume  左声道
     * @param rightVolume 右声道
     */
    public void setVolume(float leftVolume, float rightVolume) {
        mediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public boolean isLooping() {
        return isLooping;
    }

    /**
     * 设置循环播放
     *
     * @param isLooping boolean
     */
    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
        mediaPlayer.setLooping(isLooping);
    }

    /**
     * 在指定的VideoView上播放,
     * 即把textureView放到目标VideoView上
     *
     * @param target VideoView
     */
    public void playAt(VideoView target) {
        if (target == null) return;
        removeTextureView();
        addTextureView(target);
    }

    public boolean isPlaying() {
        return mPlayerState == PlayerState.PLAYING && mediaPlayer.isPlaying();
    }

    public void releasePlayerAndView(Context context) {
        if ((System.currentTimeMillis() - mClickTime) > CLICK_EVENT_SPAN) {
            Log.d(TAG, "release");
            if (context != null) {
                clearFullscreenLayout(context);
                clearTinyLayout(context);
                Utils.scanForActivity(context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                unbindAudioFocus(context);
                unbindOrientationManager();
                Utils.showSupportActionBar(context);
            }
            releaseMediaPlayer();
            removeTextureView();
            if (surfaceTexture != null) {
                surfaceTexture.release();
            }
            if (surface != null) {
                surface.release();
            }
            textureView = null;
            surfaceTexture = null;
        }
    }

    public void clearFullscreenLayout(Context context) {
        ViewGroup vp = (Utils.scanForActivity(context)).findViewById(Window.ID_ANDROID_CONTENT);
        View oldF = vp.findViewById(R.id.salient_video_fullscreen_id);
        if (oldF != null) {
            vp.removeView(oldF);
            oldF = null;
        }
    }

    public void clearTinyLayout(Context context) {
        ViewGroup vp = (Utils.scanForActivity(context)).findViewById(Window.ID_ANDROID_CONTENT);
        View oldT = vp.findViewById(R.id.salient_video_tiny_id);
        if (oldT != null) {
            vp.removeView(oldT);
            oldT = null;
        }
    }

    public void releaseMediaPlayer() {
        cancelProgressTimer();
        mediaPlayer.release();
        mediaPlayer.setHeaders(null);
        mediaPlayer.setDataSource(null);
        mCurrentData = null;
        updateState(MediaPlayerManager.PlayerState.IDLE);
    }

    /**
     * go into prepare and start
     */
    private void prepare() {
        mediaPlayer.prepare();
        if (surfaceTexture != null) {
            if (surface != null) {
                surface.release();
            }
            surface = new Surface(surfaceTexture);
            mediaPlayer.setSurface(surface);
        }
    }

    /**
     *  解绑音频焦点管理
     */
    public void unbindAudioFocus(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    /**
     *  绑定音频焦点管理
     * @param context Context
     */
    public void bindAudioFocus(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
            mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    /**
     * 设置音频焦点监听
     */
    public void setOnAudioFocusChangeListener(AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener) {
        mOnAudioFocusChangeListener = onAudioFocusChangeListener;
    }

    /**
     *  解绑重力感应横竖屏管理
     */
    public void unbindOrientationManager() {
        mOrientationEventManager.orientationDisable();
    }

    /**
     *  绑定重力感应横竖屏管理
     */
    public void bindOrientationManager(Context context) {
        mOrientationEventManager.orientationDisable();
        mOrientationEventManager.orientationEnable(context, mOnOrientationChangeListener);
    }

    /**
     *  设置重力感应横竖屏管理
     */
    public void setOnOrientationChangeListener(OrientationEventManager.OnOrientationChangeListener orientationChangeListener) {
        mOnOrientationChangeListener = orientationChangeListener;
    }

    public void updateState(PlayerState playerState) {
        Log.i(TAG, "updateState [" + playerState.name() + "] ");
        mPlayerState = playerState;
        switch (mPlayerState) {
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
        VideoView currentFloor = getCurrentVideoView();
        if (currentFloor != null && currentFloor.isCurrentPlaying()) {
            AbsControlPanel controlPanel = currentFloor.getControlPanel();
            if (controlPanel != null) {
                controlPanel.notifyStateChange();//通知当前的控制面板改变布局
            }
        }
    }

    public void onVideoSizeChanged(int width, int height) {
        Log.i(TAG, "onVideoSizeChanged " + " [" + this.hashCode() + "] ");
        if (textureView != null) {
            textureView.setVideoSize(width, height);
        }
    }

    public void setScreenScale(ScaleType scaleType) {
        if (textureView != null) {
            textureView.setScreenScale(scaleType);
        }
    }

    public void initTextureView(Context context) {
        removeTextureView();
        surfaceTexture = null;
        textureView = new ResizeTextureView(context);
        textureView.setSurfaceTextureListener(MediaPlayerManager.instance());
    }

    public void addTextureView(@NonNull VideoView videoView) {
        if (textureView == null) {
            return;
        }
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ");
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        videoView.getTextureViewContainer().addView(textureView, layoutParams);
    }

    public void removeTextureView() {
        if (textureView != null && textureView.getParent() != null) {
            ((ViewGroup) textureView.getParent()).removeView(textureView);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        if (getCurrentVideoView() == null) return;
        Log.i(TAG, "onSurfaceTextureAvailable [" + "] ");
        if (this.surfaceTexture == null) {
            this.surfaceTexture = surfaceTexture;
            prepare();
        } else {
            textureView.setSurfaceTexture(this.surfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.i(TAG, "onSurfaceTextureSizeChanged [" + "] ");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        Log.i(TAG, "onSurfaceTextureDestroyed [" + "] ");
        return this.surfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    public void setMediaPlayer(AbsMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public AbsMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * 拦截返回键
     */
    public boolean backPress() {
        Log.i(TAG, "backPress");
        try {
            VideoView currentVideoView = getCurrentVideoView();
            if (currentVideoView != null && currentVideoView.getWindowType() == VideoView.WindowType.FULLSCREEN) {//退出全屏
                currentVideoView.exitFullscreen();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void startProgressTimer() {
        Log.i(TAG, "startProgressTimer: " + " [" + this.hashCode() + "] ");
        cancelProgressTimer();
        mProgressTimer = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        mProgressTimer.schedule(mProgressTimerTask, 0, 300);
    }

    public void cancelProgressTimer() {
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
        }
        if (mProgressTimer != null) {
            mProgressTimer.cancel();
        }
    }

    public long getCurrentPositionWhenPlaying() {
        long position = 0;
        if (mPlayerState == PlayerState.PLAYING || mPlayerState == PlayerState.PAUSED) {
            try {
                position = mediaPlayer.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return position;
    }

    /**
     * 获得 MediaPlayer 绑定的 VideoView
     *
     * @return VideoView
     */
    public VideoView getCurrentVideoView() {
        if (textureView == null) return null;
        ViewParent surfaceContainer = textureView.getParent();

        if (surfaceContainer == null) return null;
        ViewParent parent = surfaceContainer.getParent();

        if (parent != null && parent instanceof VideoView) {
            return (VideoView) parent;
        }
        return null;
    }

    public AbsControlPanel getCurrentControlPanel() {
        VideoView currentVideoView = getCurrentVideoView();
        if (currentVideoView == null) return null;
        return currentVideoView.getControlPanel();
    }

    public Object getCurrentData() {
        return mCurrentData;
    }

    public void setCurrentData(Object mCurrentData) {
        this.mCurrentData = mCurrentData;
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
            if (mPlayerState == PlayerState.PLAYING || mPlayerState == PlayerState.PAUSED) {
                long position = getCurrentPositionWhenPlaying();
                long duration = getDuration();
                int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
                VideoView currentVideoView = getCurrentVideoView();
                if (currentVideoView == null) return;
                AbsControlPanel controlPanel = currentVideoView.getControlPanel();
                if (controlPanel != null) {
                    controlPanel.onProgressUpdate(progress, position, duration);
                }
            }
        }
    }
}
