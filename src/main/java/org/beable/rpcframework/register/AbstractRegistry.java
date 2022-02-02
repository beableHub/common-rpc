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
import java.util.stream.Collectors;

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
        log.info("register: {}", url.toFullString());

    }

    private void addToLocalCache(URL url) {
        String serviceName = getServiceNameFromURL(url);
        if (!registered.containsKey(serviceName)) {
            registered.put(serviceName, new ConcurrentHashSet<>());
        }
        registered.get(serviceName).add(url.toString());
    }

    public String getServiceNameFromURL(URL url) {
        return url.getParam(URLKeyConst.INTERFACE,url.getPath());
    }

    /**
     * 向注册中心注册服务
     */
    protected abstract void doRegister(URL url);



    @Override
    public List<URL> lookup(URL condition) {
        String serviceName = getServiceNameFromURL(condition);
        if (registered.containsKey(serviceName)){
            return registered.get(serviceName).stream().map(URL::valueOf).collect(Collectors.toList());
        }
        List<URL> urls = reset(condition);
        log.info("lookup:{}",urls);
        return urls;
    }

    public List<URL> reset(URL condition) {
        String serviceName = getServiceNameFromURL(condition);
        registered.remove(serviceName);
        List<URL> urls = doLookup(condition);
        for (URL url : urls){
            addToLocalCache(url);
        }
        log.info("reset:{}",urls);
        return urls;
    }

    protected abstract List<URL> doLookup(URL condition);
}
