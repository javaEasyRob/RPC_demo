package com.zte.client;

import com.zte.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 客户端发送请求
 */
class RpcClient {

    static Object send(RpcRequest rpcRequest, String host, int port) {
        Object res = null;
        //try with ,实现了Closeable的都可以在执行玩方法体后自动关闭，会处理异常
        try (
                Socket socket = new Socket(host, port);
                final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            out.writeObject(rpcRequest);
            out.flush();

            res = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }
}
