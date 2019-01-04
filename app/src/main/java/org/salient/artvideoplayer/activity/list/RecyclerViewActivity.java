package org.salient.artvideoplayer.activity.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.OnWindowDetachedListener;
import org.salient.artplayer.VideoView;
import org.salient.artvideoplayer.BaseActivity;
import org.salient.artvideoplayer.DensityUtil;
import org.salient.artvideoplayer.R;
import org.salient.artvideoplayer.adapter.RecyclerViewAdapter;
import org.salient.artvideoplayer.bean.VideoBean;
import org.salient.artplayer.ui.ControlPanel;

/**
 * Created by Mai on 2018/7/17
 * *
 * Description:
 * *
 */
public class RecyclerViewActivity extends BaseActivity {

    private RadioGroup rgLayoutManager;
    private RadioGroup rgDetachAction;
    private RecyclerView recycler_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        rgLayoutManager = findViewById(R.id.rgLayoutManager);
        rgDetachAction = findViewById(R.id.rgDetachAction);
        recycler_view = findViewById(R.id.recycler_view);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                //startActivity(new Intent(this, RecyclerViewActivity.class));
                RecyclerView.LayoutManager layoutManager = null;
                switch (rgLayoutManager.getCheckedRadioButtonId()) {
                    case R.id.linearLayoutManager:
                        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        break;
                    case R.id.gridLayoutManager:
                        layoutManager = new GridLayoutManager(this, 2);
                        break;
                    case R.id.staggeredGridLayoutManager:
                        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        break;
                }
                recycler_view.setLayoutManager(layoutManager);

                RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(DensityUtil.getWindowWidth(this));
                recycler_view.setAdapter(recyclerViewAdapter);

                switch (rgDetachAction.getCheckedRadioButtonId()) {
                    case R.id.rbNothing:
                        recyclerViewAdapter.setDetachAction(null);
                        break;
                    case R.id.rbPause:
                        recyclerViewAdapter.setDetachAction(new OnWindowDetachedListener() {
                            @Override
                            public void onDetached(VideoView videoView) {
                                videoView.pause();
                            }
                        });
                        break;
                    case R.id.rbStop:
                        recyclerViewAdapter.setDetachAction(new OnWindowDetachedListener() {
                            @Override
                            public void onDetached(VideoView videoView) {
                                MediaPlayerManager.instance().releasePlayerAndView(RecyclerViewActivity.this);
                            }
                        });
                        break;
                    case R.id.rbMinify:
                        //Specify the Detach Action which would be called when the VideoView has been detached from its window.
                        recyclerViewAdapter.setDetachAction(new OnWindowDetachedListener() {
                            @Override
                            public void onDetached(VideoView videoView) {
                                //开启小窗
                                VideoView tinyVideoView = new VideoView(videoView.getContext());
                                //set url and data
                                tinyVideoView.setUp(videoView.getDataSourceObject(), VideoView.WindowType.TINY, videoView.getData());
                                //set control panel
                                ControlPanel controlPanel = new ControlPanel(videoView.getContext());
                                tinyVideoView.setControlPanel(controlPanel);
                                //set cover
                                ImageView coverView = controlPanel.findViewById(R.id.video_cover);
                                Glide.with(controlPanel.getContext()).load(((VideoBean) videoView.getData()).getImage()).into(coverView);
                                //set parent
                                tinyVideoView.setParentVideoView(videoView);
                                //set LayoutParams
                                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(16 * 45, 9 * 45);
                                layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                                layoutParams.setMargins(0, 0, 30, 100);
                                //start tiny window
                                tinyVideoView.startTinyWindow(layoutParams);
                            }
                        });
                        break;
                }

                recyclerViewAdapter.setList(getAllComing());
                recyclerViewAdapter.notifyDataSetChanged();

                recycler_view.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (MediaPlayerManager.instance().backPress()) {
            return;
        }
        if (recycler_view.getVisibility() == View.VISIBLE) {
            MediaPlayerManager.instance().releasePlayerAndView(this);
            recycler_view.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();

    }

    /**
     * 实现重力感应则在对应生命周期下，增加以下实现方法
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
