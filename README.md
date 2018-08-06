# ArtVideoPlayer

[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/maiwenchang/ArtVideoPlayer/blob/master/LICENSE)

###
This is a flexible video player. MediaPlayer is completely separate from VideoView and can be replaced with other player kernels such as ExoPlayer and ijkPlayer. Developers can fully customize the player view, which we call the control panel. In addition, developers can use MediaPlayerManager to control playback behaviours, such as full-screen mode, small screen mode, and smart matching modes in RecyclerView.

###
这是一个灵活的视频播放器。 MediaPlayer与VideoView完全分开，可以替换为其他播放器内核，如ExoPlayer和ijkPlayer。 可以完全自定义播放器视图，我们称之为控制面板。 此外，可以使用MediaPlayerManager来控制播放行为，例如全屏模式，小屏幕模式以及RecyclerView中的智能匹配模式。

### Features
- 全屏，小屏播放
- 内部支持RecyclerView中播放
- 自定义UI
- APP内全局播放
- 静音
- 循环播放

### Preview
#### 列表播放

|keep|pause|stop|minify
|:---:|:---:|:---:|:---:|
![image](https://github.com/maiwenchang/ArtVideoPlayer/raw/master/pic/keep_playing.gif)|![image](https://github.com/maiwenchang/ArtVideoPlayer/raw/master/pic/pause.gif)|![image](https://github.com/maiwenchang/ArtVideoPlayer/raw/master/pic/stop.gif)|![image](https://github.com/maiwenchang/ArtVideoPlayer/raw/master/pic/minify.gif)

### Download

 - [Demo Download](https://github.com/maiwenchang/ArtVideoPlayer/raw/master/app/debug/artplayer-debug.apk)
 - ![image](https://github.com/maiwenchang/ArtVideoPlayer/raw/master/pic/apkqrcode.png)


### Getting started
`build.gradle`
```
dependencies {
    // required
    implementation 'org.salient.artvideoplayer:artplayer-java:0.5'

    // optional:  Default control panel
    implementation 'org.salient.artvideoplayer:artplayer-ui:0.5'
}
```

### Usage

 java
 ``` java
 import org.salient.artplayer.VideoView;

 VideoView videoView = new VideoView(this);
 videoView.setUp("http://vfx.mtime.cn/Video/2018/06/27/mp4/180627094726195356.mp4");
 videoView.setControlPanel(new ControlPanel(this));
 videoView.start();
 ```

 xml
  ``` xml
 <org.salient.artplayer.VideoView
 	android:id="@+id/video_view"
 	android:layout_width="match_parent"
 	android:layout_height="200dp"/>
 ```

`AndroidManifest.xml`
  ``` xml
<activity
    android:name=".YourActivity"
    android:configChanges="orientation|screenSize" /> <!-- required -->
 ```

Activity
  ``` java
@Override
public void onBackPressed() {
    if (MediaPlayerManager.instance().backPress(this)) {
        return;
    }
    super.onBackPressed();
}

@Override
protected void onPause() {
    super.onPause();
    MediaPlayerManager.instance().pause();
}

@Override
protected void onDestroy() {
    super.onDestroy();
    MediaPlayerManager.instance().releasePlayerAndView(this);
}
 ```

### ON-PLAN
- 视频画面比例
- ijkPlayer支持
- ExoPlayer支持
- 音频播放
- Kotlin版本

### NOT-ON-PLAN(不在计划)
- Multiple MediaPlayer playback
- 多播放器播放


### Support (支持) ###
- Public technical discussion on github is preferred.
- 请在 github 上公开讨论[技术问题](https://github.com/maiwenchang/ArtVideoPlayer/issues)


### My Build Environment
- Java 1.7
- Android Studio 3.1.2
- Gradle 3.1.2

### Authors
- [maiwenchang](https://github.com/maiwenchang)
- [ironman6121](https://github.com/ironman6121)


### License

```
   Copyright 2018 maiwenchang

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
