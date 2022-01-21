package org.beable.common.rpc.core.remoting.transport;

import org.beable.common.rpc.core.remoting.dto.RpcRequest;
import org.beable.common.rpc.extension.SPI;

@SPI
public interface Client {

    Object send(RpcRequest rpcRequest);
}
