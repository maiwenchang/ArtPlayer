package org.salient.videoplayerdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.salient.videoplayerdemo.R;
import org.salient.videoplayerdemo.adapter.RecyclerViewAdapter;
import org.salient.videoplayerdemo.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * > Created by Mai on 2018/7/17
 * *
 * > Description:
 * *
 */
public class SmartModeRecyclerViewActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_mode_recycler_view);

        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        ViewCompat.setNestedScrollingEnabled(recycler_view, false);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();
        recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_view.setAdapter(recyclerViewAdapter);
        List<VideoBean> allAttention = new ArrayList<>();
        allAttention.addAll(getAllAttention());
        allAttention.addAll(getAllAttention());
        allAttention.addAll(getAllAttention());
        allAttention.addAll(getAllAttention());
        allAttention.addAll(getAllAttention());
        recyclerViewAdapter.setList(allAttention);
        recyclerViewAdapter.notifyDataSetChanged();

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.smartMode:

                break;
        }
    }
}
