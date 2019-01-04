package org.salient.artvideoplayer;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.squareup.leakcanary.LeakCanary;

import org.salient.artvideoplayer.bean.MovieData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;

/**
 * Created by Mai on 2018/7/17
 * *
 * Description:
 * *
 */
public class BaseApplication extends Application {

    private static String jsonString = "";

    public static MovieData getMovieData() {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        } else {
            return new Gson().fromJson(jsonString, MovieData.class);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                jsonString = readAssetsFile("video.json");
            }
        });
    }

    /**
     * 读取assets中的文件
     *
     * @param path File Path
     * @return File Content String
     */
    public String readAssetsFile(String path) {
        String result = "";
        try {
            // read file content from file
            StringBuilder sb = new StringBuilder("");
            InputStreamReader reader = new InputStreamReader(getResources().getAssets().open(path));

            BufferedReader br = new BufferedReader(reader);
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            result = sb.toString();
            br.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
