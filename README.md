# LiveApp
2017年 毕业设计项目--湖科大直播平台

该项目是Android客户端应用，主要包含了主页播放列表，分类，播放历史，直播间聊天室，刷礼物，用户登录，直播功能等

使用MVP、Retrofit、Rxjava架构

播放器是使用的是三方封装的ijkplayer: **[jjdxm_ijkplayer](https://github.com/lingcimi/jjdxm_ijkplayer)**,这个项目一个ijkplayer的
二次封装库，已经很久没有维护了。所以可能有些问题。

再在播放器View技术上实现礼物刷屏，以及弹幕等功能
弹幕是采用了[bilibili的弹幕开源库](https://github.com/bilibili/DanmakuFlameMaster)

直播流是采用rtmp协议，

直播服务器可自己搭建，也可以是用百度云直播服务

后台代码暂未上传