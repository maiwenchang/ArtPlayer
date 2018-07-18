package org.salient.videoplayerdemo;

import android.app.Application;

import com.google.gson.Gson;

import org.salient.videoplayerdemo.bean.MovieData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;

/**
 * > Created by Mai on 2018/7/17
 * *
 * > Description:
 * *
 */
public class BaseApplication extends Application {

    private static MovieData mMovieData;

    @Override
    public void onCreate() {
        super.onCreate();

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                String json = readAssetsFile("video.json");
                mMovieData = new Gson().fromJson(json, MovieData.class);
            }
        });
    }

    public static MovieData getMovieData() {
        return mMovieData;
    }

    /**
     * 读取assets中的文件
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
