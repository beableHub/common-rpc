package org.beable.common.rpc.core.remoting.transport.socket;

import lombok.extern.slf4j.Slf4j;
import org.beable.common.rpc.core.remoting.dto.RpcRequest;
import org.beable.common.rpc.core.remoting.dto.RpcResponse;
import org.beable.common.rpc.core.remoting.transport.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


/**
 * @author qing.wu
 */
@Slf4j
public class SocketClient implements Client {

    public SocketClient(){

    }

    @Override
    public Object send(RpcRequest rpcRequest) {
        // TODO
        SocketAddress inetSocketAddress = new InetSocketAddress("10.118.32.165", 9988);
        try(Socket socket = new Socket()){
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            RpcResponse response = (RpcResponse) objectInputStream.readObject();
            log.info("response:{}",response);
            return response.getData();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
