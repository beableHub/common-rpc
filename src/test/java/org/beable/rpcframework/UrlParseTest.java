package org.beable.rpcframework;

import lombok.extern.slf4j.Slf4j;
import org.beable.rpcframework.common.url.URL;
import org.beable.rpcframework.common.url.URLParser;
import org.junit.Test;

/**
 *
 * @author wuqing
 * @version 1.0
 * @date 2022/1/31
 */
@Slf4j
public class UrlParseTest {

    @Test
    public void test_toURL(){
        String urlStr = "beable://192.168.1.1:21880/userService?interface=org.beable.test.testService&group=default-group&version=1.0";
        URL url = URLParser.toURL(urlStr);
        log.info("fullString:{}",url.toFullString());
    }
}
