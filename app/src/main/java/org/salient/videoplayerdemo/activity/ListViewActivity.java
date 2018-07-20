package org.salient.videoplayerdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import org.salient.videoplayerdemo.adapter.ListViewAdapter;

/**
 * > Created by Mai on 2018/7/19
 * *
 * > Description:
 * *
 */
public class ListViewActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ListView listView = new ListView(this);
        setContentView(listView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ListViewAdapter listViewAdapter = new ListViewAdapter();
        listView.setAdapter(listViewAdapter);
        listViewAdapter.setList(getAllComing());
        listViewAdapter.notifyDataSetChanged();
    }


}
