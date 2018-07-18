package org.salient.videoplayerdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.salient.videoplayerdemo.R;
import org.salient.videoplayerdemo.adapter.RecyclerViewAdapter;

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
        recyclerViewAdapter.setList(getAllAttention());
        recyclerViewAdapter.notifyDataSetChanged();

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.smartMode:

                break;
        }
    }
}
