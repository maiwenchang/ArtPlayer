package org.salient.artplayer;

import android.media.AudioManager;
import android.util.Log;

/**
 *  Created by Mai on 2018/7/23
 * *
 *  Description: 声音焦点变化管理类
 * *
 */
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN://获得焦点
                Log.d(TAG, "AUDIOFOCUS_GAIN [" + this.hashCode() + "]");
                //MediaPlayerManager.instance().start();
                break;
            case AudioManager.AUDIOFOCUS_LOSS://声音失去焦点
                MediaPlayerManager.instance().pause();
                Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://声音短暂失去焦点
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                MediaPlayerManager.instance().pause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT://声音短暂得到焦点
                Log.d(TAG, "AUDIOFOCUS_GAIN_TRANSIENT [" + this.hashCode() + "]");
                //MediaPlayerManager.instance().start();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://todo 声音短暂(一瞬间)失去焦点,适当减低音量
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK [" + this.hashCode() + "]");
                break;
        }
    }
}
