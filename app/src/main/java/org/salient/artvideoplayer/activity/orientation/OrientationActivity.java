package org.salient.artvideoplayer.activity.orientation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import org.salient.artvideoplayer.R;
import org.salient.artvideoplayer.activity.api.ApiCommonActivity;
import org.salient.artvideoplayer.activity.list.ListViewActivity;
import org.salient.artvideoplayer.activity.list.RecyclerViewActivity;

public class OrientationActivity extends Activity implements View.OnClickListener {
    private Button normal;
    private Button listViewOrientation;
    private Button recyclerViewOrientation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation);
        initView();
    }

    private void initView() {
        normal = findViewById(R.id.normal);
        listViewOrientation = findViewById(R.id.listViewOrientation);
        recyclerViewOrientation = findViewById(R.id.recyclerViewOrientation);

        normal.setOnClickListener(this);
        listViewOrientation.setOnClickListener(this);
        recyclerViewOrientation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.normal:
                startActivity(new Intent(this, ApiCommonActivity.class));
                break;
            case R.id.listViewOrientation:
                startActivity(new Intent(this, ListViewActivity.class));
                break;
            case R.id.recyclerViewOrientation:
                startActivity(new Intent(this, RecyclerViewActivity.class));
                break;
        }
    }
}
