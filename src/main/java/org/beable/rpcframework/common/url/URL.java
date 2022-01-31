package org.beable.rpcframework.common.url;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 *
 * @author wuqing
 * @version 1.0
 * @date 2022/1/31
 */
@Builder
@Getter
public class URL {

    private final String protocol;

    private final String host;

    private final int port;

    private final String path;

    private final Map<String,String> params;

    private String fullString;

    public String getAddress(){
        return host + ":" + port;
    }

    public String getParam(String key,String defaultValue){
        if (params == null){
            return defaultValue;
        }
        return params.getOrDefault(key,defaultValue);
    }

    public int getIntParam(String key,int defaultValue){
        if (params == null || params.isEmpty()){
            return defaultValue;
        }
        String value = params.get(key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public String toFullString() {
        if (fullString != null) {
            return fullString;
        }
        return fullString = URLParser.parseToStr(this);
    }

    public static URL valueOf(String url){
        return URLParser.toURL(url);
    }
}
