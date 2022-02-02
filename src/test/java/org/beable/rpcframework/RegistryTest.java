package org.beable.rpcframework;

import lombok.extern.slf4j.Slf4j;
import org.beable.rpcframework.common.url.URL;
import org.beable.rpcframework.common.url.URLParser;
import org.beable.rpcframework.register.Registry;
import org.beable.rpcframework.register.zk.ZkRegistry;
import org.junit.Test;

import java.util.List;


/**
 *
 * @author wuqing
 * @version 1.0
 * @date 2022/2/1
 */
@Slf4j
public class RegistryTest {

    @Test
    public void test_register(){
        String zkUrlStr = "zk://192.168.32.128:2181";
        URL zkUrl = URLParser.toURL(zkUrlStr);
        log.info("fullString:{}",zkUrl.toFullString());
        Registry registry = new ZkRegistry(zkUrl);

        String serviceUrlStr = "beable://localhost:21880/default-group?interface=org.beable.test.testService";
        // zk node-> /beable-rpc/org.beable.test.testService/192.168.5.128:9988
        URL serviceUrl = URLParser.toURL(serviceUrlStr);
        log.info("fullString:{}",serviceUrl.toFullString());
//        registry.register(serviceUrl);

        registry.lookup(serviceUrl);
    }
}
