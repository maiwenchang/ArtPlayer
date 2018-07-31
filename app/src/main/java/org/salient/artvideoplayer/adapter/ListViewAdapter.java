package org.salient.artvideoplayer.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;

import org.salient.artplayer.Comparator;
import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.VideoView;
import org.salient.artvideoplayer.R;
import org.salient.artvideoplayer.bean.VideoBean;
import org.salient.controlpanel.ControlPanel;

import java.util.List;

/**
 * > Created by Mai on 2018/7/19
 * *
 * > Description:
 * *
 */
public class ListViewAdapter extends BaseAdapter {

    private List<VideoBean> mList;
    // We use the ListPosition of video to distinguish whether it is the same video.
    // If is, return 0.
    private Comparator mComparator = new Comparator() {
        @Override
        public boolean compare(VideoView videoView) {
            try {
                Object dataSource = MediaPlayerManager.instance().getDataSource();
                if (dataSource != null && videoView != null) {
                    boolean b = dataSource == videoView.getDataSourceObject();
                    Log.d("ListViewAdapter", "Comparator : " + b + " Position : " + ((VideoBean) videoView.getData()).getListPosition());
                    return dataSource == videoView.getDataSourceObject();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };

    public void setList(List<VideoBean> mList) {
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public VideoBean getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position) == null ? 0 : getItem(position).getVideoId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_view, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final VideoBean item = getItem(position);
        if (item != null) {
            item.setListPosition(position);
            VideoView videoView = viewHolder.videoView;
            videoView.setUp(item.getUrl(), VideoView.WindowType.LIST, item);
            ControlPanel controlPanel = (ControlPanel) videoView.getControlPanel();
            Glide.with(videoView.getContext()).load(item.getImage()).into(controlPanel.getCoverView());
        }
        return convertView;
    }

    class ViewHolder {
        VideoView videoView;

        ViewHolder(View convertView) {
            videoView = convertView.findViewById(R.id.videoView);
            videoView.setControlPanel(new ControlPanel(convertView.getContext()));


            //videoView.setComparator(mComparator);

//            videoView.setOnWindowDetachedListener(new OnWindowDetachedListener() {
//                @Override
//                public void onDetached(VideoView videoView) {
//                    if (videoView.isCurrentPlaying()  && videoView == MediaPlayerManager.instance().getCurrentVideoView()) {
//                        //开启小窗
//                        VideoView tinyVideoView = new VideoView(videoView.getContext());
//                        tinyVideoView.setUp(videoView.getDataSourceObject(), VideoView.WindowType.TINY, videoView.getData());
//                        tinyVideoView.setControlPanel(new ControlPanel(videoView.getContext()));
//                        tinyVideoView.setParentVideoView(videoView);
//                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(16 * 40, 9 * 40);
//                        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
//                        layoutParams.setMargins(0, 0, 30, 100);
//                        MediaPlayerManager.instance().startTinyWindow(tinyVideoView);
//                    }
//                }
//            });
        }
    }
}
