package org.salient.videoplayerdemo.adapter;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;

import org.salient.VideoView;
import org.salient.videoplayerdemo.ControlPanel;
import org.salient.videoplayerdemo.R;
import org.salient.videoplayerdemo.bean.VideoBean;

import java.util.Comparator;
import java.util.List;

/**
 * > Created by Mai on 2018/7/19
 * *
 * > Description:
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
        Log.d(getClass().getSimpleName(), "getView : " + position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_view, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.videoView = convertView.findViewById(R.id.videoView);
            viewHolder.videoView.setControlPanel(new ControlPanel(parent.getContext()));
            convertView.setTag(viewHolder);
            Log.d(getClass().getSimpleName(), "new ViewHolder");
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final VideoBean item = getItem(position);
        if (item != null) {
            viewHolder.videoView.setUp(item.getUrl(), item);
            viewHolder.videoView.setComparator(new Comparator<VideoView<VideoBean>>() {
                @Override
                public int compare(VideoView<VideoBean> o1, VideoView<VideoBean> o2) {
                    if (item.getVideoId() == o2.getData().getVideoId()) {
                        return 0;
                    }
                    return -1;
                }
            });
            ControlPanel controlPanel = (ControlPanel) viewHolder.videoView.getControlPanel();
            Glide.with(viewHolder.videoView.getContext())
                    .load(item.getImage())
                    .into(controlPanel.getCoverView());
        }
        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    public class ViewHolder {
        VideoView<VideoBean> videoView;
    }
}
