package com.zte;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceTest {
    @Autowired
    IHelloService helloService;

    public String hello(String content) {
        System.out.println("===========client:开始调用远程方法===============");
        String res = helloService.sayHello(content);
        System.out.println("===========client:开始调用远程结束===============");
        return res;
    }
}
