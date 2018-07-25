package org.salient;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Constructor;

/**
 * > Created by Mai on 2018/7/10
 * *
 * > Description: 视频播放视图
 * *
 */
public class VideoView extends FrameLayout {

    private final String TAG = VideoView.class.getSimpleName();
    private final int ROOT_VIEW_POSITION = -1;
    private final int CONTROL_PANEL_POSITION = 1;
    private WindowType mWindowType = WindowType.NORMAL;
    public int widthRatio = 0;
    public int heightRatio = 0;
    private FrameLayout textureViewContainer;
    private int mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    // settable by the client
    private Object mData = null;//video data like id, title, cover picture...

    private Object dataSourceObject;// video dataSource (contains url) would be posted to MediaPlayer.

    private AbsControlPanel mControlPanel;

    private DetachAction mDetachAction = DetachAction.NOTHING;

    private boolean mSmartMode = true;

    private VideoView mParentVideoView = null;

    public VideoView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        View view = View.inflate(context, R.layout.salient_layout_video_view, null);

        addView(view, ROOT_VIEW_POSITION);

        textureViewContainer = findViewById(R.id.surface_container);

        try {
            mScreenOrientation = ((AppCompatActivity) context).getRequestedOrientation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUp(String url) {
        setUp(url, WindowType.NORMAL, null);
    }

    public void setUp(String url, Object data) {
        setUp(url, WindowType.NORMAL, data);
    }

    public void setUp(String url, WindowType windowType) {
        setUp(url, windowType, null);
    }

    public void setUp(Object dataSourceObjects, WindowType windowType, Object data) {
        this.dataSourceObject = dataSourceObjects;
        this.mWindowType = windowType;
        this.mData = data;
        if (mSmartMode) {
            autoMatch();
        } else if (mControlPanel != null) {
            mControlPanel.onStateIdle();
        }
    }

    public boolean isSmartMode() {
        return mSmartMode;
    }

    public void setSmartMode(boolean mSmartMode) {
        this.mSmartMode = mSmartMode;
    }

    private void autoMatch() {
        if (mWindowType != WindowType.NORMAL) {
            return;
        }
        VideoView currentPlaying = MediaPlayerManager.instance().getCurrentVideoView();
        if (isCurrentPlaying()) {

            MediaPlayerManager.instance().playAt(this);

            if (mControlPanel != null) {
                mControlPanel.notifyStateChange();
            }

        } else if (currentPlaying == this) {
            //removeTextureView();
            if (mControlPanel != null) {
                mControlPanel.onStateIdle();
            }
        } else {
            if (currentPlaying != null && currentPlaying.getWindowType() == WindowType.TINY && currentPlaying.getParentVideoView() == this) {
                //自动退出小窗

            }
        }
    }

    public void setData(Object data) {
        mData = data;
    }

    public Object getData() {
        return mData;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWindowType == WindowType.FULLSCREEN || mWindowType == WindowType.TINY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (widthRatio != 0 && heightRatio != 0) {
            int specWidth = MeasureSpec.getSize(widthMeasureSpec);
            int specHeight = (int) ((specWidth * (float) heightRatio) / widthRatio);
            setMeasuredDimension(specWidth, specHeight);

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(specWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY);
            getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    /**
     * 开启小窗模式
     */
    private void startWindowTiny() {


    }

    public int getScreenOrientation() {
        return mScreenOrientation;
    }

    /**
     * 开始播放
     */
    public void start() {
        Log.d(TAG, "start [" + this.hashCode() + "] ");

        if (dataSourceObject == null) {
            Log.w(TAG, "No Url");
            return;
        }

        if (isCurrentPlaying()) {
            switch (MediaPlayerManager.instance().getPlayerState()) {
                case IDLE://从初始状态开始播放
                case ERROR:
                    MediaPlayerManager.instance().play(this);
                    break;
                case PREPARED:
                case PLAYBACK_COMPLETED: // 重播
                case PAUSED://从暂停状态恢复播放
                    MediaPlayerManager.instance().start();
                    break;
            }
        } else {
            MediaPlayerManager.instance().play(this);
        }

    }

    /**
     * 暂停
     */
    public void pause() {
        if (isCurrentPlaying()) {
            if (MediaPlayerManager.instance().getPlayerState() == MediaPlayerManager.PlayerState.PLAYING) {
                Log.d(TAG, "pause [" + this.hashCode() + "] ");
                MediaPlayerManager.instance().pause();
            }
        }
    }

    public Object getDataSourceObject() {
        return dataSourceObject;
    }

    public void setDataSourceObject(Object dataSourceObject) {
        this.dataSourceObject = dataSourceObject;
    }

    /**
     * 进入全屏模式
     * <p>
     * 注意：这里会重新创建一个VideoView实例，
     * 动态添加到{@link Window#ID_ANDROID_CONTENT }所指的ContentView中
     */
    public void startWindowFullscreen(int screenOrientation) {
        Log.i(TAG, "startWindowFullscreen " + " [" + this.hashCode() + "] ");
        Utils.hideSupportActionBar(getContext());

        ViewGroup vp = (Utils.scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.salient_video_fullscreen_id);
        if (old != null) {
            vp.removeView(old);
        }
        textureViewContainer.removeView(MediaPlayerManager.instance().textureView);

        try {
            VideoView fullScreenVideoView = new VideoView(getContext());
            fullScreenVideoView.setParentVideoView(this);
            fullScreenVideoView.setId(R.id.salient_video_fullscreen_id);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            vp.addView(fullScreenVideoView, lp);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                fullScreenVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            } else {
                fullScreenVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }

            fullScreenVideoView.setUp(dataSourceObject, WindowType.FULLSCREEN, mData);

            fullScreenVideoView.addTextureView();

            AbsControlPanel controlPanel = getControlPanel();
            if (controlPanel != null) {
                Class<? extends AbsControlPanel> cls = controlPanel.getClass();
                //参数类型
                Class<?>[] params = {Context.class};
                //参数值
                Object[] values = {getContext()};
                Constructor<? extends AbsControlPanel> constructor = cls.getDeclaredConstructor(params);
                AbsControlPanel absControlPanel = constructor.newInstance(values);
                fullScreenVideoView.setControlPanel(absControlPanel);
                absControlPanel.onEnterFullScreen();
            }

            //VideoLayerManager.instance().setSecondFloor(fullScreenVideoView);

            Utils.setRequestedOrientation(getContext(), screenOrientation);

            //MediaPlayerManager.instance().mClickFullScreenTime = System.currentTimeMillis();

            MediaPlayerManager.instance().updateState(MediaPlayerManager.instance().getPlayerState());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTextureView() {
        if (MediaPlayerManager.instance().textureView == null) {
            return;
        }
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ");
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        textureViewContainer.addView(MediaPlayerManager.instance().textureView, layoutParams);
    }

    public AbsControlPanel getControlPanel() {
        return mControlPanel;
    }

    /**
     * 设置控制面板
     *
     * @param mControlPanel AbsControlPanel
     */
    public void setControlPanel(AbsControlPanel mControlPanel) {
        if (mControlPanel != null) {
            mControlPanel.setTarget(this);
            ViewParent parent = mControlPanel.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mControlPanel);
            }
            textureViewContainer.setOnClickListener(mControlPanel);
            textureViewContainer.setOnTouchListener(mControlPanel);
        }
        this.mControlPanel = mControlPanel;
        View child = getChildAt(CONTROL_PANEL_POSITION);
        if (child != null) {
            removeViewAt(CONTROL_PANEL_POSITION);
        }
        if (this.mControlPanel != null) {
            getTextureViewContainer().setOnClickListener(this.mControlPanel);
        }
        addView(this.mControlPanel, CONTROL_PANEL_POSITION);
        if (this.mControlPanel != null) {
            this.mControlPanel.onStateIdle();
        }
    }

    public FrameLayout getTextureViewContainer() {
        return textureViewContainer;
    }

    public WindowType getWindowType() {
        return mWindowType;
    }

    public void setWindowType(WindowType mWindowType) {
        this.mWindowType = mWindowType;
    }

    public void completeVideo() {
        Log.i(TAG, "completeVideo " + " [" + this.hashCode() + "] ");
        MediaPlayerManager mediaPlayerManager = MediaPlayerManager.instance();

        if (MediaPlayerManager.instance().getPlayerState() == MediaPlayerManager.PlayerState.PLAYING
                || MediaPlayerManager.instance().getPlayerState() == MediaPlayerManager.PlayerState.PAUSED) {//保存进度
            long position = mediaPlayerManager.getCurrentPositionWhenPlaying();
            //Utils.saveProgress(getContext(), Utils.getCurrentFromDataSource(dataSourceObject, currentUrlMapIndex), position);
        }

        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.IDLE);

        if (mControlPanel != null) {
            mControlPanel.onStateIdle();
        }

        mediaPlayerManager.cancelProgressTimer();

        textureViewContainer.removeView(MediaPlayerManager.instance().textureView);

        mediaPlayerManager.abandonAudioFocus(getContext());

        Utils.scanForActivity(getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        clearFullscreenLayout();

        //Utils.setRequestedOrientation(getContext(), mScreenOrientation);

        if (mediaPlayerManager.surfaceTexture != null) {
            mediaPlayerManager.surfaceTexture.release();
        }

        if (mediaPlayerManager.surface != null) {
            mediaPlayerManager.surface.release();
        }
        mediaPlayerManager.textureView = null;
        mediaPlayerManager.surfaceTexture = null;

    }

    private void clearFullscreenLayout() {
        ViewGroup vp = (Utils.scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
        View oldF = vp.findViewById(R.id.salient_video_fullscreen_id);
        View oldT = vp.findViewById(R.id.salient_video_tiny_id);
        if (oldF != null) {
            vp.removeView(oldF);
        }
        if (oldT != null) {
            vp.removeView(oldT);
        }
        Utils.showSupportActionBar(getContext());
    }

    /**
     * @param detachAction DetachAction
     */
    public void setDetachStrategy(DetachAction detachAction) {
        mDetachAction = detachAction;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isCurrentPlaying()) {
            switch (mDetachAction) {
                case NOTHING:
                    break;
                case PAUSE:
                    MediaPlayerManager.instance().pause();
                    break;
                case STOP:
                    MediaPlayerManager.instance().releasePlayerAndView(getContext());
                    break;
                case MINIFY:
                    //小屏
                    startWindowTiny();
                    break;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VideoView && mComparator.compare(this);
    }

    public void setComparator(@NonNull Comparator mComparator) {
        this.mComparator = mComparator;
    }

    public Comparator getComparator() {
        return mComparator;
    }

    /**
     * 判断VideoView 与 正在播放的多媒体资源是否匹配;
     * 匹配规则可以通过{@link VideoView#setComparator(VideoView.Comparator)} 设置;
     * 默认比较{@link VideoView#dataSourceObject} 和 {@link AbsMediaPlayer#dataSource}
     * See{@link VideoView#mComparator }
     *
     * @return VideoView
     */
    public boolean isCurrentPlaying() {
        return mComparator.compare(this);
    }


    public VideoView getParentVideoView() {
        return mParentVideoView;
    }

    public void setParentVideoView(VideoView mParentVideoView) {
        this.mParentVideoView = mParentVideoView;
    }

    private Comparator mComparator = new Comparator() {
        @Override
        public boolean compare(VideoView videoView) {
            return videoView == MediaPlayerManager.instance().getCurrentVideoView()
                    && videoView.getDataSourceObject() == MediaPlayerManager.instance().getDataSource();
        }
    };

    public enum DetachAction {
        NOTHING,
        PAUSE,
        STOP,
        MINIFY
    }

    public enum WindowType {
        NORMAL,
        FULLSCREEN,
        TINY
    }

    public interface Comparator {
        boolean compare(VideoView obj);
    }
}
