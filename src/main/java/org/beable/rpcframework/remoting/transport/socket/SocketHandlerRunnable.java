package org.beable.rpcframework.remoting.transport.socket;

import lombok.extern.slf4j.Slf4j;
import org.beable.rpcframework.remoting.dto.RpcRequest;
import org.beable.rpcframework.remoting.dto.RpcResponse;
import org.beable.rpcframework.remoting.handler.RequestHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * @author qing.wu
 */
@Slf4j
public class SocketHandlerRunnable implements Runnable{

    private final Socket socket;

    private final RequestHandler requestHandler;

    public SocketHandlerRunnable(Socket socket){
        this.socket = socket;
        this.requestHandler = new RequestHandler();
    }


    @Override
    public void run() {
        log.info("server handle message from client by thread: [{}]", Thread.currentThread().getName());
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())){
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object result = requestHandler.handle(rpcRequest);
            RpcResponse response = RpcResponse.success(result, UUID.randomUUID().toString());
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception:", e);
        }
    }
}
