[English](https://github.com/maiwenchang/ArtVideoPlayer/blob/master/README.English.md) | 简体中文
# ArtPlayer

[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/maiwenchang/ArtVideoPlayer/blob/master/LICENSE)

### 简介
这是一个灵活的视频播放器。 MediaPlayer与VideoView完全分开，可以替换为其他播放器内核，如ExoPlayer和ijkPlayer。 可以完全自定义播放器视图，我们称之为控制面板。 此外，可以使用MediaPlayerManager来控制播放行为，例如全屏模式，小屏幕模式以及RecyclerView中的智能匹配模式。

<p align="center">
<img src="https://github.com/maiwenchang/ArtPlayer/raw/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"/>
</p>

### 特点
- 全屏，小屏播放
- 内部支持RecyclerView中播放
- 自定义UI
- APP内全局播放
- 静音
- 循环播放
- 手势操作（小窗：单指拖动，双指缩放；全屏：音量，亮度，快进）
- ijkPlayer支持
- ExoPlayer支持
- 重力感应支持
- Raw/Assets，本地视频文件播放支持

### 预览
<img src="https://github.com/maiwenchang/ArtPlayer/raw/master/pic/main.png" height="500"/><img src="https://github.com/maiwenchang/ArtPlayer/raw/master/pic/mediaplayer.png" height="500"/><img src="https://github.com/maiwenchang/ArtPlayer/raw/master/pic/api.png" height="500"/><img src="https://github.com/maiwenchang/ArtPlayer/raw/master/pic/list.png" height="500"/><img src="https://github.com/maiwenchang/ArtPlayer/raw/master/pic/recyclerview.png" height="500"/><img src="https://github.com/maiwenchang/ArtPlayer/raw/master/pic/extension.png" height="500"/>

### 下载

 - [Demo Download](https://github.com/maiwenchang/ArtPlayer/raw/master/app/debug/artplayer-debug.apk)
 - ![image](https://github.com/maiwenchang/ArtPlayer/raw/master/pic/apkqrcode.png)


### 开始使用
`build.gradle`
```
dependencies {
    // required
    implementation 'org.salient.artvideoplayer:artplayer-java:0.7.0'

    // Default control panel: optional
    implementation 'org.salient.artvideoplayer:artplayer-ui:0.7.0'

     //ijkPlayer: optional
     implementation 'org.salient.artvideoplayer:artplayer-ijk:0.7.0'
     implementation "org.salient.artvideoplayer:artplayer-armv7a:0.7.0"

      //Other ABIs: optional
     implementation "org.salient.artvideoplayer:artplayer-armv5:0.7.0"
     implementation "org.salient.artvideoplayer:artplayer-x86:0.7.0"
     // Other ABIs: optional (minSdk version >= 21)
     implementation "org.salient.artvideoplayer:artplayer-arm64:0.7.0"
     implementation "org.salient.artvideoplayer:artplayer-x86_64:0.7.0"

     //ExoPlayer2 : optional
     implementation "org.salient.artvideoplayer:artplayer-exo:0.7.0"
}
```

### 使用方法

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

### 计划中
- 音频播放
- Kotlin版本

### 不在计划
- 多播放器播放


### 支持
- 请在 github 上公开讨论[技术问题](https://github.com/maiwenchang/ArtPlayer/issues)


### 构建环境
- Java 1.7
- Android Studio 3.1.2
- Gradle 4.4
- IjkPlayer 0.8.8
- ExoPlayer 2.8.3

### 作者
- [maiwenchang](https://github.com/maiwenchang)
- [ironman6121](https://github.com/ironman6121)

### 联系方式
- cv.stronger@gmail.com

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
