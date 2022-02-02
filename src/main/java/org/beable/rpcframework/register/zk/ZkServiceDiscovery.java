package org.beable.rpcframework.register.zk;

import cn.hutool.core.collection.CollectionUtil;
import org.beable.rpcframework.common.extension.ExtensionLoader;
import org.beable.rpcframework.common.url.URL;
import org.beable.rpcframework.common.url.URLKeyConst;
import org.beable.rpcframework.register.Registry;
import org.beable.rpcframework.register.ServiceDiscovery;
import org.beable.rpcframework.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author wuqing
 * @version 1.0
 * @date 2022/2/2
 */
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final Registry registry;

    public ZkServiceDiscovery(){
        this.registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension("zk");
    }

    @Override
    public SocketAddress lookupService(RpcRequest rpcRequest) {
        Map<String,String> params = new TreeMap<>();
        params.put(URLKeyConst.INTERFACE, rpcRequest.getInterfaceName());
        params.put(URLKeyConst.VERSION, rpcRequest.getVersion());
        URL url = URL.builder().protocol("beable-rpc").host("anyhost").params(params).build();
        // 注册中心拿出所有服务的信息
        List<URL> urls = registry.lookup(url);
        if (CollectionUtil.isEmpty(urls)){
            throw new RuntimeException("Not service Providers registered." + params);
        }
        // TODO 暂时先取第一个
        URL first = urls.get(0);
        return new InetSocketAddress(first.getHost(),first.getPort());
    }
}
