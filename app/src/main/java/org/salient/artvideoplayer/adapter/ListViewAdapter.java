package org.salient.artvideoplayer.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.OnWindowDetachedListener;
import org.salient.artplayer.VideoView;
import org.salient.artvideoplayer.R;
import org.salient.artvideoplayer.bean.VideoBean;
import org.salient.artplayer.ui.ControlPanel;

import java.util.List;

/**
 * Created by Mai on 2018/7/19
 * *
 * Description:
 * *
 */
public class ListViewAdapter extends BaseAdapter {

    private List<VideoBean> mList;

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
            Glide.with(videoView.getContext()).load(item.getImage()).into((ImageView) controlPanel.findViewById(R.id.video_cover));
        }
        return convertView;
    }

    class ViewHolder {
        VideoView videoView;

        ViewHolder(View convertView) {
            videoView = convertView.findViewById(R.id.videoView);
            videoView.setControlPanel(new ControlPanel(convertView.getContext()));

            //optional: Specify the Detach Action which would be called when the VideoView has been detached from its window.
            videoView.setOnWindowDetachedListener(new OnWindowDetachedListener() {
                @Override
                public void onDetached(VideoView videoView) {
                    if (videoView.isCurrentPlaying() && videoView == MediaPlayerManager.instance().getCurrentVideoView()) {
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
                }
            });
        }
    }
}
