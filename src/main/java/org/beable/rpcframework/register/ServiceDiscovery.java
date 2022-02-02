package org.beable.rpcframework.register;

import org.beable.rpcframework.common.extension.SPI;
import org.beable.rpcframework.remoting.dto.RpcRequest;

import java.net.SocketAddress;

/**
 *
 * @author wuqing
 * @version 1.0
 * @date 2022/2/2
 */
@SPI
public interface ServiceDiscovery {

    SocketAddress lookupService(RpcRequest rpcRequest);
}
