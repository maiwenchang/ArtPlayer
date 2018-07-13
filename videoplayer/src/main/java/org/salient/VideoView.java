package org.salient;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
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
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;

/**
 * > Created by Mai on 2018/7/10
 * *
 * > Description: 视频播放视图
 * *
 */
public class VideoView extends FrameLayout {

    public static final String URL_KEY_DEFAULT = "url_default_key";//当播放的地址只有一个的时候的key
    private final String TAG = VideoView.class.getSimpleName();
    private final int ROOT_VIEW_POSITION = -1;
    private final int CONTROL_PANEL_POSITION = 1;
    public WindowType mWindowType = WindowType.NORMAL;
    public Object[] dataSourceObjects;
    public int currentUrlMapIndex = 0;
    public int widthRatio = 0;
    public int heightRatio = 0;
    // settable by the client
    private Uri mUri;
    private String mCoverUrl;
    private Object[] mHeaders = null; // 标题
    private int mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private int positionInList = -1;
    private int mScreenWidth;
    private int mScreenHeight;
    private AudioManager mAudioManager;
    private AbsControlPanel mControlPanel;
    private FrameLayout textureViewContainer;
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

        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        try {
            if (isCurrentPlay()) {
                mScreenOrientation = ((AppCompatActivity) context).getRequestedOrientation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 是否当前播放地址
     *
     * @return boolean
     */
    public boolean isCurrentPlay() {
        return VideoLayerManager.isCurrentFloor(this)
                && Utils.dataSourceObjectsContainsUri(dataSourceObjects, MediaPlayerManager.instance().getCurrentDataSource());//不仅正在播放的url不能一样，并且各个清晰度也不能一样
    }

    public void setUp(String url, WindowType windowType, Object... headers) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put(URL_KEY_DEFAULT, url);
        Object[] dataSourceObjects = new Object[1];
        dataSourceObjects[0] = map;
        setUp(dataSourceObjects, 0, windowType, headers);
    }

    public void setUp(Object[] dataSourceObjects, int defaultUrlMapIndex, WindowType windowType, Object... headers) {

        if (this.dataSourceObjects != null && dataSourceObjects != null) {//与当前正在播放的Url相同
            Object newDataSource = Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex);

            Object oldDataSource = Utils.getCurrentFromDataSource(this.dataSourceObjects, currentUrlMapIndex);

            if (newDataSource != null && oldDataSource != null && !newDataSource.equals(oldDataSource)) {
                return;
            }
        }

        boolean isCurrentFloor = VideoLayerManager.isCurrentFloor(this);

        if (isCurrentFloor && Utils.dataSourceObjectsContainsUri(dataSourceObjects, MediaPlayerManager.instance().getCurrentDataSource())) {
            long position = 0;
            try {
                position = MediaPlayerManager.instance().getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if (position != 0) {
                //Utils.saveProgress(getContext(), MediaPlayerManager.instance().getCurrentDataSource(), position);
            }
            MediaPlayerManager.instance().releaseMediaPlayer();
        } else if (isCurrentFloor && !Utils.dataSourceObjectsContainsUri(dataSourceObjects, MediaPlayerManager.instance().getCurrentDataSource())) {

            startWindowTiny();

        } else if (!isCurrentFloor && Utils.dataSourceObjectsContainsUri(dataSourceObjects, MediaPlayerManager.instance().getCurrentDataSource())) {

            if (VideoLayerManager.getCurrentFloor() != null &&
                    VideoLayerManager.getCurrentFloor().mWindowType == WindowType.TINY) {
                //需要退出小窗退到我这里，我这里是第一层级
                //tmp_test_back = true;
            }

        } else if (!isCurrentFloor && !Utils.dataSourceObjectsContainsUri(dataSourceObjects, MediaPlayerManager.instance().getCurrentDataSource())) {

        }

        this.dataSourceObjects = dataSourceObjects;
        this.currentUrlMapIndex = defaultUrlMapIndex;
        this.mWindowType = windowType;
        this.mHeaders = headers;
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

        if (dataSourceObjects == null || Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex) == null) {
            Toast.makeText(getContext(), "No Url", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isCurrentPlay()) {
            switch (MediaPlayerManager.instance().getCurrentState()) {
                case IDLE://从初始状态开始播放
                    startVideo();
                    break;
                case PLAYBACK_COMPLETED: // 重播:
                    //startVideo();
                    MediaPlayerManager.instance().start();
                    break;
                case PAUSED://从暂停状态恢复播放
                    MediaPlayerManager.instance().start();
                    break;
            }
        } else {
            startVideo();
        }

    }

    /**
     * 暂停
     */
    public void pause() {
        if (isCurrentPlay()) {
            if (MediaPlayerManager.instance().getCurrentState() == MediaPlayerManager.PlayerState.PLAYING) {
                Log.d(TAG, "pause [" + this.hashCode() + "] ");
                MediaPlayerManager.instance().pause();
            }
        }
    }

    public void startVideo() {
        VideoLayerManager.completeAll();
        Log.d(TAG, "startVideo [" + this.hashCode() + "] ");

        initTextureView();
        addTextureView();

        MediaPlayerManager.instance().setOnAudioFocusChangeListener(getContext());

        Utils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MediaPlayerManager.instance().setDataSource(dataSourceObjects);
        MediaPlayerManager.instance().setCurrentDataSource(Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
        MediaPlayerManager.instance().positionInList = positionInList;

        VideoLayerManager.setFirstFloor(this);

    }

    /**
     * 进入全屏模式
     * <p>
     * 注意：这里会重新创建一个VideoView实例，
     * 动态添加到{@link android.view.Window#ID_ANDROID_CONTENT }所指的ContentView中
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
            fullScreenVideoView.setId(R.id.salient_video_fullscreen_id);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            vp.addView(fullScreenVideoView, lp);
            fullScreenVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            fullScreenVideoView.setUp(dataSourceObjects, currentUrlMapIndex, WindowType.FULLSCREEN, mHeaders);

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

            VideoLayerManager.setSecondFloor(fullScreenVideoView);

            Utils.setRequestedOrientation(getContext(), screenOrientation);

            MediaPlayerManager.instance().mClickFullScreenTime = System.currentTimeMillis();

            MediaPlayerManager.instance().updateState(MediaPlayerManager.instance().getCurrentState());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //退出全屏或小窗
    public void closeWindowFullScreen() {
        Log.i(TAG, "closeWindowFullScreen " + " [" + this.hashCode() + "] ");
        currentUrlMapIndex = VideoLayerManager.getSecondFloor().currentUrlMapIndex;
        MediaPlayerManager.instance().clearFloatScreen(getContext());
        addTextureView();
        MediaPlayerManager.instance().updateState(MediaPlayerManager.instance().getCurrentState());
        if (getControlPanel() != null) {
            getControlPanel().onExitFullScreen();
        }
    }

    public void addTextureView() {
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ");
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        textureViewContainer.addView(MediaPlayerManager.instance().textureView, layoutParams);
    }

    public void initTextureView() {
        removeTextureView();
        MediaPlayerManager.instance().textureView = new ResizeTextureView(getContext());
        MediaPlayerManager.instance().textureView.setSurfaceTextureListener(MediaPlayerManager.instance());
    }

    public void removeTextureView() {
        MediaPlayerManager.instance().surfaceTexture = null;
        if (MediaPlayerManager.instance().textureView != null && MediaPlayerManager.instance().textureView.getParent() != null) {
            ((ViewGroup) MediaPlayerManager.instance().textureView.getParent()).removeView(MediaPlayerManager.instance().textureView);
        }
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

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setCoverUrl(String mCoverUrl) {
        this.mCoverUrl = mCoverUrl;
    }

    public void completeVideo() {
        Log.i(TAG, "completeVideo " + " [" + this.hashCode() + "] ");
        MediaPlayerManager mediaPlayerManager = MediaPlayerManager.instance();
        if (MediaPlayerManager.instance().getCurrentState() == MediaPlayerManager.PlayerState.PLAYING
                || MediaPlayerManager.instance().getCurrentState() == MediaPlayerManager.PlayerState.PAUSED) {//保存进度
            long position = mediaPlayerManager.getCurrentPositionWhenPlaying();
            //Utils.saveProgress(getContext(), Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), position);
        }
        MediaPlayerManager.instance().updateState(MediaPlayerManager.PlayerState.IDLE);
        mediaPlayerManager.cancelProgressTimer();
        textureViewContainer.removeView(MediaPlayerManager.instance().textureView);
        mediaPlayerManager.currentVideoWidth = 0;
        mediaPlayerManager.currentVideoHeight = 0;

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

    public enum WindowType {
        NORMAL,
        LIST,
        FULLSCREEN,
        TINY
    }
}
