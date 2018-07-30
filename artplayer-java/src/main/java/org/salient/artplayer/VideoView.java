package org.salient.artplayer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.util.Map;

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
    public int widthRatio = 0;
    public int heightRatio = 0;
    private WindowType mWindowType = WindowType.NORMAL;
    private FrameLayout textureViewContainer;
    private int mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    // settable by the client
    private Object mData = null;//video data like id, title, cover picture...

    private Object dataSourceObject;// video dataSource (contains url) would be posted to MediaPlayer.

    protected Map<String, String> mHeaders;

    private AbsControlPanel mControlPanel;

    private OnWindowDetachedListener mDetachedListener;

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

        textureViewContainer = new FrameLayout(getContext());
        textureViewContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(textureViewContainer, ROOT_VIEW_POSITION, params);

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
        VideoView currentVideoView = MediaPlayerManager.instance().getCurrentVideoView();
        if (mSmartMode && isCurrentPlaying() || currentVideoView == null || currentVideoView.getWindowType() == WindowType.TINY) {
            autoMatch();
        }
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public void setHeaders(Map<String, String> mHeaders) {
        this.mHeaders = mHeaders;
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
        VideoView currentVideoView = MediaPlayerManager.instance().getCurrentVideoView();
        if (isCurrentPlaying()) {
            //quit tiny window
            if (currentVideoView != null && currentVideoView.getWindowType() == WindowType.TINY) {
                //play at this video view
                MediaPlayerManager.instance().playAt(this);
                //自动退出小窗
                MediaPlayerManager.instance().clearTinyLayout(getContext());
                if (mControlPanel != null) {
                    mControlPanel.onExitSecondScreen();
                    mControlPanel.notifyStateChange();
                }
            } else if (currentVideoView != null && currentVideoView.getWindowType() == WindowType.FULLSCREEN) {
                if (mControlPanel != null) {
                    //mControlPanel.onStateIdle();
                }
            } else {
                //play at this video view
                MediaPlayerManager.instance().playAt(this);
                if (mControlPanel != null) {
                    mControlPanel.notifyStateChange();
                }
            }
        } else if (currentVideoView == this) {
            MediaPlayerManager.instance().removeTextureView();
            if (mControlPanel != null) {
                mControlPanel.onStateIdle();
            }
        } else {
            if (mControlPanel != null) {
                mControlPanel.onStateIdle();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mSmartMode) {
            autoMatch();
        }
    }

    public Object getData() {
        return mData;
    }

    public void setData(Object data) {
        mData = data;
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
            //textureViewContainer.setOnClickListener(mControlPanel);
            //textureViewContainer.setOnTouchListener(mControlPanel);
        }
        this.mControlPanel = mControlPanel;
        View child = getChildAt(CONTROL_PANEL_POSITION);
        if (child != null) {
            removeViewAt(CONTROL_PANEL_POSITION);
        }
        if (this.mControlPanel != null) {
            //getTextureViewContainer().setOnClickListener(this.mControlPanel);
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

    public OnWindowDetachedListener getDetachedListener() {
        return mDetachedListener;
    }

    public void setOnWindowDetachedListener(OnWindowDetachedListener mDetachedListener) {
        this.mDetachedListener = mDetachedListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDetachedListener != null) {
            mDetachedListener.onDetached(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VideoView && mComparator.compare(this);
    }

    public Comparator getComparator() {
        return mComparator;
    }

    public void setComparator(@NonNull Comparator mComparator) {
        this.mComparator = mComparator;
    }

    /**
     * 判断VideoView 与 正在播放的多媒体资源是否匹配;
     * 匹配规则可以通过{@link VideoView#setComparator(Comparator)} 设置;
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
            VideoView currentVideoView = MediaPlayerManager.instance().getCurrentVideoView();
            Object dataSource = MediaPlayerManager.instance().getDataSource();

            if (dataSource != null && videoView != null) {
                boolean b = dataSource == videoView.getDataSourceObject();
                Log.d(TAG, "Comparator : " + b + "");
                return dataSource == videoView.getDataSourceObject();
            }

            return false;
        }
    };

    public enum WindowType {
        NORMAL,
        FULLSCREEN,
        TINY
    }
}
