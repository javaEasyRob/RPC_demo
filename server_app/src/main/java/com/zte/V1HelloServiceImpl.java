package com.zte;

import com.zte.annotation.RpcServiceImpl;

@RpcServiceImpl(value = IHelloService.class, version = "v1.0")
public class V1HelloServiceImpl implements IHelloService {

    @Override
    public String sayHello(String content) {
        System.out.println("===============server:开始执行远程方法sayHello【v1.0】===============");
        System.out.println(content);
        System.out.println("===============server:远程方法sayHello【v1.0】执行完毕===============");
        return "恭喜你,rpc通信完成了！这是我返回给你的结果";
    }
}
