package org.beable.common.rpc.core.provider;

import org.beable.common.rpc.core.config.RpcServiceConfig;

/**
 * @author qing.wu
 */
public interface ServiceProvider {

    Object getService(String serviceName);

    void addService(String serviceName,Object service);

    void publishService(RpcServiceConfig config);
}
