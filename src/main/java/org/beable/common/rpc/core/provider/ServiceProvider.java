package org.beable.common.rpc.core.provider;

/**
 * @author qing.wu
 */
public interface ServiceProvider {

    Object getService(String serviceName);

    void addService(String serviceName,Object service);
}
