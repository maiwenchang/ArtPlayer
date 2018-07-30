package org.salient.artplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.NonNull;
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
 * > Created by Mai on 2018/7/10
 * *
 * > Description: 视频播放器管理类
 * *
 */
public class MediaPlayerManager implements TextureView.SurfaceTextureListener {

    private final String TAG = getClass().getSimpleName();
    private final int FULL_SCREEN_NORMAL_DELAY = 300;
    //surface
    private ResizeTextureView textureView;
    private SurfaceTexture surfaceTexture;
    private Surface surface;

    private PlayerState mPlayerState = PlayerState.IDLE;
    private Object mCurrentData;
    private long mClickFullScreenTime = 0;
    private Timer mProgressTimer;
    private ProgressTimerTask mProgressTimerTask;

    // settable by client
    private boolean isMute = false;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;
    private AbsMediaPlayer mediaPlayer;
    //private int videoRotation = 0;

    private MediaPlayerManager() {
        if (mediaPlayer == null) {
            mediaPlayer = new SystemMediaPlayer();
            onAudioFocusChangeListener = new AudioFocusManager();
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
        return instance().mediaPlayer.getDataSource();
    }

    private void setDataSource(Object dataSource, Map<String, String> headers) {
        if (dataSource == null) return;
        if (dataSource instanceof AssetFileDescriptor) {
            instance().mediaPlayer.setDataSource((AssetFileDescriptor) dataSource);
        } else {
            instance().mediaPlayer.setDataSource(dataSource.toString(), headers);
        }
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

    public void play(@NonNull VideoView videoView) {
        Log.d(TAG, "play [" + videoView.hashCode() + "] ");
        //check data source
        if (videoView.getDataSourceObject() == null) {
            return;
        }
        //get context
        Context context = videoView.getContext();
        //clear videoView open before
        VideoView currentVideoView = getCurrentVideoView();
        if (currentVideoView != null && currentVideoView != videoView) {
            if (videoView.getWindowType() != VideoView.WindowType.TINY) {
                clearTinyLayout(context);
            } else if (videoView.getWindowType() != VideoView.WindowType.FULLSCREEN) {
                clearFullscreenLayout(context);
            }
        }
        // reset state to IDLE
        updateState(MediaPlayerManager.PlayerState.IDLE);
        //pass data to MediaPlayer

        setDataSource(videoView.getDataSourceObject(),videoView.getHeaders());

        mCurrentData = videoView.getData();
        //keep screen on
        Utils.scanForActivity(context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //bind {@link AudioManager#OnAudioFocusChangeListener}
        bindAudioFocus(context);
        //init TextureView, we will prepare and start the player when surfaceTextureAvailable.
        initTextureView(context);
        addTextureView(videoView);
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
        return mPlayerState == PlayerState.PLAYING && instance().mediaPlayer.isPlaying();
    }

    public void releasePlayerAndView(Context context) {
        if ((System.currentTimeMillis() - mClickFullScreenTime) > FULL_SCREEN_NORMAL_DELAY) {
            Log.d(TAG, "release");
            if (context != null) {
                clearFullscreenLayout(context);
                clearTinyLayout(context);
                Utils.scanForActivity(context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                abandonAudioFocus(context);
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
        mediaPlayer.release();
        updateState(MediaPlayerManager.PlayerState.IDLE);
    }

    /**
     * go into prepare and start
     */
    private void prepare() {
        releaseMediaPlayer();//release first
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

    public void bindAudioFocus(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    public void setOnAudioFocusChangeListener(AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener) {
        this.onAudioFocusChangeListener = onAudioFocusChangeListener;
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
//            if (videoRotation != 0) {
//                textureView.setRotation(videoRotation);
//            }
            textureView.setVideoSize(width, height);
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
        //if (VideoLayerManager.instance().getCurrentFloor() == null) return;
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
        //Log.i(TAG, "onSurfaceTextureUpdated [" + VideoLayerManager.instance().getCurrentFloor().hashCode() + "] ");

    }

    public void setMediaPlayer(AbsMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public boolean isMute() {
        return isMute;
    }

    /**
     * 设置静音
     *
     * @param mute boolean
     */
    public void setMute(boolean mute) {
        this.isMute = mute;
        instance().mediaPlayer.mute(mute);
    }

    /**
     * 进入全屏模式
     * <p>
     * 注意：这里把一个VideoView动态添加到{@link Window#ID_ANDROID_CONTENT }所指的View中
     */
    public void startFullscreen(@NonNull VideoView videoView, int screenOrientation) {
        if (videoView.getParent() != null) {
            throw new IllegalStateException("The specified VideoView already has a parent. " +
                    "You must call removeView() on the VideoView's parent first.");
        }
        Context context = videoView.getContext();
        videoView.setWindowType(VideoView.WindowType.FULLSCREEN);
        Utils.hideSupportActionBar(context);
        // add to window
        ViewGroup vp = (Utils.scanForActivity(context)).findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.salient_video_fullscreen_id);
        if (old != null) {
            vp.removeView(old);
        }
        videoView.setId(R.id.salient_video_fullscreen_id);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        vp.addView(videoView, lp);
        //add TextureView
        removeTextureView();
        addTextureView(videoView);
        //update ControlPanel State
        AbsControlPanel controlPanel = videoView.getControlPanel();
        if (controlPanel != null) {
            controlPanel.onEnterSecondScreen();
        }
        //update Parent ControlPanel State
        VideoView parentVideoView = videoView.getParentVideoView();
        if (parentVideoView != null) {
            AbsControlPanel parentControlPanel = parentVideoView.getControlPanel();
            if (parentControlPanel != null) {
                parentControlPanel.onEnterSecondScreen();
            }
        }
        //Rotate window an enter fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        Utils.setRequestedOrientation(context, screenOrientation);

        mClickFullScreenTime = System.currentTimeMillis();

        updateState(getPlayerState());

    }

    /**
     * 进入小屏模式
     * <p>
     * 注意：这里把一个VideoView动态添加到{@link Window#ID_ANDROID_CONTENT }所指的View中
     */
    public void startTinyWindow(@NonNull VideoView videoView) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(16 * 40, 9 * 40);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        layoutParams.setMargins(0, 0, 30, 100);
        startTinyWindow(videoView, layoutParams);
    }

    /**
     * 进入小屏模式
     * <p>
     * 注意：这里把一个VideoView动态添加到{@link Window#ID_ANDROID_CONTENT }所指的View中
     */
    public void startTinyWindow(@NonNull VideoView videoView, FrameLayout.LayoutParams lp) {
        Log.i(TAG, "startWindowTiny " + " [" + this.hashCode() + "] ");
        if (videoView.getParent() != null) {
            throw new IllegalStateException("The specified VideoView already has a parent. " +
                    "You must call removeView() on the VideoView's parent first.");
        }
        Context context = videoView.getContext();
        videoView.setWindowType(VideoView.WindowType.TINY);

        // add to window
        ViewGroup vp = (Utils.scanForActivity(context)).findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.salient_video_tiny_id);
        if (old != null) {
            vp.removeView(old);
        }
        videoView.setId(R.id.salient_video_tiny_id);
        if (lp != null) {
            vp.addView(videoView, lp);
        } else {
            vp.addView(videoView);
        }
        //add TextureView
        removeTextureView();
        addTextureView(videoView);
        //update ControlPanel State
        AbsControlPanel controlPanel = videoView.getControlPanel();
        if (controlPanel != null) {
            controlPanel.onEnterSecondScreen();
        }
        //update Parent ControlPanel State
        VideoView parentVideoView = videoView.getParentVideoView();
        if (parentVideoView != null) {
            AbsControlPanel parentControlPanel = parentVideoView.getControlPanel();
            if (parentControlPanel != null) {
                parentControlPanel.onEnterSecondScreen();
            }
        }

        updateState(getPlayerState());
    }

    /**
     * 拦截返回键
     */
    public boolean backPress(Context context) {
        Log.i(TAG, "backPress");
        if ((System.currentTimeMillis() - mClickFullScreenTime) < FULL_SCREEN_NORMAL_DELAY) {
            return false;
        }
        try {
            VideoView currentVideoView = getCurrentVideoView();
            if (currentVideoView != null && currentVideoView.getWindowType() == VideoView.WindowType.FULLSCREEN) {//退出全屏
                Utils.setRequestedOrientation(currentVideoView.getContext(), currentVideoView.getScreenOrientation());
                clearFullscreenLayout(currentVideoView.getContext());
                Utils.showSupportActionBar(context);
                VideoView parent = currentVideoView.getParentVideoView();
                if (parent != null) {//在常规窗口继续播放
                    playAt(parent);
                    AbsControlPanel controlPanel = parent.getControlPanel();
                    if (controlPanel != null) {
                        controlPanel.notifyStateChange();
                        controlPanel.onExitSecondScreen();
                    }
                } else {//直接开启的全屏，只有一层，没有常规窗口
                    releasePlayerAndView(currentVideoView.getContext());
                }
                mClickFullScreenTime = System.currentTimeMillis();
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
        if (mProgressTimer != null) {
            mProgressTimer.cancel();
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
        }
    }

    public long getCurrentPositionWhenPlaying() {
        long position = 0;
        if (mPlayerState == PlayerState.PLAYING || mPlayerState == PlayerState.PAUSED) {
            try {
                position = instance().mediaPlayer.getCurrentPosition();
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
