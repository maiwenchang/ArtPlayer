package org.salient.artvideoplayer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import org.salient.artvideoplayer.R;
import org.salient.artvideoplayer.adapter.RecyclerViewAdapter;
import org.salient.artvideoplayer.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * > Created by Mai on 2018/7/17
 * *
 * > Description:
 * *
 */
public class RecyclerViewActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_mode_recycler_view);

        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        ViewCompat.setNestedScrollingEnabled(recycler_view, false);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //GridLayoutManager layoutManager = new GridLayoutManager(this, 3);

        //StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

        recycler_view.setLayoutManager(layoutManager);

        recycler_view.setAdapter(recyclerViewAdapter);
        List<VideoBean> list = new ArrayList<>();
        list.addAll(getAllComing());
        list.addAll(getAllAttention());
        recyclerViewAdapter.setList(list);
        recyclerViewAdapter.notifyDataSetChanged();

    }

    public void onClick(View view) {
        switch (view.getId()) {


        }
    }
}
