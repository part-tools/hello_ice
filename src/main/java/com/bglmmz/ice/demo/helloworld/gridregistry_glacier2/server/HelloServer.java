package com.bglmmz.ice.demo.helloworld.gridregistry_glacier2.server;

import com.bglmmz.ice.demo.helloworld.impl.HelloServiceImpl;
import com.zeroc.Ice.InitializationData;
import com.zeroc.Ice.Properties;

public class HelloServer {
    public static void main(String[] args) {

        int status = 0;

        //每个task任务启动时，需要把本节点（算力节点,或者数据节点）中的ice服务注册到本组织的IceGridRegistry中

        Properties properties = com.zeroc.Ice.Util.createProperties();
        //和registry.cfg中配置一致

        properties.setProperty("Ice.Default.Locator", "DefaultIceGrid/Locator:tcp -p 4061 -h 192.168.21.26");

        //无需说明端口，不清楚是自动选择还是缺省端口，后续继续研究。
        //此任务的ice服务的adapter名称为：task_id_1_parter_id_1_Adapter
        String taskIceServantAdapterName = "DefaultTCPAdapter";
        String taskIceServantAdapterId = "icChannelAdapter1";

        properties.setProperty(taskIceServantAdapterName + ".Endpoints", "tcp -h 192.168.21.26");

        //此任务的ice服务的adapter ID为：task_id_1_parter_id_1，客户端通过IceGridRegistry寻址时，是通过adapter ID来寻址的。
        properties.setProperty(taskIceServantAdapterName + ".AdapterId", taskIceServantAdapterId);

        InitializationData initializationData = new InitializationData();
        initializationData.properties = properties;


        try (com.zeroc.Ice.Communicator ic = com.zeroc.Ice.Util.initialize(args, initializationData)){
            // 调用Ice.Util.Initialize()初始化Ice run time
            System.out.println("初始化ice run time...");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> ic.destroy()));

            // 创建一个对象适配器，传入适配器名字和在10000端口处接收来的请求
            System.out.println("创建对象适配器，监听端口10001...");

            //adapter的名字可以随意，一般可以用以下模板：$identityID$Adapter，其中$identityID$是服务ID
            //重要的是：地址和端口，表示adapter在此端口上监听，并代理对服务(Servant)的调用。
            //一个端口只能有1个adapter来监听；
            //一个adapter可以代理多个servant实例；同一个servant可以用不同的identityID来加入同一个adapter，然后客户端可以用不同的identityID来调用不同的servant实例。

            com.zeroc.Ice.ObjectAdapter adapter = ic.createObjectAdapter(taskIceServantAdapterName);

            // 实例化一个PrinterI对象，为Printer接口创建一个servant
            System.out.println("为接口创建servant...");
            //Servant 是一个实现了slice中定义的接口，可以认为就是个服务
            com.zeroc.Ice.Object object = new HelloServiceImpl();

            // 调用适配器的add,告诉它有一个新的servant,传递的参数是刚才的servant,这里的“HelloService”是Servant的名字
            //可以注册多个servant到同一个adapter, 只要用servantId identityID来区分不同的servant
            System.out.println("对象适配器加入servant...");
            String servantId = "task_1_party_id_1";
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity(servantId));

            //调用适配器的activate()方法，激活适配器。被激活后，服务器开始处理来自客户的请求。
            System.out.println("激活适配器，服务器等待处理请求...");
            adapter.activate();

            //这个方法挂起发出调用的线程，直到服务器实现终止为止。或我们自己发出一个调用关闭。
            ic.waitForShutdown();

            //从adapter中删除servant
            adapter.remove(com.zeroc.Ice.Util.stringToIdentity(servantId));

            //销毁adapter，回收资源
            adapter.deactivate();
            adapter.destroy();
        }
        System.exit(status);
    }
}
