package org.salient.artvideoplayer.activity.list;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import org.salient.artplayer.VideoView;
import org.salient.artvideoplayer.BaseActivity;
import org.salient.artvideoplayer.R;
import org.salient.controlpanel.ControlPanel;

/**
 * Created by Mai on 2018/8/7
 * *
 * Description:
 * *
 */
public class ListActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recyclerView:
                startActivity(new Intent(this, RecyclerViewActivity.class));
                break;
            case R.id.listView:
                startActivity(new Intent(this, ListViewActivity.class));
                break;
        }
    }
}
