package org.beable.rpcframework.remoting.transport;

import org.beable.rpcframework.remoting.dto.RpcRequest;
import org.beable.rpcframework.common.extension.SPI;

@SPI
public interface Client {

    Object send(RpcRequest rpcRequest);
}
