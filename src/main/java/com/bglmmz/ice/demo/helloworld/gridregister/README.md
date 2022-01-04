本demo演示动态创建servant，adapter，并把servant绑定到adapter上。客户端访问时，无需知道服务端ip和端口。

1. 可以注册多个servant到同一个adapter, 只要用identityID来区分不同的servant
2. 并没有创建IceGrid, IceNode, 仅仅是利用了IceGridRegistry。不要被IceGridRegistry这个名字迷惑，它可以单独作为servant的注册器存在。
3. 动态注册Ice Object（对于我们来说就是servant）需要在iceGridRegistry的配置设置：IceGrid.Registry.DynamicRegistration=1 

** 服务端代码、客户端代码参考源码中注释


demo演示步骤：
1. 根据Registry.cfg中的配置
    IceGrid.Registry.LMDB.Path=deploy/lmdb/registry 
    创建目录deploy/lmdb/registry

2. 在命令行窗口，或者IDEA的Terminal中运行：
   icegridregistry.exe --Ice.Config=D:\github.com\bglmmz\hello_ice\src\main\java\com\bglmmz\ice\demo\helloworld\grid\Registry.cfg
   
3. 在IDEA中运行HelloServer, HelloServer2, 以及HelloClient, HelloClient2，在client中根据提示输入命令即可。

