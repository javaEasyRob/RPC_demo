package com.zte.client;

import com.zte.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * api接口的实际处理者
 */
public class RpcHandler implements InvocationHandler {
    private String host;
    private int port;
    private String version;

    public RpcHandler(String host, int port, String version) {
        this.host = host;
        this.port = port;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setArgs(args);
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setVersion(version);
        return RpcClient.send(rpcRequest, host, port);
    }
}
