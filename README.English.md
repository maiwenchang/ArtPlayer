English | [简体中文](https://github.com/maiwenchang/ArtVideoPlayer/blob/master/README.md)
# ArtPlayer

[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/maiwenchang/ArtVideoPlayer/blob/master/LICENSE)

### Introduction
This is a flexible video player. MediaPlayer is completely separate from VideoView and can be replaced with other player kernels such as ExoPlayer and ijkPlayer. Developers can fully customize the player view, which we call the control panel. In addition, developers can use MediaPlayerManager to control playback behaviours, such as full-screen mode, small screen mode, and smart matching modes in RecyclerView.

<p align="center">
<img src="https://github.com/maiwenchang/ArtPlayer/raw/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"/>
</p>

### Features
- Fullscreen,TinyWindow play
- Support for playing in RecyclerView
- Custom UI
- Global playback in APP
- Mute
- Loop Playback
- Gesture manipulation (small window: single finger drag, double finger zoom; full screen: volume, brightness, fast forward)
- ijkPlayer support
- ExoPlayer support
- Gravity sensor support
- Raw/Assets, and local playback support

### Preview
<img src="https://github.com/maiwenchang/ArtPlayer/raw/master/pic/main.png" height="500"/><img src="https://github.com/maiwenchang/ArtPlayer/raw/master/pic/mediaplayer.png" height="500"/><img src="https://github.com/maiwenchang/ArtPlayer/raw/master/pic/api.png" height="500"/><img src="https://github.com/maiwenchang/ArtPlayer/raw/master/pic/list.png" height="500"/><img src="https://github.com/maiwenchang/ArtPlayer/raw/master/pic/recyclerview.png" height="500"/><img src="https://github.com/maiwenchang/ArtPlayer/raw/master/pic/extension.png" height="500"/>

### Download

 - [Demo Download](https://github.com/maiwenchang/ArtPlayer/raw/master/app/debug/artplayer-debug.apk)
 - ![image](https://github.com/maiwenchang/ArtPlayer/raw/master/pic/apkqrcode.png)


### Getting started
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
- Audio playback
- Kotlin version

### NOT-ON-PLAN
- Multiple MediaPlayer playback


### Support
- Public technical discussion on github is preferred.[Technical problems](https://github.com/maiwenchang/ArtPlayer/issues)


### My Build Environment
- Java 1.7
- Android Studio 3.1.2
- Gradle 4.4
- IjkPlayer 0.8.8
- ExoPlayer 2.8.3

### Authors
- [maiwenchang](https://github.com/maiwenchang)
- [ironman6121](https://github.com/ironman6121)

### Contact
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
