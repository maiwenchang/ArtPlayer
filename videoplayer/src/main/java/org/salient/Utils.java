package org.salient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * > Created by Mai on 2018/7/10
 * *
 * > Description:
 * *
 */
public class Utils {
    public static final String TAG = "Utils";

    public static String stringForTime(long timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        int seconds = (int) (totalSeconds % 60);
        int minutes = (int) ((totalSeconds / 60) % 60);
        int hours = (int) (totalSeconds / 3600);
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * This method requires the caller to hold the permission ACCESS_NETWORK_STATE.
     *
     * @param context context
     * @return if wifi is connected,return true
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Get activity from context object
     *
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    public static Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }

        return null;
    }

    /**
     * Get AppCompatActivity from context
     *
     * @param context context
     * @return AppCompatActivity if it's not null
     */
    public static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    public static void setRequestedOrientation(Context context, int orientation) {
        if (getAppCompActivity(context) != null) {
            getAppCompActivity(context).setRequestedOrientation(orientation);
        } else {
            scanForActivity(context).setRequestedOrientation(orientation);
        }
    }

    public static Window getWindow(Context context) {
        if (getAppCompActivity(context) != null) {
            return getAppCompActivity(context).getWindow();
        } else {
            return scanForActivity(context).getWindow();
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void saveProgress(Context context, Object url, long progress) {
        Log.i(TAG, "saveProgress: " + progress);
        if (progress < 5000) {
            progress = 0;
        }
        SharedPreferences spn = context.getSharedPreferences("video_progress", Context.MODE_PRIVATE);
        spn.edit().putLong("newVersion:" + url.toString(), progress).apply();
    }

    public static long getSavedProgress(Context context, Object url) {
        SharedPreferences spn = context.getSharedPreferences("video_progress", Context.MODE_PRIVATE);
        return spn.getLong("newVersion:" + url.toString(), 0);
    }

    /**
     * if url == null, clear all progress
     *
     * @param context context
     * @param url     if url!=null clear this url progress
     */
    public static void clearSavedProgress(Context context, Object url) {
        if (url == null) {
            SharedPreferences spn = context.getSharedPreferences("video_progress",
                    Context.MODE_PRIVATE);
            spn.edit().clear().apply();
        } else {
            SharedPreferences spn = context.getSharedPreferences("video_progress",
                    Context.MODE_PRIVATE);
            spn.edit().putLong("newVersion:" + url.toString(), 0).apply();
        }
    }

    public static Object getCurrentFromDataSource(Object[] dataSourceObjects, int index) {
        if (dataSourceObjects == null || dataSourceObjects[0] == null || !(dataSourceObjects[0] instanceof LinkedHashMap)) {
            return null;
        }
        LinkedHashMap<String, Object> map = (LinkedHashMap) dataSourceObjects[0];
        if (map.size() > 0) {
            return getValueFromLinkedMap(map, index);
        }
        return null;
    }

    public static Object getValueFromLinkedMap(LinkedHashMap<String, Object> map, int index) {
        int currentIndex = 0;
        for (String key : map.keySet()) {
            if (currentIndex == index) {
                return map.get(key);
            }
            currentIndex++;
        }
        return null;
    }

    public static boolean dataSourceObjectsContainsUri(Object[] dataSourceObjects, Object object) {
        if (dataSourceObjects == null || dataSourceObjects[0] == null || !(dataSourceObjects[0] instanceof LinkedHashMap)) {
            return false;
        }
        LinkedHashMap map = (LinkedHashMap) dataSourceObjects[0];
        return object != null && map.containsValue(object);
    }

    public static String getKeyFromDataSource(Object[] dataSourceObjects, int index) {
        if (dataSourceObjects == null || dataSourceObjects[0] == null || !(dataSourceObjects[0] instanceof LinkedHashMap)) {
            return null;
        }
        LinkedHashMap<String, Object> map = (LinkedHashMap) dataSourceObjects[0];
        int currentIndex = 0;
        for (String key : map.keySet()) {
            if (currentIndex == index) {
                return key;
            }
            currentIndex++;
        }
        return null;
    }

    @SuppressLint("RestrictedApi")
    public static void hideSupportActionBar(Context context) {
        if (Utils.getAppCompActivity(context) != null) {
            ActionBar ab = Utils.getAppCompActivity(context).getSupportActionBar();
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false);
                ab.hide();
            }
        }
        Utils.getWindow(context).setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @SuppressLint("RestrictedApi")
    public static void showSupportActionBar(Context context) {
        if (Utils.getAppCompActivity(context) != null) {
            ActionBar ab = Utils.getAppCompActivity(context).getSupportActionBar();
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false);
                ab.show();
            }
        }
        Utils.getWindow(context).clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
