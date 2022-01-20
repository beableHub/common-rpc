package org.beable.common.rpc;

import lombok.extern.slf4j.Slf4j;
import org.beable.common.rpc.core.provider.zk.ZkServiceProvider;
import org.beable.common.rpc.core.remoting.dto.RpcRequest;
import org.beable.common.rpc.core.remoting.dto.RpcResponse;
import org.beable.common.rpc.core.remoting.transport.socket.SocketServer;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

@Slf4j
public class RpcTest {

    @Test
    public void test_send(){
        InetSocketAddress givenInetSocketAddress = new InetSocketAddress("10.118.32.165", 9988);

        final Method method = TestService.class.getMethods()[0];

        RpcRequest rpcRequest = RpcRequest.builder()
                .parameters(new Object[]{"Owner"})
                .interfaceName("org.beable.common.rpc.TestService")
                .methodName("add")
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group("default-group")
                .version("1.0")
                .build();
        try(Socket socket = new Socket()){
            socket.connect(givenInetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            RpcResponse response = (RpcResponse) objectInputStream.readObject();
            log.info("response:{}",response);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test_server(){
        ZkServiceProvider zkServiceProvider = ZkServiceProvider.getInstance();
        TestService testService = new TestServiceImpl();
        zkServiceProvider.addService("org.beable.common.rpc.TestService#default-group#1.0",testService);
        SocketServer server = new SocketServer();
        server.start();
    }
}
