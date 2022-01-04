本demo演示了Glacier2 + IceGridRegistry 的集成。

1. IceGridRegistry部署在内网，它就是个服务地址注册和定位器。
   不要被IceGridRegistry这个名字迷惑，它可以单独作为servant的注册器存在。
   并不一定要有IceGrid, IceNode存在。
   通过IceGridRegistry寻址的模式是：servantIdentityId@adapterId
   
   如果要动态注册servant，需要增加如下配置：   
   IceGrid.Registry.DynamicRegistration=1
   

2. Glacier2的配置中，增加IceGridRegistry的配置，以便Glacier2在转发时，首先定位目标ice服务的地址。
   Ice.Default.Locator=DefaultIceGrid/Locator:tcp -p 4061 -h 192.168.21.26

3. 服务端代码编写，动态注册servant时，几乎不变，只需要在初始化Communicator前，增加配置IceGridRegistry的代码。
   //首先确定taskIceServantAdapterName，taskIceServantAdapterId
   //其中，taskIceServantAdapterName可以相同，而taskIceServantAdapterId必须唯一。
   properties.setProperty(taskIceServantAdapterName + ".Endpoints", "tcp -h 192.168.21.26");
   properties.setProperty(taskIceServantAdapterName + ".AdapterId", taskIceServantAdapterId);
   
   //配置IceGridRegistry的地址：
   properties.setProperty("Ice.Default.Locator", "DefaultIceGrid/Locator:tcp -p 4061 -h 192.168.21.26");
   有了这个配置， 在服务端激活适配器时，就会把servant注册到IceGridRegistry中，寻址方式：servantIdentityId@adapterId
  
   //激活adapter
   adapter.activate();

4. 客户端代码编写，和普通访问Glacier2后面的服务一样，只需在客户端初始化Communicator前，增加配置Glacier2的代码。
   properties.setProperty("Ice.Default.Router", "DefaultGlacier2/router:tcp -p 4064 -h 192.168.10.149");

demo演示步骤：
1. 根据Registry.cfg中的配置
    IceGrid.Registry.LMDB.Path=deploy/lmdb/registry 
    创建目录deploy/lmdb/registry

2. 在命令行窗口，或者IDEA的Terminal中运行：
   icegridregistry.exe --Ice.Config=D:\github.com\bglmmz\hello_ice\src\main\java\com\bglmmz\ice\demo\helloworld\gridregistry_glacier2\config.gridregistry
   
3. 在IDEA中运行HelloServer, 以及HelloClient, 在client中根据提示输入命令即可。

说明：
Glacier2作用相当于router，部署于内网，远程客户端要能访问。一般可以提供一个公网IP给外网客户端访问。
Registry作用相当于服务注册表，部署于内网。
Servant就是用户实现的ice服务。
Ice Server就是提供监听端口的服务进程。此服务进程启动时，需要把servant绑定到adapter。

