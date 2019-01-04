package org.salient.artvideoplayer.activity.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ListView;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artvideoplayer.BaseActivity;
import org.salient.artvideoplayer.adapter.ListViewAdapter;
import org.salient.artvideoplayer.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mai on 2018/7/19
 * *
 * Description:
 * *
 */
public class ListViewActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ListView listView = new ListView(this);
        setContentView(listView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ListViewAdapter listViewAdapter = new ListViewAdapter();
        listView.setAdapter(listViewAdapter);
        listView.setDividerHeight(30);
        List<VideoBean> list = new ArrayList<>();
        list.addAll(getAllAttention());
        list.addAll(getAllComing());
        listViewAdapter.setList(list);
        listViewAdapter.notifyDataSetChanged();
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
