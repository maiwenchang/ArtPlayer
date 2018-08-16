package org.salient.artvideoplayer.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.salient.artplayer.Comparator;
import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.OnWindowDetachedListener;
import org.salient.artplayer.VideoView;
import org.salient.artvideoplayer.R;
import org.salient.artvideoplayer.bean.VideoBean;
import org.salient.artplayer.ui.ControlPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mai on 2018/7/17
 * *
 * Description:
 * *
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.VideoViewHolder> {

    private final int mScreenWidth;
    private List<VideoBean> mList = new ArrayList<>();

    private boolean isStaggeredGridLayoutManager = false;

    private Comparator mComparator = new Comparator() {
        @Override
        public boolean compare(VideoView videoView) {
            try {
                Object currentData = MediaPlayerManager.instance().getCurrentData();
                //By comparing the position on the list to distinguish whether the same video
                //根据列表位置识别是否同一个视频
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
        return new VideoViewHolder(view);
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
            ViewGroup.LayoutParams layoutParams = holder.videoView.getLayoutParams();
            layoutParams.height = (int) (mScreenWidth / 2 / 16 * 9 + Math.sin((position + 1) * Math.PI / 2) * 15);
            layoutParams.width = layoutParams.height / 9 * 16;
            holder.videoView.setLayoutParams(layoutParams);
        }

        //setCover
        ImageView coverView = ((ControlPanel) holder.videoView.getControlPanel()).findViewById(R.id.video_cover);
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
        }
    }

    private OnWindowDetachedListener mOnWindowDetachedListener = null;

    public void setDetachAction(OnWindowDetachedListener onWindowDetachedListener) {
        mOnWindowDetachedListener = onWindowDetachedListener;
    }

}
