package org.beable.rpcframework.common.core.provider;

import org.beable.rpcframework.common.core.config.RpcServiceConfig;

/**
 * @author qing.wu
 */
public interface ServiceProvider {

    Object getService(String serviceName);

    void addService(RpcServiceConfig config);

    void publishService(RpcServiceConfig config);
}
