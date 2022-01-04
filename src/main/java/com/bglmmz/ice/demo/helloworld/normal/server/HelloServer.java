package com.bglmmz.ice.demo.helloworld.normal.server;

import com.bglmmz.ice.demo.helloworld.impl.HelloServiceImpl;

public class HelloServer {
    public static void main(String[] args) {

        int status = 0;

        try (com.zeroc.Ice.Communicator ic = com.zeroc.Ice.Util.initialize(args)){
            // 调用Ice.Util.Initialize()初始化Ice run time
            System.out.println("初始化ice run time...");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> ic.destroy()));

            // 创建一个对象适配器，传入适配器名字和在10000端口处接收来的请求
            System.out.println("创建对象适配器，监听端口10001...");

            //adapter的名字可以随意，一般可以用以下模板：$identityID$Adapter，其中$identityID$是服务ID
            //重要的是：地址和端口，表示adapter在此端口上监听，并代理对服务(Servant)的调用。
            //一个端口只能有1个adapter来监听；
            //一个adapter可以代理多个servant实例；同一个servant可以用不同的identityID来加入同一个adapter，然后客户端可以用不同的identityID来调用不同的servant实例。

            //同一个服务，如果要多进程得方式运行，则必然需要监听不同的端口；而客户端则需要知道这些端口
            com.zeroc.Ice.ObjectAdapter adapter = ic.createObjectAdapterWithEndpoints("HelloServiceAdapter", "default -p 10001");

            // 实例化一个PrinterI对象，为Printer接口创建一个servant
            System.out.println("为接口创建servant...");
            //Servant 是一个实现了slice中定义的接口，可以认为就是个服务
            com.zeroc.Ice.Object object = new HelloServiceImpl();

            // 调用适配器的add,告诉它有一个新的servant,传递的参数是刚才的servant,这里的“HelloService”是Servant的名字
            System.out.println("对象适配器加入servant...");
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("HelloService"));

            //调用适配器的activate()方法，激活适配器。被激活后，服务器开始处理来自客户的请求。
            System.out.println("激活适配器，服务器等待处理请求...");
            adapter.activate();


            //这个方法挂起发出调用的线程，直到服务器实现终止为止。或我们自己发出一个调用关闭。
            ic.waitForShutdown();
        }
        System.exit(status);
    }
}
