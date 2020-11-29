package com.zte.sever;

import com.zte.RpcRequest;
import org.springframework.util.StringUtils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

/**
 * 处理客户端请求
 */
public class ServerProcessorHandler implements Runnable {

    private Socket socket;
    private Map<String, Object> handlerMap;


    public ServerProcessorHandler(Socket socket, Map<String, Object> handlerMap) {
        this.socket = socket;
        this.handlerMap = handlerMap;
    }

    @Override
    public void run() {
        try (
                final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            final RpcRequest request = (RpcRequest) in.readObject();
            out.writeObject(invoke(request));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object invoke(RpcRequest request) throws Exception {
        Object res;
        String serviceName = request.getClassName();
        String version = request.getVersion();
        //增加版本号的判断
        if (!StringUtils.isEmpty(version)) {
            serviceName += "-" + version;
        }
        //得到实现类bean对象
        Object service = handlerMap.get(serviceName);
        if (service == null) {
            throw new RuntimeException("service not found:" + serviceName);
        }
        //拿到客户端请求的参数
        Object[] args = request.getArgs();
        Method method = null;
        Class clazz = Class.forName(request.getClassName());
        if (args != null) {
            //获得每个参数的类型
            Class<?>[] types = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                types[i] = args[i].getClass();
            }
            method = clazz.getMethod(request.getMethodName(), types);
            res = method.invoke(service, request.getArgs());
        } else {
            method = clazz.getMethod(request.getMethodName());
            res = method.invoke(service);
        }
        return res;
    }
}
