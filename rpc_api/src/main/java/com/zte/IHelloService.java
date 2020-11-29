package com.zte;

import com.zte.annotation.RpcServiceAPI;

@RpcServiceAPI
public interface IHelloService {
    String sayHello(String content);
}
