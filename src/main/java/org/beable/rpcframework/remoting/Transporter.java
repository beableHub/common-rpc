package org.beable.rpcframework.remoting;

import org.beable.rpcframework.remoting.transport.Client;

/**
 * @author qing.wu
 */
public interface Transporter {

    Client connect(String url) throws RemotingException;
}
