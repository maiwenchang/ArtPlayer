package org.salient.artvideoplayer.activity.api;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
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
        try {
            //获取是否开启系统
            int isRotate = Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common:
                startActivity(new Intent(this, ApiCommonActivity.class));
                break;

        }
    }
}
