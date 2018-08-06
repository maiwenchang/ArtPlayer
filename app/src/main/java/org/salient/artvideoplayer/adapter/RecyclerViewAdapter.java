package org.salient.artvideoplayer.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.salient.artplayer.Comparator;
import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.OnWindowDetachedListener;
import org.salient.artplayer.Utils;
import org.salient.artplayer.VideoView;
import org.salient.artvideoplayer.DensityUtil;
import org.salient.artvideoplayer.R;
import org.salient.artvideoplayer.bean.VideoBean;
import org.salient.controlpanel.ControlPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * > Created by Mai on 2018/7/17
 * *
 * > Description:
 * *
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.VideoViewHolder> {

    private final int mScreenWidth;
    private List<VideoBean> mList = new ArrayList<>();

    private boolean isStaggeredGridLayoutManager = false;

    private OnItemClickListener mOnItemClickListener = null;
    private Comparator mComparator = new Comparator() {
        @Override
        public boolean compare(VideoView videoView) {
            try {
                Object currentData = MediaPlayerManager.instance().getCurrentData();
                //By comparing the position on the list to distinguish whether the same video
                if (currentData != null && videoView != null) {
                    Object data = videoView.getData();
                    return data != null
                            && currentData instanceof VideoBean
                            && data instanceof VideoBean
                            && ((VideoBean) currentData).getListPosition() == ((VideoBean) data).getListPosition();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };

    public RecyclerViewAdapter(int screenWidth) {
        mScreenWidth = screenWidth;
    }

    public void setList(List<VideoBean> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_view, parent, false);
        final VideoViewHolder videoViewHolder = new VideoViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    //注意这里使用getTag方法获取position
                    mOnItemClickListener.onItemClick(v, videoViewHolder.getAdapterPosition());
                }
            }
        });
        return videoViewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView.getLayoutManager() != null && recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            isStaggeredGridLayoutManager = true;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Log.d("testt", "onBindViewHolder position:" + position + "hashCode : " + holder.videoView.hashCode());
        VideoBean videoBean = mList.get(holder.getAdapterPosition());
        videoBean.setListPosition(position);
        holder.videoView.setUp(videoBean.getUrl(), VideoView.WindowType.LIST, videoBean);

        // 瀑布流时，手动更改高度，使不同位置的高度有所不同
        if (isStaggeredGridLayoutManager) {
//            ViewGroup.LayoutParams layoutParams = holder.videoView.getLayoutParams();
//            //layoutParams.width = 16 * 40;
//            //layoutParams.height = (int) (9 * 40 + Math.sin((position + 1) * Math.PI / 2) * 5);
//            Activity activity = Utils.scanForActivity(holder.videoView.getContext());
//            DisplayMetrics dm = null;
//
//            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//            layoutParams.height = (int) (DensityUtil.getInstance(videoBean.get).widthPixels / 2 + Math.sin((position + 1) * Math.PI / 2) * 5);
//
//            holder.videoView.setLayoutParams(layoutParams);
        }

        //setCover
        ImageView coverView = ((ControlPanel) holder.videoView.getControlPanel()).getCoverView();
        Glide.with(holder.videoView.getContext()).load(videoBean.getImage()).into(coverView);

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull VideoViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull VideoViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null) {
            this.mOnItemClickListener = onItemClickListener;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        VideoView videoView;

        VideoViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            ControlPanel controlPanel = new ControlPanel(videoView.getContext());
            videoView.setControlPanel(controlPanel);
            //optional:
            videoView.setComparator(mComparator);
            //optional: Specify the Detach Action which would be called when the VideoView has been detached from its window.
            videoView.setOnWindowDetachedListener(mOnWindowDetachedListener);

//            videoView.setOnWindowDetachedListener(new OnWindowDetachedListener() {
//                @Override
//                public void onDetached(VideoView videoView) {
//                    if (videoView.isCurrentPlaying() && videoView == MediaPlayerManager.instance().getCurrentVideoView()) {
//                        //开启小窗
//                        VideoView tinyVideoView = new VideoView(videoView.getContext());
//                        //set url and data
//                        tinyVideoView.setUp(videoView.getDataSourceObject(), VideoView.WindowType.TINY, videoView.getData());
//                        //set control panel
//                        ControlPanel controlPanel = new ControlPanel(videoView.getContext());
//                        tinyVideoView.setControlPanel(controlPanel);
//                        //set cover
//                        ImageView coverView = controlPanel.getCoverView();
//                        Glide.with(controlPanel.getContext()).load(((VideoBean) videoView.getData()).getImage()).into(coverView);
//                        //set parent
//                        tinyVideoView.setParentVideoView(videoView);
//                        //set LayoutParams
//                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(16 * 45, 9 * 45);
//                        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
//                        layoutParams.setMargins(0, 0, 30, 100);
//                        //start tiny window
//                        tinyVideoView.startTinyWindow(layoutParams);
//                    }
//                }
//            });
        }
    }

    private OnWindowDetachedListener mOnWindowDetachedListener = null;

    public void setDetachAction(OnWindowDetachedListener onWindowDetachedListener) {
        mOnWindowDetachedListener = onWindowDetachedListener;
    }

}
