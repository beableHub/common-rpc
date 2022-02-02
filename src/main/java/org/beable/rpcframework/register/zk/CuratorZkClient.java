package org.beable.rpcframework.register.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.beable.rpcframework.common.url.URL;
import org.beable.rpcframework.common.url.URLKeyConst;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wuqing
 * @version 1.0
 * @date 2022/1/31
 */
@Slf4j
public class CuratorZkClient {


    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 5000;

    private static final int DEFAULT_SESSION_TIMEOUT_MS = 60000;

    private static final String SESSION_TIMEOUT_KEY = "zk.sessionTimeoutMS";

    private static final int RETRY_TIMES = 3;

    private static final int RETRY_SLEEP_MS = 1000;

    private static final String ROOT_PATH = "/beable-rpc";

    private final CuratorFramework client;

    private static final Map<String, CuratorCache> LISTENER_MAP = new ConcurrentHashMap<>();

    public CuratorZkClient(URL url){

        int timeout = url.getIntParam(URLKeyConst.TIMEOUT,DEFAULT_CONNECTION_TIMEOUT_MS);
        int sessionTimeout = url.getIntParam(SESSION_TIMEOUT_KEY,DEFAULT_SESSION_TIMEOUT_MS);

        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
            .connectString(url.getAddress())
            .retryPolicy(new RetryNTimes(RETRY_TIMES,RETRY_SLEEP_MS))
            .connectionTimeoutMs(timeout)
            .sessionTimeoutMs(sessionTimeout);
        client = builder.build();
        client.start();
        try {
            client.blockUntilConnected(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Time out waiting to connect to zookeeper! Please check the zookeeper config.");
        }
    }


    public void createNode(String path, CreateMode createMode) {
        try {
            client.create().creatingParentsIfNeeded().withMode(createMode).forPath(buildPath(path));
        } catch (KeeperException.NodeExistsException e) {
            log.warn("ZNode " + path + " already exists.");
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private String buildPath(String path) {
        if (path.startsWith(ROOT_PATH)){
            return path;
        }
        if (path.startsWith("/")){
            return ROOT_PATH + path;
        }
        return ROOT_PATH + "/" + path;
    }

    public void createEphemeralNode(String path){
        log.info("create path:{}",path);
        createNode(path,CreateMode.EPHEMERAL);
    }

    public void createPersistentNode(String path){
        createNode(path,CreateMode.PERSISTENT);
    }

    public List<String> getChildren(String path) {
        try{
            return client.getChildren().forPath(buildPath(path));
        }catch (KeeperException.NoNodeException e) {
            return Collections.emptyList();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void addListener(String path, CuratorCacheListener listener){
        String fullPath = buildPath(path);
        if (LISTENER_MAP.containsKey(fullPath)){
            return;
        }
        CuratorCache curatorCache = CuratorCache.build(client,fullPath);
        LISTENER_MAP.put(fullPath,curatorCache);
        curatorCache.listenable().addListener(listener);
        curatorCache.start();
    }
}
