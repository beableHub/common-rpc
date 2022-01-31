package org.beable.rpcframework.common.core.provider.support;

import org.beable.rpcframework.common.core.config.RpcServiceConfig;
import org.beable.rpcframework.common.core.provider.ServiceProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qing.wu
 */
public class DefaultServiceProvider implements ServiceProvider {

    private final Map<String, Object> serviceMap;

    private static final DefaultServiceProvider DEFAULT_SERVICE_PROVIDER = new DefaultServiceProvider();

    public DefaultServiceProvider(){
        serviceMap = new ConcurrentHashMap<>();
    }

    @Override
    public Object getService(String serviceName) {
        return serviceMap.get(serviceName);
    }

    @Override
    public void addService(String serviceName, Object service) {
        serviceMap.putIfAbsent(serviceName,service);
    }

    @Override
    public void publishService(RpcServiceConfig config) {

    }

    public static DefaultServiceProvider getInstance(){
        return DEFAULT_SERVICE_PROVIDER;
    }
}
