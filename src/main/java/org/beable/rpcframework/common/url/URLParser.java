package org.beable.rpcframework.common.url;

import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author wuqing
 * @version 1.0
 * @date 2022/1/31
 */
@NoArgsConstructor
public class URLParser {

    public static String parseToStr(URL url) {
        StringBuilder buf = new StringBuilder();
        if (StringUtils.isNotBlank(url.getProtocol())) {
            buf.append(url.getProtocol());
            buf.append("://");
        }
        if (StringUtils.isNotBlank(url.getHost())) {
            buf.append(url.getHost());
            if (url.getPort() > 0) {
                buf.append(":");
                buf.append(url.getPort());
            }
        }
        if (StringUtils.isNotBlank(url.getPath())) {
            buf.append("/");
            buf.append(url.getPath());
        }

        buildParams(url, buf);

        return buf.toString();
    }

    private static void buildParams(URL url, StringBuilder buf) {
        Map<String,String> params = url.getParams();
        if (params == null || params.isEmpty()){
            return;
        }
        boolean isFirst = true;
        for (Map.Entry<String,String> param : params.entrySet()){
            if (isFirst) {
                buf.append("?");
            }else {
                buf.append("&");
            }
            buf.append(param.getKey());
            buf.append("=");
            buf.append(param.getValue());
        }
    }

    public static URL toURL(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, String> params = null;
        int i = url.indexOf('?');
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("&");
            if (parts.length > 0) {
                params = new TreeMap<>();
                for (String part : parts) {
                    part = part.trim();
                    if (part.length() == 0) {
                        continue;
                    }
                    int j = part.indexOf('=');
                    if (j <= 0 || j + 1 > part.length() - 1) {
                        continue;
                    }
                    String key = part.substring(0, j);
                    String value = part.substring(j + 1);
                    params.put(key, value);
                }
            }
            url = url.substring(0, i);
        }

        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) {
                throw new IllegalStateException("url missing  protocol: \"" + url + "\"");
            }
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        }

        i = url.indexOf('/');
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }

        i = url.indexOf(':');
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) {
            host = url;
        }
        return URL.builder()
                .protocol(protocol)
                .host(host)
                .port(port)
                .path(path)
                .params(params)
                .build();
    }
}
