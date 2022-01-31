package org.beable.rpcframework.register;


import org.beable.rpcframework.common.url.URL;

import java.util.List;

/**
 * @author killua
 */
public interface Registry {

    void register(URL url);

    List<URL> lookup(URL condition);

}
