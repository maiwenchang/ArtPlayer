package org.salient.artvideoplayer.activity.api;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;

import org.salient.artvideoplayer.BaseActivity;
import org.salient.artvideoplayer.R;

/**
 * Created by Mai on 2018/8/7
 * *
 * Description:
 * *
 */
public class ApiActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common:
                startActivity(new Intent(this, ApiCommonActivity.class));
                break;
            case R.id.rawAssets:
                startActivity(new Intent(this,ApiRawAssetsActivity.class));
                break;

        }
    }

}
