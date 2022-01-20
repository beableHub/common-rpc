package org.beable.common.rpc.core.remoting.handler;

import lombok.extern.slf4j.Slf4j;
import org.beable.common.rpc.core.provider.ServiceProvider;
import org.beable.common.rpc.core.provider.zk.ZkServiceProvider;
import org.beable.common.rpc.core.remoting.dto.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author qing.wu
 */
@Slf4j
public class RequestHandler {

    public final ServiceProvider serviceProvider = ZkServiceProvider.getInstance();

    public RequestHandler(){

    }

    public Object handle(RpcRequest rpcRequest){
        // 读取服务
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        // 执行
        return invokeTargetMethod(rpcRequest,service);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service){
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service,rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }
}
