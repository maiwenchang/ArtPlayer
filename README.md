[English](https://github.com/maiwenchang/ArtVideoPlayer/blob/master/README.English.md) | 简体中文
# ArtPlayer

![GitHub top language](https://img.shields.io/github/languages/top/maiwenchang/artPlayer)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/maiwenchang/ArtVideoPlayer/blob/master/LICENSE)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/maiwenchang/ArtPlayer?color=g)

### 简介
Kotlin实现的视频播放器，将MediaPlayer与VideoView解耦合，支持切换播放器内核（如ExoPlayer和ijkPlayer），支持自定义控制视图，提供MediaPlayerManager实现全屏模式，小屏幕模式等。

<p align="center">
<img src="https://github.com/maiwenchang/ArtPlayer/raw/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"/>
</p>

[查看详细文档](https://github.com/maiwenchang/ArtPlayer/wiki)

### 特点
- ijkPlayer支持
- ExoPlayer支持
- 全屏，小屏播放
- 完全自定义UI
- 静音
- 循环播放
- 倍速播放(IjkPlayer支持)
- 多播放器同时播放
- Raw/Assets，本地视频文件播放支持
- Activity生命周期感知，实现了onPause暂停播放，onDestory停止播放并释放资源

### 开始使用
核心依赖
```
implementation 'org.salient.artvideoplayer:artplayer-core:1.0.0'
```

选择使用ExoPlayer
```
implementation "org.salient.artvideoplayer:artplayer-exo:1.0.0"
```

选择使用IjkPlayer
```
implementation 'org.salient.artvideoplayer:artplayer-ijk:1.0.0'
implementation "org.salient.artvideoplayer:artplayer-armv7a:1.0.0"
```

需要支持不同的cpu架构
```
implementation "org.salient.artvideoplayer:artplayer-armv5:1.0.0"
implementation "org.salient.artvideoplayer:artplayer-x86:1.0.0"
//下面两个需要minSdk version >= 21
implementation "org.salient.artvideoplayer:artplayer-arm64:1.0.0"
implementation "org.salient.artvideoplayer:artplayer-x86_64:1.0.0"
```

### 使用方法

kotlin
``` kotlin
import org.salient.artplayer.VideoView

val videoView = VideoView(context)
videoView.mediaPlayer = SystemMediaPlayer().apply{
    setDataSource(context, Uri.parse("http://vfx.mtime.cn/Video/2018/07/06/mp4/180706094003288023.mp4"))
}
videoView.prepare()
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
``` kotlin
//拦截全屏时的返回事件
override fun onBackPressed() {
    if (MediaPlayerManager.blockBackPress(this)) {
        return
    }
    super.onBackPressed()
}
 ```

设置封面
``` java
//绑定封面图片资源到VideoView的`cover`字段
Glide.with(context)
        .load("http://img5.mtime.cn/mg/2018/07/06/093947.51483272.jpg")
        .into(videoView.cover);
```

### 开发中
- Kotlin版本
- 手势操作支持（小窗：单指拖动，双指缩放；全屏：音量，亮度，快进）
- 重力感应支持

### 支持
- 请在 github 上公开讨论[技术问题](https://github.com/maiwenchang/ArtPlayer/issues)


### 构建环境
- Kotlin 1.37.2
- Java 1.8
- Android Studio 3.6.0
- Gradle 5.6.4
- IjkPlayer 0.8.8
- ExoPlayer 2.11.3

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
