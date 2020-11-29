package com.zte.sever;

import com.zte.annotation.RpcServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 开启SocketServer，接受客户端请求，并将接口对应的实现类对象注入到容器
 */
@PropertySource("classpath:rpc.properties")
@Component
public class RpcServer implements ApplicationContextAware, InitializingBean {

    //推荐自定参数创建
    ExecutorService executorService = Executors.newCachedThreadPool();

    private Map<String, Object> handlerMap = new HashMap();

    @Value("${rpc.host:127.0.0.1}")
    private String host;
    @Value("${rpc.port:6666}")
    private int port;

    public RpcServer() {
    }

    public RpcServer(ExecutorService executorService, Map<String, Object> handlerMap) {
        this.executorService = executorService;
        this.handlerMap = handlerMap;
    }

    public RpcServer(int port) {
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("server端启动了");
            while (true) {//不断接受请求
                Socket socket = serverSocket.accept();//BIO
                //每一个socket 交给一个processorHandler来处理
                executorService.execute(new ServerProcessorHandler(socket, handlerMap));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcServiceImpl.class);
        if (!serviceBeanMap.isEmpty()) {
            for (Object servcieBean : serviceBeanMap.values()) {
                //拿到注解
                RpcServiceImpl rpcService = servcieBean.getClass().getAnnotation((RpcServiceImpl.class));
                String serviceName = rpcService.value().getName();//拿到接口类定义
                String version = rpcService.version(); //拿到版本号
                if (!StringUtils.isEmpty(version)) {
                    serviceName += "-" + version;
                }
                handlerMap.put(serviceName, servcieBean);
            }
        }
    }
}
