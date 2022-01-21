package org.beable.common.rpc.core.proxy;

import lombok.extern.slf4j.Slf4j;
import org.beable.common.rpc.core.config.RpcServiceConfig;
import org.beable.common.rpc.core.provider.ServiceProvider;
import org.beable.common.rpc.core.remoting.dto.RpcRequest;
import org.beable.common.rpc.core.remoting.transport.Client;
import org.beable.common.rpc.extension.ExtensionLoader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author qing.wu
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private final Client client;

    private final RpcServiceConfig config;

    public RpcClientProxy(Client client, RpcServiceConfig config) {
        this.client = client;
        this.config = config;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("invoke method: [{}]",method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(config.getGroup())
                .version(config.getVersion())
                .build();
        return client.send(rpcRequest);
    }
}
