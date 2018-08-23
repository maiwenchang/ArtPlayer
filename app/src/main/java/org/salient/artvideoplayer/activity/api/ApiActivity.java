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

    private SensorManager sm;

    private Sensor sensor;
    /**
     * 加速度传感器监听
     */
    protected OrientationEventListener orientationEventListener;

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
//        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
//        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        orientationEventListener = new OrientationEventListener(this) { // 加速度传感器监听，用于自动旋转屏幕
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation >= 340) { //屏幕顶部朝上
//                    Log.i("orientation", "屏幕顶部朝上");
                } else if (orientation >= 260 && orientation <= 280) { //屏幕左边朝上
//                    Log.i("orientation", "屏幕左边朝上");
                } else if (orientation >= 70 && orientation <= 90) { //屏幕右边朝上
//                    Log.i("orientation", "屏幕右边朝上");
                }
            }
        };

    }

    SensorEventListener myListener = new SensorEventListener() {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];

            Log.i("onSensorChanged", " X :" + X + " Y :" + Y + " Z :" + Z);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common:
                startActivity(new Intent(this, ApiCommonActivity.class));
                break;

        }
    }

    @Override
    protected void onResume() {
//        sm.registerListener(myListener, sensor, SensorManager.SENSOR_DELAY_UI);
        orientationEventListener.enable();
        super.onResume();
    }

    @Override
    protected void onPause() {
//        sm.unregisterListener(myListener);
        orientationEventListener.disable();
        super.onPause();
    }
}
