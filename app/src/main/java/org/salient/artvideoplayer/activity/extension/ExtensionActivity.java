package org.salient.artvideoplayer.activity.extension;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import org.salient.artvideoplayer.BaseActivity;
import org.salient.artvideoplayer.R;

/**
 * Created by Mai on 2018/8/8
 * *
 * Description:
 * *
 */
public class ExtensionActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extension);
    }

    public void onClick(View view) {
        switch (view.getId()) {


        }
    }

}
