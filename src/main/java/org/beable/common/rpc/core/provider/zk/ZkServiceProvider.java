package org.beable.common.rpc.core.provider.zk;

import org.beable.common.rpc.core.provider.ServiceProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Administrator
 */
public class ZkServiceProvider implements ServiceProvider {

    private final Map<String, Object> serviceMap;

    private static final ZkServiceProvider zkServiceProvider = new ZkServiceProvider();

    public ZkServiceProvider(){
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

    public static ZkServiceProvider getInstance(){
        return zkServiceProvider;
    }
}
