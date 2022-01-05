演示
1. 普通的ice服务端、客户端 
2. 客户端访问远程Glacier2后的服务
3. 客户端通过IceGridRegistry访问动态注册的服务
4. 客户端访问远程Glacier2后、通过IceGridRegistry访问动态注册的服务

有slice生成JAVA源码：
slice2java --output-dir src\main\java slice\hello.ice

操作步骤：
- 创建IceGrid数据库目录
```bash
mkdir -p deploy/lmdb/registry
```

- 启动Glacier2服务
```bash
glacier2router --Ice.Config=/home/luodahui/test/hello_ice/src/main/java/com/bglmmz/ice/demo/helloworld/gridregistry_glacier2/config.glacier2
```

- 启动IceGridRegistry服务
```bash
icegridregistry --Ice.Config=/home/luodahui/test/hello_ice/src/main/java/com/bglmmz/icdemo/helloworld/gridregistry_glacier2/config.gridregistry
```

- 启动服务器
IDE启动

- 启动客户端
IDE启动