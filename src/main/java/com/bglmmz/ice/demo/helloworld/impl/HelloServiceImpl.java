package com.bglmmz.ice.demo.helloworld.impl;

import com.bglmmz.ice.demo.helloworld.slice.HelloService;
import com.zeroc.Ice.Current;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String s, Current current) {
        System.out.println("Server received a greeting: " + s);
        return "Hello! " + s + ", this is Server.";
    }

    @Override
    public void shutdown(Current current) {
        System.out.println("Server is shutting down, client will be disconnected...");
        current.adapter.getCommunicator().shutdown();
    }
}
