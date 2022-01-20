package org.beable.common.rpc.core.remoting;

import org.beable.common.rpc.core.remoting.transport.Client;

/**
 * @author qing.wu
 */
public interface Transporter {

    Client connect(String url) throws RemotingException;
}
