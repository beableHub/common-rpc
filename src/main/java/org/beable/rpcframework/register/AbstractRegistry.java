package org.beable.rpcframework.register;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.beable.rpcframework.common.url.URL;
import org.beable.rpcframework.common.url.URLKeyConst;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author wuqing
 * @version 1.0
 * @date 2022/1/31
 */
@Slf4j
public abstract class AbstractRegistry implements Registry{

    /**
     * 已注册的服务的本地缓存 {serviceName,[URL]}
     */
    private final Map<String, Set<String>> registered = new ConcurrentHashMap<>();



    @Override
    public void register(URL url) {
        Assert.notNull(url, "register url == null");
        doRegister(url);
        // 添加到本地缓存
        addToLocalCache(url);

    }

    private void addToLocalCache(URL url) {
        String serviceName = getServiceNameFromURL(url);
        if (!registered.containsKey(serviceName)) {
            registered.put(serviceName, new ConcurrentHashSet<>());
        }
        registered.get(serviceName).add(url.toString());
    }

    private String getServiceNameFromURL(URL url) {
        return url.getParam(URLKeyConst.INTERFACE,url.getPath());
    }

    /**
     * 向注册中心注册服务
     */
    protected abstract void doRegister(URL url);



    @Override
    public List<URL> lookup(URL condition) {
        return null;
    }
}
