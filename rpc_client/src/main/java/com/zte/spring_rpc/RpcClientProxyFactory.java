package com.zte.spring_rpc;

import com.zte.client.RpcHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * 接口示例工厂，提供api接口的代理类
 */
@PropertySource("classpath:rpc.properties")
public class RpcClientProxyFactory<T> implements FactoryBean<T> {
    @Value("${rpc.host:127.0.0.1}")
    private String host;
    @Value("${rpc.port:6666}")
    private int port;
    @Value("${rpc.version:v1.0}")
    private String version;
    private Class<T> interfaceCls;

    public RpcClientProxyFactory(Class<T> interfaceCls) {
        this.interfaceCls = interfaceCls;
    }

    public Class<T> getInterfaceCls() {
        return interfaceCls;
    }


    public void setInterfaceCls(Class<T> interfaceCls) {
        this.interfaceCls = interfaceCls;
    }

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(interfaceCls.getClassLoader(),
                new Class[]{interfaceCls}, new RpcHandler(host, port, version));
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceCls;
    }
}
