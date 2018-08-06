package org.salient.artvideoplayer;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 *  Created by Mai on 2018/5/18
 * *
 *  Description:屏幕像素转换类
 * *
 */
public class DensityUtil {

    /**
     * dip转像素
     */
    public static int dip2px(Context context, int dip) {
        final float SCALE = context.getResources().getDisplayMetrics().density;
        return (int) ((float) dip * SCALE + 0.5f);
    }

    /**
     * 像素转dip
     */
    public static float px2dip(Context context, int Pixels) {
        final float SCALE = context.getResources().getDisplayMetrics().density;
        return Pixels / SCALE;
    }

    /**
     * 屏幕分辨率宽
     */
    public static int getWindowWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 屏幕分辩类高
     */
    public static int getWindowHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 屏幕的dpi
     */
    public static float getDmDensityDpi(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }
}
