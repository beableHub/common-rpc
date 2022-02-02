package org.beable.rpcframework.common.core.provider.support;

import lombok.extern.slf4j.Slf4j;
import org.beable.rpcframework.common.core.config.RpcServiceConfig;
import org.beable.rpcframework.common.core.provider.ServiceProvider;
import org.beable.rpcframework.common.extension.ExtensionLoader;
import org.beable.rpcframework.common.url.URL;
import org.beable.rpcframework.common.url.URLKeyConst;
import org.beable.rpcframework.register.Registry;
import org.beable.rpcframework.remoting.transport.netty.NettyRpcServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qing.wu
 */
@Slf4j
public class DefaultServiceProvider implements ServiceProvider {

    private final Map<String, Object> serviceMap;

    private final Set<String> registeredService;

    private final Registry registry;

    private static final DefaultServiceProvider DEFAULT_SERVICE_PROVIDER = new DefaultServiceProvider();

    public DefaultServiceProvider(){
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension("zk");
    }

    @Override
    public Object getService(String serviceName) {
        return serviceMap.get(serviceName);
    }

    @Override
    public void addService(RpcServiceConfig config) {
        String serviceName = config.getServiceName();
        if (registeredService.contains(serviceName)){
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName,config.getService());
        log.info("Add service: {} and interfaces:{}", serviceName, config.getService().getClass().getInterfaces());
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        Map<String,String> params = new TreeMap<>();
        params.put(URLKeyConst.INTERFACE, config.getServiceName());
        params.put(URLKeyConst.VERSION, config.getVersion());
        try {
            this.addService(config);
            String host = InetAddress.getLocalHost().getHostAddress();
            URL url = URL.builder().protocol("beable-rpc").host(host).port(NettyRpcServer.PORT).params(params).build();
            registry.register(url);
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }

    }

    public static DefaultServiceProvider getInstance(){
        return DEFAULT_SERVICE_PROVIDER;
    }
}
