package org.beable.rpcframework.register.zk;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.net.URLEncoder;
import lombok.extern.slf4j.Slf4j;
import org.beable.rpcframework.common.url.URL;
import org.beable.rpcframework.common.url.URLParser;
import org.beable.rpcframework.register.AbstractRegistry;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author wuqing
 * @version 1.0
 * @date 2022/2/1
 */
@Slf4j
public class ZkRegistry extends AbstractRegistry {

    private final CuratorZkClient zkClient;

    private static final URLEncoder urlEncoder = URLEncoder.createPathSegment();

    private static final Charset charset = Charset.defaultCharset();

    public ZkRegistry() {
        String urlStr = "zk://192.168.32.128:2181";
        URL url = URL.valueOf(urlStr);
        zkClient = new CuratorZkClient(url);
    }

    public ZkRegistry(URL url) {

        zkClient = new CuratorZkClient(url);
    }

    @Override
    protected void doRegister(URL url) {
        zkClient.createEphemeralNode(toUrlPath(url));
    }

    @Override
    protected List<URL> doLookup(URL condition) {
        List<String> children = zkClient.getChildren(toServicePath(condition));
        List<URL> urls = children.stream()
                .map(s -> URLParser.toURL(URLDecoder.decode(s,charset)))
                .collect(Collectors.toList());
        for (URL url: urls){
            watch(url);
        }
        return urls;
    }

    private void watch(URL url) {
        String path = toServicePath(url);
        zkClient.addListener(path,(type, oldData, data) ->{
            log.info("watch event. type={}, oldData={}, data={}", type, oldData, data);
            reset(url);
        });
    }

    private String toUrlPath(URL url) {
        // eg /beable-rpc/org.beable.test.userService/beable-rpc%3A%2F%2F192.168.32.1%3A9988%3Finterface=org.beable.rpcframework.TestService%3Fversion=1.0
        return toServicePath(url) + "/" + urlEncoder.encode(url.toFullString(), charset);
    }

    private String toServicePath(URL url) {
        return getServiceNameFromURL(url);
    }
}
