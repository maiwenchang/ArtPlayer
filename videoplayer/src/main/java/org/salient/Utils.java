package org.salient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Window;
import android.view.WindowManager;

import java.util.LinkedHashMap;

/**
 * > Created by Mai on 2018/7/10
 * *
 * > Description:
 * *
 */
public class Utils {

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
