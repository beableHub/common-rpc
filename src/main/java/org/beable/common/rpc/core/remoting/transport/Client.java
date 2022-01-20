package org.beable.common.rpc.core.remoting.transport;

import org.beable.common.rpc.core.remoting.dto.RpcRequest;

public interface Client {

    public Object send(RpcRequest rpcRequest);
}
