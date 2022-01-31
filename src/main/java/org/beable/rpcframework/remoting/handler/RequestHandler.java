package org.beable.rpcframework.remoting.handler;

import lombok.extern.slf4j.Slf4j;
import org.beable.rpcframework.common.core.provider.ServiceProvider;
import org.beable.rpcframework.common.core.provider.support.DefaultServiceProvider;
import org.beable.rpcframework.remoting.dto.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author qing.wu
 */
@Slf4j
public class RequestHandler {

    public final ServiceProvider serviceProvider = DefaultServiceProvider.getInstance();

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
