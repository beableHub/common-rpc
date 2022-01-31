package org.beable.rpcframework;

import lombok.extern.slf4j.Slf4j;
import org.beable.rpcframework.common.core.config.RpcServiceConfig;
import org.beable.rpcframework.common.core.provider.support.DefaultServiceProvider;
import org.beable.rpcframework.common.core.proxy.RpcClientProxy;
import org.beable.rpcframework.remoting.dto.RpcRequest;
import org.beable.rpcframework.remoting.dto.RpcResponse;
import org.beable.rpcframework.remoting.transport.Client;
import org.beable.rpcframework.remoting.transport.netty.NettyRpcClient;
import org.beable.rpcframework.remoting.transport.netty.NettyRpcServer;
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
    public void test_proxy(){
        RpcServiceConfig config = new RpcServiceConfig();
        TestService testService = new TestServiceImpl();
        final Method method = TestService.class.getMethods()[0];
        config.setService(testService);
        Client client = new NettyRpcClient();
        RpcClientProxy proxy = new RpcClientProxy(client,config);
        Object result = proxy.invoke(testService, method, new Object[]{"test_proxy"});
        log.info("result:{}",result);
    }


    @Test
    public void test_server(){
        DefaultServiceProvider defaultServiceProvider = DefaultServiceProvider.getInstance();
        TestService testService = new TestServiceImpl();
        defaultServiceProvider.addService("org.beable.common.rpc.TestService#default-group#1.0",testService);
        NettyRpcServer server = new NettyRpcServer();
        server.start();
    }
}
