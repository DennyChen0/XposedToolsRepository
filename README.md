# XposedToolsRepository
对Xposed模块进行封装，包含需要的模块，以及代码说明
1. 工程项目中包含3个包名：dc.hook.encapsulation、dc.reverse.mod、dc.utils, 模块入口在dc.reverse.mod中；

2. 入口处，首先是对attach进行了hook，然后在内层再对真正要hook的函数进行hook，这样做的目的是因为当某个apk的
dex很多时，其中的一些并不一定在一开始就加载到内存，这时如果正巧对那个dex中的函数进行hook时，会找不到那个函数，
但dex加载时，attach必然会先调用，这个函数被hook了，Xposed中会调回调函数，然后再hook dex中函数，此时必然能找
到。

3. 在模块中还注册了一个广播，这个广播在逆向分析中是很大的，为分析者想安卓中提供参数，然后反射调用安卓中的方法
起到很大作用，项目中“adb发广播通知.txt”文件中有简单介绍。

4. 这个Xposed模块中原本是对momo抢红包的功能进行封装的，所以如果安卓手机里面安装了momo，那么在安装好Xposed后，
是可以自动抢红包的（所有的类型都支持：单个好友红包、群红包、讨论组红包）。
