Java Http Proxy Framework
=============

**Java Http Proxy Framework是一个Java实现的Http Proxy，支持Plugin模式来扩展功能。**

## 环境

运行需要Jdk1.6，开箱即用。   
编译需要Jdk1.7和ant。不过自行编译后，运行也需要Jdk1.7了（我编译的proxy.jar做了特殊处理的）。   


## 立即开始

1. 下载[proxy.jar](https://github.com/zms351/JavaHttpProxy/raw/master/dist/proxy.jar)。   
2. 双击这个jar文件即可（windows/linux/mac皆可）。   
3. 如果不行，下载[start.bat](https://github.com/zms351/JavaHttpProxy/raw/master/dist/start.bat)这个启动脚本，用脚本启动，这个启动脚本也可以在linux/mac下执行（可能需要chmod a+x)。   
4. 效果是，在**58088**端口侦听，创立一个Http代理，启动浏览器，设置代理为localhost:58088，试试看。   
默认有两个Plugin，其中一个会把所有的通讯内容记录到自动创建的files目录下。

## 这有什么用？

用处很多，比方说，你浏览器访问优酷、土豆、奇艺、在线听歌，你看过听过的视频音频文件都会自动保存到本地（虽然保存的路径需要你找一下）。   
其它，比如你想了解一个桌面应用发出了什么http的请求（以前的做法是用抓包工具）。

## 我需要技术支持

自己摸索吧。实在不行，到水木社区Java版找我。